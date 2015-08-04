package smartgrids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import smartgrids.message.InfoRequest;
import smartgrids.message.InfoResponse;
import smartgrids.message.ValueReport;
import smartgrids.message.MonitorReport;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.UntypedActor;

public class Mailer extends UntypedActor 
{
	private Agent agent;
	
	//private boolean valChanged;
	
	private boolean mailerActive;   // will be used to monitor termination conditions, determine when monitor should stop agents
	
	private String monitorPath = "akka.tcp://monitorSystem@127.0.0.1:2550/user/monitor";
	
	
	public Mailer(Identifier id, HashMap<String, Variable<Integer>> variables, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors)
	{
		agent = new Agent(id, variables, constraints, neighbors, this);
		mailerActive = true;
	}
	
	
	private void sendIdentifyRequests()
	{
		System.out.println("Reporting to monitor");
		getContext().actorSelection(monitorPath).tell(getSelf(), getSelf());
		
		for (Identifier neighbor : agent.getNeighbors().values())
		{
			if (neighbor.refSet()) continue;
			String path = "akka.tcp://" + neighbor.getName() + "System@" + neighbor + "/user/" + neighbor.getName();
			System.out.println(agent.getId().getName() + " is trying to connect with " + path);
			getContext().actorSelection(path).tell(new Identify(path), getSelf());
		}
	}


	@Override
	public void onReceive(Object message)
	{
		if (message instanceof String)
		{
			if (((String)message).equals("identify"))
			{
				System.out.println("Agent " + agent.getId().getName() + " received " + (String)message);
				long lastTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - lastTime < 2000);
				sendIdentifyRequests();
			}
			else if (((String)message).equals("report"))
			{
				//System.err.println("received report message from monitor");
				ArrayList<Boolean> varsActive = new ArrayList<Boolean>();
				for (Variable<?> v : agent.getVariables().values())
				{
					varsActive.add(v.getSet());
				}
				getSender().tell(new MonitorReport(mailerActive, varsActive), getSelf());
			}	
		}
		else if (message instanceof ActorIdentity)
		{
			ActorRef responder = ((ActorIdentity)message).getRef();
			
			if (responder != null)
			{
				System.out.println(agent.getId().getName() + " received actor identity");
				responder.tell(new InfoRequest(), getSelf());
			}
		}
		else if (message instanceof InfoRequest)
		{
			System.out.println(agent.getId().getName() + " received info request");
			getSender().tell(new InfoResponse(agent.getId().getName()), getSelf());
		}
		else if (message instanceof InfoResponse)
		{
			String name = ((InfoResponse)message).name;
			agent.getNeighbors().get(name).setActorRef(getSender());
			
			System.out.println(agent.getId().getName() + " received a response from " + name + "!");
			
			boolean filledOut = true;
			
			// check if neighbors filled out
			for (Identifier id : agent.getNeighbors().values())
			{
				if (!id.refSet())
				{
					filledOut = false;
					break;
				}
			}
			
			if (filledOut)
			{
				// set up actual variable objects in constraint
				for (Constraint constraint : agent.getConstraints().values())
				{
					constraint.setupVars(agent.getId(), agent.getVariables(), agent.getNeighbors());
				}
				
				// wait 2 seconds to make sure all agents' constraints are set up
				long lastTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - lastTime < 2000);
				
				ArrayList<String> sentVars = new ArrayList<>();
				
				// initial value report
				for (Constraint constraint : agent.getConstraints().values())
				{
					Collection<Variable<Integer>> ourVars = constraint.getOurVars().values();
					Collection<Variable<Integer>> theirVars = constraint.getTheirVars().values();
					
					for (Variable<Integer> theirVar : theirVars)
					{
						String ownerName = theirVar.getOwner().getName();
						
						ActorRef ownerRef = theirVar.getOwner().getActorRef();
						
						for (Variable<Integer> ourVar : ourVars)
						{
							//if (ourVar.valChanged())
							if (!sentVars.contains(ourVar.getName() + ":" + theirVar.getOwner().getName()))
							{
								System.err.println("sending " + ourVar.getName() + " to " + ownerName);
								ownerRef.tell(new ValueReport(agent.getId().getName(), ourVar.getName(), ourVar.getValue()), getSelf());
								ourVar.reset();
								
								sentVars.add(ourVar.getName() + ":" + theirVar.getOwner().getName());
							}
						}
					}
				}
			}
		}
		else  if (message instanceof ValueReport)
		{
			ValueReport vr = (ValueReport)message;
			String varKey = vr.ownerName + ":" + vr.varName;
			
			System.out.println("received a value report from " + vr.ownerName + " - " + vr.varName);
			
			for (Constraint c : agent.getConstraints().values())
			{
				Variable<Integer> theirVar = c.getTheirVars().get(varKey);
				
				if (theirVar == null || theirVar.set)
				{
					continue;
				}
				
				//vr.var.set = true;
				
				System.err.print(c.getName() + " - " + varKey + "  " + theirVar.getValue() + " -> ");
				//c.getTheirVars().put(varKey, vr.var); //overwrite previous value
				theirVar.setVal(vr.value);
				theirVar.set = true;
				System.err.print(c.getTheirVars().get(varKey).getValue());
				System.err.println();
			}
			
			// check if all variables have come in for this iteration
			boolean allVarsSet = true;
			
			for (Constraint c : agent.getConstraints().values())
			{
				for (Variable<?> var : c.getTheirVars().values())
				{
					if (!var.set)
					{
						allVarsSet = false;
						break;
					}
				}
				
				if (!allVarsSet) break;
			}
			
			// all vars are set, change vars to improve costs
			if (allVarsSet)
			{
				System.out.println("ALL VARS SET");
				
				for (Variable<Integer> var : agent.getVariables().values())
				{
					int bestCost = 0;
					for (Constraint constraint : var.getConstraints())
					{
						bestCost += constraint.calcCost();
					}
					int oldCost = bestCost;
					
					int bestVal = var.getValue();
					int oldVal = bestVal;
					
					ArrayList<Integer> domainValues = var.getDomain().getValues();
					
					for (int i = 0; i < domainValues.size(); i++)
					{
						var.setVal(domainValues.get(i));
						
						int currentCost = 0;
						for (Constraint constraint : var.getConstraints())
						{
							currentCost += constraint.calcCost();
						}
						
						if (currentCost < bestCost)
						{
							bestCost = currentCost;
							bestVal = domainValues.get(i);
						}
					}
					
					System.out.println("  " + var.getName() + " - " + oldVal + ", " + oldCost + " -> " + bestVal + ", " + bestCost);
					
					var.setVal(bestVal);
				}
				
				// unset vars for next iteration
				for (Constraint c : agent.getConstraints().values())
				{
					System.out.println("  " + c.getName() + " cost: " + c.calcCost());
					
					for (Variable<?> var : c.getTheirVars().values())
					{
						var.set = false;
					}
				}
			}
		}
		else
		{
			unhandled(message);
		}
	}
}

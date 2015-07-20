package smartgrids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import smartgrids.message.InfoRequest;
import smartgrids.message.InfoResponse;
import smartgrids.message.ValueReport;
import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.UntypedActor;

public class Agent extends UntypedActor 
{
	private Identifier id;
	
	//private HashMap<String, Domain<?>> domains = new HashMap<>();
	private HashMap<String, Variable<?>> variables = new HashMap<>();
	//private HashMap<String, Relation> relations = new HashMap<>();
	private HashMap<String, Constraint> constraints = new HashMap<>();
	
	private HashMap<String, Identifier> neighbors = new HashMap<>();
	
	private String monitorPath = "akka.tcp://monitorSystem@127.0.0.1:2550/user/monitor";
	
	//private boolean valChanged;
	
	
	public Agent(Identifier id, HashMap<String, Domain<?>> domains, HashMap<String, Variable<?>> variables, HashMap<String, Relation> relations, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors)
	{
		this.id = id;
		
		//this.domains = domains;
		this.variables = variables;
		//this.relations = relations;
		this.constraints = constraints;
		
		this.neighbors = neighbors;
		
		//this.valChanged = true;
		
		System.out.println("\nAgent " + id.getName() + " alive\n");
		
		System.err.println("Variables:");
		for (Variable<?> v : variables.values())
		{
			v.setOwner(id);
			
			System.err.print("    " + v.getName() + ": " + v.getValue() + ", " + v.getDomain().getName() + ": ");
			for (Object value : v.getDomain().getValues())
			{
				System.err.print((int)value + " ");
			}
			System.err.println();
		}
		System.err.println();
	}
	
	
	private void sendIdentifyRequests()
	{
		System.out.println("Reporting to monitor");
		getContext().actorSelection(monitorPath).tell(getSelf(), getSelf());
		
		for (Identifier neighbor : neighbors.values())
		{
			if (neighbor.refSet()) continue;
			String path = "akka.tcp://" + neighbor.getName() + "System@" + neighbor + "/user/" + neighbor.getName();
			System.out.println(id.getName() + " is trying to connect with " + path);
			getContext().actorSelection(path).tell(new Identify(path), getSelf());
		}
	}


	@Override
	public void onReceive(Object message)
	{
		if (message instanceof String)
		{
			System.out.println("Agent " + id.getName() + " received " + (String)message);
			if (((String)message).equals("identify"))
			{
				long lastTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - lastTime < 2000);
				sendIdentifyRequests();
			}
			else if (((String)message).equals("report"))
			{
				System.err.println("received report message from monitor");
			}	

		}
		else if (message instanceof ActorIdentity)
		{
			ActorRef responder = ((ActorIdentity)message).getRef();
			
			if (responder != null)
			{
				System.out.println(id.getName() + " received actor identity");
				responder.tell(new InfoRequest(), getSelf());
			}
		}
		else if (message instanceof InfoRequest)
		{
			System.out.println(id.getName() + " received info request");
			getSender().tell(new InfoResponse(id.getName()), getSelf());
		}
		else if (message instanceof InfoResponse)
		{
			String name = ((InfoResponse)message).name;
			neighbors.get(name).setActorRef(getSender());
			
			System.out.println(id.getName() + " received a response from " + name + "!");
			
			boolean filledOut = true;
			
			// check if neighbors filled out
			for (Identifier id : neighbors.values())
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
				for (Constraint constraint : constraints.values())
				{
					constraint.setupVars(id, variables, neighbors);
				}
				
				// wait 2 seconds to make sure all agents' constraints are set up
				long lastTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - lastTime < 2000);
				
				ArrayList<String> sentVars = new ArrayList<>();
				
				// initial value report
				for (Constraint constraint : constraints.values())
				{
					Collection<Variable<?>> ourVars = constraint.getOurVars().values();
					Collection<Variable<?>> theirVars = constraint.getTheirVars().values();
					
					for (Variable<?> theirVar : theirVars)
					{
						String ownerName = theirVar.getOwner().getName();
						
						ActorRef ownerRef = theirVar.getOwner().getActorRef();
						
						for (Variable<?> ourVar : ourVars)
						{
							//if (ourVar.valChanged())
							if (!sentVars.contains(ourVar.getName() + ":" + theirVar.getOwner().getName()))
							{
								System.err.println("sending " + ourVar.getName() + " to " + ownerName);
								ownerRef.tell(new ValueReport(id.getName(), ourVar), getSelf());
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
			String varKey = vr.name + ":" + vr.var.getName();
			
			System.out.println("received a value report from " + vr.name + " - " + vr.var.getName());
			
			for (Constraint c : constraints.values())
			{
				Variable<?> theirVar = c.getTheirVars().get(varKey);
				
				if (theirVar == null || theirVar.set)
				{
					continue;
				}
				
				vr.var.set = true;
				
				System.err.print(c.getName() + " - " + varKey + "  " + theirVar.getValue() + " -> ");
				c.getTheirVars().put(varKey, vr.var); //overwrite previous value
				System.err.print(c.getTheirVars().get(varKey).getValue());
				System.err.println();
			}
			
			// check if all variables have come in for this iteration
			boolean allVarsSet = true;
			
			for (Constraint c : constraints.values())
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
				System.out.println("ALL VARS SET YAAAAAYYY");
				
				//TODO: cost stuff
				
				// unset vars for next iteration
				for (Constraint c : constraints.values())
				{
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

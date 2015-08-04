package smartgrids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import akka.actor.ActorRef;
import smartgrids.message.ValueReport;

public class Agent{

	private Identifier id;
	private HashMap<String, Variable<Integer>> variables = new HashMap<>();
	private HashMap<String, Constraint> constraints = new HashMap<>();
	private HashMap<String, Identifier> neighbors = new HashMap<>();
	private Mailer mailer;
	
	private ActorRef self;
	
	private boolean agentActive;   // will be used to monitor termination conditions, determine when monitor should stop agents 
	
	private String monitorPath = "akka.tcp://monitorSystem@127.0.0.1:2550/user/monitor";
	
	
	public Agent(Identifier id, HashMap<String, Variable<Integer>> variables, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors, Mailer mailer, ActorRef self)
	{
		this.id = id;
		this.variables = variables;
		this.constraints = constraints;
		this.neighbors = neighbors;
		this.mailer = mailer;
		this.self = self;
		
		agentActive = true;
		
		System.out.println("\nAgent " + id.getName() + " is alive\n");
		
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
	
	
	public void infoResponse(String name, ActorRef sender)
	{
		neighbors.get(name).setActorRef(sender);
		
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
			
			// initial value report
			sendVars();
		}
	}
	
	public void valueReport(String ownerName, String varName, int value)
	{
		String varKey = ownerName + ":" + varName;
		
		System.out.println("received a value report from " + ownerName + " - " + varName);
		
		for (Constraint c : constraints.values())
		{
			Variable<Integer> theirVar = c.getTheirVars().get(varKey);
			
			if (theirVar == null || theirVar.set)
			{
				continue;
			}
			
			System.err.print(c.getName() + " - " + varKey + "  " + theirVar.getValue() + " -> ");
			
			theirVar.setVal(value);
			theirVar.set = true;
			
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
			System.out.println("ALL VARS SET");
			
			for (Variable<Integer> var : variables.values())
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
			for (Constraint c : constraints.values())
			{
				System.out.println("  " + c.getName() + " cost: " + c.calcCost());
				
				for (Variable<?> var : c.getTheirVars().values())
				{
					var.set = false;
				}
			}
		}
	}
	
	
	private void sendVars()
	{
		ArrayList<String> sentVars = new ArrayList<>();
		
		for (Constraint constraint : constraints.values())
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
						ownerRef.tell(new ValueReport(id.getName(), ourVar.getName(), ourVar.getValue()), self);
						ourVar.reset();
						
						sentVars.add(ourVar.getName() + ":" + theirVar.getOwner().getName());
					}
				}
			}
		}
	}
	
	
	public HashMap<String, Identifier> getNeighbors()
	{
		return neighbors;
	}
	
	public Identifier getId()
	{
		return id;
	}
	
	public void setId(Identifier id)
	{
		this.id = id;
	}
	
	public Mailer getAgentMailer()
	{
		return mailer;
	}
	
	public void setAgentMailer(Mailer agentMailer)
	{
		this.mailer = agentMailer;
	}
	
	public HashMap<String, Variable<Integer>> getVariables()
	{
		return variables;
	}
	
	public void setVariables(HashMap<String, Variable<Integer>> variables)
	{
		this.variables = variables;
	}
	
	public HashMap<String, Constraint> getConstraints()
	{
		return constraints;
	}
	
	public void setConstraints(HashMap<String, Constraint> constraints)
	{
		this.constraints = constraints;
	}
	
	public String getMonitorPath()
	{
		return monitorPath;
	}
	
	public void setMonitorPath(String monitorPath)
	{
		this.monitorPath = monitorPath;
	}
	
	public boolean agentActive()
	{
		return agentActive;
	}
	
	public void setNeighbors(HashMap<String, Identifier> neighbors)
	{
		this.neighbors = neighbors;
	}
}
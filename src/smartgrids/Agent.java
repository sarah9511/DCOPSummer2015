package smartgrids;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorRef;
import smartgrids.message.ValueReport;
import smartgrids.message.ReadyMessage;

import java.lang.Thread;

public class Agent
{
	private static final int cycleThreshold = 20;
	private int currentCycle;
	private int currCycleStartTime;
	private int betterCostThreshold = 10;
	private int iterationsSinceBetterCost;
	private boolean currCycleComplete;

	private Identifier id;
	private HashMap<String, Variable<Integer>> variables = new HashMap<>();
	private HashMap<String, Constraint> constraints = new HashMap<>();
	private HashMap<String, Identifier> neighbors = new HashMap<>();
    private HashMap<String, ReadyMessage> neighborsReady = new HashMap<>();
	private Mailer mailer;
	
	private Boolean active;   // will be used to monitor termination conditions, determine when monitor should stop agents 
	
	private Timer cycleCheckTimer;
	public boolean neighborReadyStatus;
	//private String monitorPath = "akka.tcp://monitorSystem@127.0.0.1:2550/user/monitor";
	
	
	public Agent(Identifier id, HashMap<String, Variable<Integer>> variables, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors, Mailer mailer)
	{
		this.id = id;
		this.variables = variables;
		this.constraints = constraints;
		this.neighbors = neighbors;
		this.mailer = mailer;
		
		
		
		active = true;
		currentCycle = 0;
		currCycleComplete = false;
		iterationsSinceBetterCost = 0;
		cycleCheckTimer = new Timer();
		
		
		
		System.err.println("\nAgent " + id.getName() + " is alive\n");
		
		// print variables
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
		//cycleCheckTimer.scheduleAtFixedRate( new CycleCheck(), 5000, 500  );  // move this functionality to valueReport
	}
	
	
	/*
	 * Received info from a neighbor agent. This lets us assign ActorRefs to the Identifiers in the neighbors list
	 */
	public void infoResponse(String name, ActorRef sender)
	{
		// set ActorRef for corresponding neighbor
		neighbors.get(name).setActorRef(sender);
		
		System.err.println(id.getName() + " received a response from " + name + "!");
		
		// flag is set if all neighbors' ActorRefs have been set
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
		
		// if neighbors filled out, set up constraints' variables (see Constraint.setupVars())
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
	
	/*
	 * Received a variable value from a neighbor agent
	 */
	public void valueReport(String ownerName, String varName, int value)
	{
		// key for variable; used in variables HashMap
		String varKey = ownerName + ":" + varName;
		
		System.err.println("received a value report from " + ownerName + " - " + varName);
		
		// find Variable object that this value corresponds to and set it (corresponding Variable object may exist in multiple places)
		for (Constraint c : constraints.values())
		{
			Variable<Integer> theirVar = c.getTheirVars().get(varKey);
			
			if (theirVar == null || theirVar.set)
			{
				continue;
			}
			
			// before
			System.err.print('\t' + c.getName() + " - " + varKey + "  " + theirVar.getValue() + " -> ");
			
			theirVar.setVal(value);
			theirVar.set = true;
			
			// after
			System.err.print(c.getTheirVars().get(varKey).getValue());
			System.err.println();
		}
		
		// flag is set if all neighbors' variables are set
		boolean allVarsSet = true;
		
		// check if all variables have come in for this iteration
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
			System.err.println("\nALL VARS SET\n");
			boolean betterFound = false;
			// determine a possible change in value for a variable
			for (Variable<Integer> var : variables.values())
			{
				
				// best possible cost of this variable
				int bestCost = 0;
				for (Constraint constraint : var.getConstraints())
				{
					bestCost += constraint.calcCost();
				}
				int oldCost = bestCost;
				
				// best possible value for this variable
				int bestVal = var.getValue();
				int oldVal = bestVal;
				
				ArrayList<Integer> domainValues = var.getDomain().getValues();
				
				// test each value in variable's domain
				for (int i = 0; i < domainValues.size(); i++)
				{
					var.setVal(domainValues.get(i));
					
					// cost for this variable value
					int currentCost = 0;
					for (Constraint constraint : var.getConstraints())
					{
						currentCost += constraint.calcCost();
					}
					
					// if this value is better, this is the best so far
					if (currentCost < bestCost)
					{
						betterFound = true;
						iterationsSinceBetterCost = 0;
                        currCycleComplete = false;
						bestCost = currentCost;
						bestVal = domainValues.get(i);
					} 
					
				}
				
				// increment cycle condition based on time since an improvement was found
				if (!betterFound){ 
                    iterationsSinceBetterCost++;
                    System.err.println( "current number of iterations since last improvement: " + iterationsSinceBetterCost );
                }		
				
				// what to do if this agent is ready to move to next cycle: notify all neighbors
				if (iterationsSinceBetterCost >= cycleThreshold){
                    currCycleComplete = true;
                    //notify neighbors this agent is ready
                    for (Identifier n : neighbors.values()){
                        mailer.send( n.getActorRef() , new ReadyMessage( currCycleComplete, id.getName() ) );
                        
                    }
                    
                    //if all ready messages received, call sendVars, move on to next cycle, clear all ready messages
                    if ( neighborsReady.size() == neighbors.size() ){
                        System.err.println("ALL AGENTS READY FOR NEXT CYCLE");
                        iterationsSinceBetterCost = 0;
                        currentCycle++;
                        neighborsReady.clear();
                        sendVars();
                        
                    }
                    
				}
				
				// chance of changing value
				if (Math.random() > 0.5)
				{
					System.err.println('\t' + var.getName() + " - " + oldVal + ", " + oldCost + " -> " + bestVal + ", " + bestCost);
					var.setVal(bestVal);
				}
				else
				{
					var.setVal(oldVal);
				}
			}
			
			System.err.println();
			
			// unset vars for next iteration
			for (Constraint c : constraints.values())
			{
				System.err.println('\t' + c.getName() + " cost: " + c.calcCost());
				
				for (Variable<?> var : c.getTheirVars().values())
				{
					var.set = false;
				}
			}
			
			System.err.println();
			
			// wait 2 seconds to make sure all agents' receive vals
			long lastTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - lastTime < 2000);
			
			sendVars();
		}
	}
	
	
	/*
	 * Send variable values to neighbor agents
	 */
	private void sendVars()
	{
		System.err.println("Sending vars\n");
		
		// list used to keep track of variables sent, don't want to send a variable multiple times
		ArrayList<String> sentVars = new ArrayList<>();
		
		for (Constraint constraint : constraints.values())
		{
			Collection<Variable<Integer>> ourVars = constraint.getOurVars().values();
			Collection<Variable<Integer>> theirVars = constraint.getTheirVars().values();
			
			// we use theirVars because we know the owner of those vars
			// we know to send ourVars to the owners of theirVars because they share a constraint
			for (Variable<Integer> theirVar : theirVars)
			{
				String ownerName = theirVar.getOwner().getName();
				ActorRef ownerRef = theirVar.getOwner().getActorRef();
				
				// send each of ourVars in this constraint to our neighbor (the owner of theirVar)
				for (Variable<Integer> ourVar : ourVars)
				{
					String varKey = ourVar.getName() + ':' + ownerName;
					
					// if we haven't already sent this var
					if (!sentVars.contains(varKey))
					{
						mailer.send(ownerRef, new ValueReport(id.getName(), ourVar.getName(), ourVar.getValue()));
						//ownerRef.tell(new ValueReport(id.getName(), ourVar.getName(), ourVar.getValue()), self);
						ourVar.reset();
						
						sentVars.add(varKey);
					}
				}
			}
		}
	}
	
	
	public Identifier getId()
	{
		return id;
	}
	
	public HashMap<String, Variable<Integer>> getVariables()
	{
		return variables;
	}
	
	public HashMap<String, Identifier> getNeighbors()
	{
		return neighbors;
	}
	
	public boolean active()
	{
		return active;
	}
	
	public boolean getCurrCycleComplete(){
		return currCycleComplete;
	}
	
	public void receiveReadyMessage( ReadyMessage rm ){
        neighborsReady.put( rm.getName(), rm );
    }
	
	
	class CycleCheck extends TimerTask{
		
		@Override
		public void run(){
			System.err.println("in cycle check; number of iterations:" + iterationsSinceBetterCost  + "\n");

			if ( iterationsSinceBetterCost > betterCostThreshold ){
				currCycleComplete = true;								//pre-ready state
				System.err.println(" current cycle complete ");
			}
			boolean terminate = true;
			for ( Identifier n : neighbors.values() ){
				mailer.send( n.getActorRef(), "cycleCheck" );
				if (!neighborReadyStatus){
					terminate = false;
					break;
				}
			}
			if(terminate && currCycleComplete){
				//have all agents agree,
				//commit seppuku
			}
			
		}
		
	}
	
	
}
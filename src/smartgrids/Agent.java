package smartgrids;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


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
	
	private HashMap<String, Domain> domains = new HashMap<>();
	private HashMap<String, Variable> variables = new HashMap<>();
	private HashMap<String, Relation> relations = new HashMap<>();
	private HashMap<String, Constraint> constraints = new HashMap<>();
	
	private HashMap<String, Identifier> neighbors = new HashMap<>();
	
	private boolean valChanged;
	
	
	public Agent(Identifier id, HashMap<String, Domain> domains, HashMap<String, Variable> variables, HashMap<String, Relation> relations, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors)
	{
		this.id = id;
		
		this.domains = domains;
		this.variables = variables;
		this.relations = relations;
		this.constraints = constraints;
		
		this.neighbors = neighbors;
		
		this.valChanged = true;
		
		//Thread.sleep(2000);
		
		System.out.println("\nAgent " + id.getName() + " alive\n");
		
		System.err.println("Variables:");
		for (Variable v : variables.values())
		{
			v.owner = id;
			
			System.err.print("    " + v.getName() + ": " + v.getValue() + ", " + v.getDomain().getName() + ": ");
			for (Object value : v.getDomain().getValues())
			{
				System.err.print((int)value + " ");
			}
			System.err.println();
		}
		System.err.println();
		//run();
	}
	
	private void sendIdentifyRequests()
	{
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
		}
		else if (message instanceof ActorIdentity)
		{
			ActorRef responder = ((ActorIdentity)message).getRef();
			
			if (responder != null)
			{
				System.out.println(id.getName() + " received actor identity");
				//getContext().watch(responder);
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
				List<ActorRef> sendTo = new ArrayList<ActorRef>();
				
				for( String n : neighbors.keySet() ){
					System.err.println("in InfoResponse loop: " + n);
				}
				
				for (Constraint constraint : constraints.values())
				{
					constraint.setupVars(id, variables, neighbors);
				}
				
				// send vars
				
				// check if each variable needs to be changed
				// redundant in this step, just here for reference for now
				
				for(Variable v : variables.values()){
					//TODO: change to get list of apt neighbors through constraints
					//      send a new ValueReport to the apt neighbors
					//		reset v.valChanged
					
					
					
					if (v.getValChanged()){
						// may change in future to use owner property of variable and narrow by going through constraints?
						for (Identifier n: neighbors.values()  ){
						
							n.getActorRef().tell( (new ValueReport( id.getName(), getSelf(), v )) , getSelf() );
						
						}
						v.reset();
						
					}	
					
				}
				
			}
		}
		else  if (message instanceof ValueReport)
		{
			ValueReport vr = (ValueReport) message;
			System.out.println("received a value report from: " + vr.name );
			
			
			for (Constraint c : constraints.values()){
				System.err.println("going through constraints");
				Variable theirVar = c.getTheirVars().get( vr.var.getName() );
				
				if ( theirVar == null ){
					 continue;
				}
				System.err.println("Value before update: " + theirVar.getValue());
				
				
				c.getTheirVars().put( vr.var.getName() , vr.var ); //overwrite previous value
				System.err.println("Value after update: " + c.getTheirVars().get( vr.var.getName() ).getValue());
				
				
				
			}
			
			
		}
		else
		{
			unhandled(message);
		}
	}
}

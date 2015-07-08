package smartgrids;

import java.util.HashMap;

import smartgrids.message.InfoRequest;
import smartgrids.message.InfoResponse;
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
			//getContext().system().scheduler().scheduleOnce(Duration.create(3, SECONDS), getSelf(), ReceiveTimeout.getInstance(), getContext().dispatcher(), getSelf());
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
				getContext().watch(responder);
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
		}
		else
		{
			unhandled(message);
		}
	}
}

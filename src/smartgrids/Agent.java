package smartgrids;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.SuppressedDeadLetter;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Agent extends UntypedActor
{
    private Identifier id;
	
    private HashMap<String, Domain> domains = new HashMap<>();
	private HashMap<String, Variable> variables = new HashMap<>();
	private HashMap<String, Relation> relations = new HashMap<>();
	private HashMap<String, Constraint> constraints = new HashMap<>();
	
	private HashMap<String, Identifier> neighbors = new HashMap<>();
	
	
	public Agent(Identifier id, HashMap<String, Domain> domains, HashMap<String, Variable> variables, HashMap<String, Relation> relations, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors)
	{
		this.id = id;
		
		this.domains = domains;
		this.variables = variables;
		this.relations = relations;
		this.constraints = constraints;
		
		this.neighbors = neighbors;
		
		System.out.println("Agent " + id.getName() + " alive");
	}


	@Override
	public void onReceive(Object message)
	{
		if (message instanceof String)
		{
			System.out.println("Agent " + id.getName() + " received " + (String)message);
		}
		else
		{
			unhandled(message);
		}
	}
}

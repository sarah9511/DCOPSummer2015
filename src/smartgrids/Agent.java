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

public class Agent{

	private Identifier id;
	
	private Mailer agentMailer;
	//private HashMap<String, Domain<?>> domains = new HashMap<>();
	private HashMap<String, Variable<Integer>> variables = new HashMap<>();
	//private HashMap<String, Relation> relations = new HashMap<>();
	private HashMap<String, Constraint> constraints = new HashMap<>();
		
	private HashMap<String, Identifier> neighbors = new HashMap<>();
	
	
	private String monitorPath = "akka.tcp://monitorSystem@127.0.0.1:2550/user/monitor";
	
	//private boolean valChanged;
	
	private boolean agentActive;   // will be used to monitor termination conditions, determine when monitor should stop agents 
	
	public HashMap<String, Identifier> getNeighbors(){
		return neighbors;
	}//closing getNeighbors
	
	public Identifier getId() {
		return id;
	}
	public void setId(Identifier id) {
		this.id = id;
	}
	public Mailer getAgentMailer() {
		return agentMailer;
	}
	public void setAgentMailer(Mailer agentMailer) {
		this.agentMailer = agentMailer;
	}
	public HashMap<String, Variable<Integer>> getVariables() {
		return variables;
	}
	public void setVariables(HashMap<String, Variable<Integer>> variables) {
		this.variables = variables;
	}
	public HashMap<String, Constraint> getConstraints() {
		return constraints;
	}
	public void setConstraints(HashMap<String, Constraint> constraints) {
		this.constraints = constraints;
	}
	public String getMonitorPath() {
		return monitorPath;
	}
	public void setMonitorPath(String monitorPath) {
		this.monitorPath = monitorPath;
	}
	public boolean isAgentActive() {
		return agentActive;
	}
	public void setAgentActive(boolean agentActive) {
		this.agentActive = agentActive;
	}
	public void setNeighbors(HashMap<String, Identifier> neighbors) {
		this.neighbors = neighbors;
	}
	public Agent(Identifier id, HashMap<String, Domain<?>> domains, HashMap<String, Variable<Integer>> variables, HashMap<String, Relation> relations, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors)
	{
		this.id = id;
		
		//this.domains = domains;
		this.variables = variables;
		//this.relations = relations;
		this.constraints = constraints;
		
		this.neighbors = neighbors;
		
		this.agentActive = true;
		
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
	
	//this method creates the mailer and sets our agents mailer to this one
	public void establishMailer(){
		agentMailer = new Mailer(this);
	}//closing create Mailer
	
	
	
}//closing class Agent
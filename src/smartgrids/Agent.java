package smartgrids;

import java.util.HashMap;

public class Agent{

	private Identifier id;
	private HashMap<String, Variable<Integer>> variables = new HashMap<>();
	private HashMap<String, Constraint> constraints = new HashMap<>();
	private HashMap<String, Identifier> neighbors = new HashMap<>();
	private Mailer mailer;
	
	private String monitorPath = "akka.tcp://monitorSystem@127.0.0.1:2550/user/monitor";
	
	//private boolean valChanged;
	
	private boolean agentActive;   // will be used to monitor termination conditions, determine when monitor should stop agents 
	
	
	public Agent(Identifier id, HashMap<String, Variable<Integer>> variables, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors, Mailer mailer)
	{
		this.id = id;
		this.variables = variables;
		this.constraints = constraints;
		this.neighbors = neighbors;
		this.mailer = mailer;
		
		agentActive = true;
		
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
	
	public void setAgentActive(boolean agentActive)
	{
		this.agentActive = agentActive;
	}
	
	public void setNeighbors(HashMap<String, Identifier> neighbors)
	{
		this.neighbors = neighbors;
	}
}
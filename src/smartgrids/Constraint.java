package smartgrids;

import java.util.HashMap;


public class Constraint
{
	private String name;
	private int arity;
	private String[] variables;
	private Relation relation;
	
	private HashMap<String, Variable<?>> ourVars = new HashMap<>();
	private HashMap<String, Variable<?>> theirVars = new HashMap<>();
	
	
	public Constraint(String name, int arity, String scope, Relation relation)
	{
		this.name = name;
		this.arity = arity;
		this.relation = relation;
		
		variables = scope.split(" ");
	}
	
	
	public void setupVars(Identifier thisAgt, HashMap<String, Variable<?>> ourVariables, HashMap<String, Identifier> neighbors)
	{
		//for each string in this constraint's variable list
		for (int i = 0; i < variables.length; i++)
		{
			String[] stringVar = variables[i].split(":");
			
			//if the variable belongs to this agent 
			if (stringVar.length == 1)
			{
				String varName = stringVar[0];
				ourVars.put(varName, ourVariables.get(varName));
			}
			//the agent belongs to someone else 
			else
			{
				String agtName = stringVar[0];
				String varName = stringVar[1];
				
				if (neighbors.get(agtName) == null) return;
				theirVars.put(variables[i], new Variable(varName, neighbors.get(agtName)));
			}
		}
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public int getArity()
	{
		return arity;
	}
	
	public HashMap<String, Variable<?>> getOurVars()
	{
		return ourVars;
	}
	
	public HashMap<String, Variable<?>> getTheirVars()
	{
		return theirVars;
	}
	
	
	//TODO: getCost function
}
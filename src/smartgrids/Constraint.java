package smartgrids;

import java.util.HashMap;


public class Constraint
{
	private String name;
	private int arity;
	private String[] variables;
	private Relation relation;
	
	private HashMap<String, Variable> ourVars = new HashMap<>();
	private HashMap<String, Variable> theirVars = new HashMap<>();
	
	
	public Constraint(String name, int arity, String scope, Relation relation)
	{
		this.name = name;
		this.arity = arity;
		this.relation = relation;
		
		variables = scope.split(" ");
	}
	
	
	public void setupVars(Identifier thisAgt, HashMap<String, Variable> ourVariables, HashMap<String, Identifier> neighbors)
	{
		/*for (int i = 0; i < variables.length; i++)
		{
			System.out.print(variables[i] + " ");
		}
		System.out.println();*/
		
		for (int i = 0; i < variables.length; i++)
		{
			String[] stringVar = variables[i].split(":");
			
			if (stringVar.length == 1)
			{
				String var = stringVar[0];
				
				ourVars.put(var, ourVariables.get(var));
				
				//System.out.println("Our var: " + thisAgt.getName() + " " + var);
			}
			else
			{
				String agtName = stringVar[0];
				String var = stringVar[1];
				
				theirVars.put(var, new Variable(var, neighbors.get(agtName)));
				
				//System.out.println("Their var: " + agtName + " " + var);
			}
		}
	}
	
	
	//TODO: getCost function
}
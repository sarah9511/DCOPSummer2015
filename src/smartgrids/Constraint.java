package smartgrids;

import java.util.ArrayList;
import java.util.HashMap;

import smartgrids.Relation.Tuple;

@SuppressWarnings("rawtypes")

public class Constraint
{
	private String name;
	private int arity;
	private String[] variables;
	private Relation relation;
	private HashMap<String, Variable<Integer>> ourVars = new HashMap<>();
	private HashMap<String, Variable<Integer>> theirVars = new HashMap<>();
	
	
	public Constraint(String name, int arity, String scope, Relation relation)
	{
		this.name = name;
		this.arity = arity;
		this.relation = relation;
		
		variables = scope.split(" ");
	}
	
	
	public void setupVars(Identifier thisAgt, HashMap<String, Variable<Integer>> ourVariables, HashMap<String, Identifier> neighbors)
	{
		for (int i = 0; i < variables.length; i++)
		{
			String[] stringVar = variables[i].split(":");
			
			if (stringVar.length == 1)
			{
				String varName = stringVar[0];
				
				ourVars.put(thisAgt.getName() + ":" + varName, ourVariables.get(varName));
			}
			else
			{
				String agtName = stringVar[0];
				String varName = stringVar[1];
				
				theirVars.put(variables[i], new Variable<Integer>(varName, neighbors.get(agtName)));
			}
		}
		
		for (Variable var : ourVars.values())
		{
			var.addConstraint(this);
		}
	}
	
	
	public int calcCost()
	{
		Tuple[] tuples = relation.getTuples();
		
		//for each tuple
		for (int i = 0; i < tuples.length; i++)
		{
			Tuple tuple = tuples[i];
			int[] input = tuple.getInput();
			
			ArrayList<Integer> temp = new ArrayList<>();
			
			for (int j = 0; j < input.length; j++)
			{
				temp.add(input[j]);
			}
			
			// for each of our vars
			for (Variable ourVar : ourVars.values())
			{
				if (temp.contains(ourVar.getValue()))
				{
					temp.remove(ourVar.getValue());
				}
			}
			
			// for each of their vars
			for (Variable theirVar : theirVars.values())
			{
				if (temp.contains(theirVar.getValue()))
				{
					temp.remove(theirVar.getValue());
				}
			}
			
			//if the item list is empty, that means that each input in the tuples was found in the either our vars or their vars within the constraint information
			if (temp.isEmpty())
			{
				return tuple.getCost();
			}
		}
		
		return relation.getDefaultCost(); //returns the default cost because there was no match that could be found.
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public int getArity()
	{
		return arity;
	}
	
	public HashMap<String, Variable<Integer>> getOurVars()
	{
		return ourVars;
	}
	
	public HashMap<String, Variable<Integer>> getTheirVars()
	{
		return theirVars;
	}
}

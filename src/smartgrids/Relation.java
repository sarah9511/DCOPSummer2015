package smartgrids;

import java.util.ArrayList;


public class Relation
{
	private String name;
	private int arity;
	private int defaultCost;
	private String semantics;
	
	private ArrayList<Tuple> tuples = new ArrayList<Tuple>();
	
	
	public Relation(String name, int arity, int defaultCost, String semantics)
	{
		this.name = name;
		this.arity = arity;
		this.defaultCost = defaultCost;
		this.semantics = semantics;
	}
	
		
	public void createTuples(String tuplesString)
	{
		tuplesString = tuplesString.trim();
		String[] tuplesStrings = tuplesString.split("\\|");
		
		int numTuples = tuplesStrings.length;
		
		for (int i = 0; i < numTuples; i++)
		{
			String[] tupleSplit = tuplesStrings[i].split(":");
			
			int cost;
			if (tupleSplit[0].equals("infinity"))
			{
				cost = Integer.MAX_VALUE;
			}
			else
			{
				cost = Integer.parseInt(tupleSplit[0]);
			}
			
			String[] inputSplit = tupleSplit[1].trim().split(" ");
			ArrayList<Integer> input = new ArrayList<>();
			
			for (int j = 0; j < inputSplit.length; j++)
			{
				input.add(Integer.parseInt(inputSplit[j]));
			}
			
			tuples.add(new Tuple(cost, input));
		}
	}
	
	public void printTuples()
	{
		for (int i = 0; i < tuples.size(); i++)
		{
			System.out.println("Tuple " + i + ": " + tuples.get(i));
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
	
	public int getNumTuples()
	{
		return tuples.size();
	}
	
	
	public String toString()
	{
		return "Name: " + name;
	}
	
	
	private class Tuple
	{
		private int cost;
		private ArrayList<Integer> input;

		
		public Tuple(int cost, ArrayList<Integer> input)
		{
			this.cost = cost;
			this.input = input;
		}
		
		
		public int getCost()
		{
			return cost;
		}
		
		public ArrayList<Integer> getInput()
		{
			return input;
		}
		
		
		public String toString()
		{
			String string = "Cost: " + cost + ", Values: ";
			
			for (Integer i : input)
			{
				string += i + " ";
			}
			
			return string;
		}
	}
}

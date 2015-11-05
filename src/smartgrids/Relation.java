package smartgrids;


public class Relation
{
	private String name;
	private int arity;
	private int defaultCost;
	
	private Tuple[] tuples;

	//private String semantics;
	//private ArrayList<Tuple> tuples = new ArrayList<Tuple>();
	
	
	public Relation(String name, int arity, int defaultCost, String semantics)
	{
		this.name = name;
		this.arity = arity;
		this.defaultCost = defaultCost;
		//this.semantics = semantics;
	}
	
	
	//This method fills in the tuples for this relation. it is not included in the constructor because of its complexity
	public void createTuples(String tuplesString)
	{
		tuplesString = tuplesString.trim();
		String[] tuplesStrings = tuplesString.split("\\|");
		
		int nTuples = tuplesStrings.length;
		
		tuples = new Tuple[nTuples];
		
		for (int i = 0; i < nTuples; i++)
		{
			String[] tupleSplit = tuplesStrings[i].split(":");
			
			//System.out.println("TupleSplit: " + tupleSplit[0] + ":" + tupleSplit[1]);
			//System.out.println( "\tlength: " + tuplesString.length() + " last char:" + tuplesString.charAt(tuplesString.length() - 1));
			int cost;
			if (tupleSplit[0].equals("infinity"))
			{
				cost = Integer.MAX_VALUE;
			}
			else
			{
				cost = Integer.parseInt(tupleSplit[0].trim());
			}
			
			String[] inputSplit = tupleSplit[1].trim().split(" ");
			int[] input = new int[inputSplit.length];
			
			for (int j = 0; j < inputSplit.length; j++)
			{
				input[j] = Integer.parseInt(inputSplit[j]);
			}
			
			tuples[i] = new Tuple(cost, input);
		}
	}
	
	
	public void printTuples()
	{
		for (int i = 0; i < tuples.length; i++)
		{
			System.out.println("Tuple " + i + ": " + tuples[i]);
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
	
	public int getDefaultCost()
	{
		return defaultCost;
	}
	
	public Tuple[] getTuples()
	{
		return tuples;
	}
	
	
	public String toString()
	{
		return "Name: " + name;
	}
	
	
	public class Tuple
	{
		private int cost;
		private int[] input;

		
		public Tuple(int cost, int[] input)
		{
			this.cost = cost;
			this.input = input;
		}
		
		
		public int getCost()
		{
			return cost;
		}
		
		public int[] getInput()
		{
			return input;
		}
		
		
		public String toString()
		{
			String string = "Cost: " + cost + ", Values: ";
			
			for (int i = 0; i < input.length; i++)
			{
				string += input[i] + " ";
			}
			
			return string;
		}
	}
}


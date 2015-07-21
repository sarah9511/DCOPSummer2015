package smartgrids;

import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings("rawtypes")

public class Constraint
{
	private String name;
	private int arity;
	private String[] variables;
	private Relation relation;	
	private HashMap<String, Variable<?>> ourVars = new HashMap<>();
	private HashMap<String, Variable<?>> theirVars = new HashMap<>();
	
	public String getName()
	{
		return name;
	}
	
	public int getArity()
	{
		return arity;
	}
	public HashMap<String, Variable<?>> getOurVars(){
		return ourVars;
	}
	public HashMap<String, Variable<?>> getTheirVars(){
		return theirVars;
	}
	public Constraint(String name, int arity, String scope, Relation relation)
	{
		this.name = name;
		this.arity = arity;
		this.relation = relation;
		
		variables = scope.split(" ");
	}
	
	/*
	 * 
	 */
	
	public int retrieveConstraintValue( Relation r){
		boolean matchFlag = true;

		//for each tuple
		for(int j = 0; j < r.getTuples().size(); j++){

			HashMap<String, Variable<?>> ourVarlist =  getOurVars();//storing our variables
			HashMap<String, Variable<?>> theirVarlist = getTheirVars();//storing their variables
			Iterator o = ourVarlist.entrySet().iterator();//iterator for our variables.
			//Iterating through each variable in the current tuples
			int i = 0;
			HashMap.Entry cVar;
			
			//iterating through all of our variables
			
			while(o.hasNext()){
				
				cVar = (HashMap.Entry)o.next();//getting the next element from iterator

				if(!(cVar.getValue().equals(r.getTuples().get(j).getInput().get(i)))){//if the current variable is not equal to its correspondent in the tuple
					matchFlag = false; //they do not match , so set the flag to false.
				}//closing if statement block 
				i++; //incrementing the variable in the tuple that is being examined
				
			}//closing while loop.
			Iterator t = theirVarlist.entrySet().iterator();
			
			
			while(t.hasNext()){
				
				cVar = (HashMap.Entry)t.next();//getting the next element from iterator

				if(!(cVar.getValue().equals(r.getTuples().get(j).getInput().get(i)))){//if the current variable is not equal to its correspondent in the tuple
					matchFlag = false; //they do not match , so set the flag to false.
				}//closing if statement block 
				i++; //incrementing the variable in the tuple that is being examined
				
			}//closing while loop.
			
			//if this tuple turns out to be a match for our variable values
			if (matchFlag){
				//
				int currentCost = r.getTuples().get(j).getCost();

				return currentCost;
				
			}//closing if statement 

		}//closing for loop

		return r.getDefault();


	}//closing function
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

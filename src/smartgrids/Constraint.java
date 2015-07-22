package smartgrids;

import java.util.HashMap;
import java.util.ArrayList;
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
		
		//for each tuple
		for(int j = 0; j < r.getTuples().size(); j++){
			ArrayList<Integer> o = new ArrayList(ourVars.values());
			ArrayList<Integer> t = new ArrayList(theirVars.values());
			ArrayList<Integer> iTemp = new ArrayList();
			
			for (int i = 0; i <r.getTuples().get(j).getInput().size(); i++){
				iTemp.add(r.getTuples().get(j).getInput().get(i));
			}//closing for loop for filling copy array list
			
			for(int k = 0; k < iTemp.size(); k ++){//for each element in the temp array
				if(o.contains(iTemp.get(k))){//if the int is one of our variables
					o.remove(iTemp.get(k));//remove from the list of our vars
					iTemp.remove(k); //remove this element from itemp
					k--;//setting the index back one to adjust for removal
				}//closingif block
				else if(t.contains(iTemp.get(k))){//if the int is one of our variables
					t.remove(iTemp.get(k));//remove from the list of our vars
					iTemp.remove(k); //remove this element from itemp
					k--;//setting index back one to adjust for removal
				}//closingif block
				
				
			}//closing for loop that checks each element in iTemp
			
			//if the itemp list is empty, that means that each input in the tuples was found in the either our vars or their vars within the constraint information
			if(iTemp.isEmpty()){
				return r.getTuples().get(j).getCost();
			}
			
		}//closing for loop
		
		return r.getDefault(); //returns the default cost because there was no match that could be found.


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

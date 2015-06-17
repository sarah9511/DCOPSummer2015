import java.util.ArrayList;
public class Relation{

   String name;
   int arity;
   int defaultCost;
   String semantics;
   
   ArrayList<Tuple> tuples;
   int nbtuples;
   
   
   public Relation(String n, int a, int d, String s){
      name = n;
      arity = a;
      defaultCost = d;
      semantics = s;
      tuples = new ArrayList<Tuple>();
          
   }//closing constructor
   
   public void setNBTuples(int nb){
      nbtuples = nb;
   }
   
   public int getNBTuples(){
      return nbtuples;
   }

   
   public int getArity(){
      return arity;
   }
   
  
   
   public String toString(){
      String s = "**Printing Relation** \nName: " + name + "\n";
       
      return s;  
   }//closing
	
   public void createTuples(String t){
		System.out.println("Got into create Tuples. T is " + t );
		System.out.println("nbtuples is : " + nbtuples + " arity is " + arity );
		for(int j = 0; j < nbtuples; j++){
			System.out.println("***CREATING TUPLES: iteration " + j + " ***");
			t = t.trim();
			System.out.println("T trimmed is " + t);
			int Cost;
			if(t.substring(0, t.indexOf(':')).equals("infinity")){
				//System.out.println("infinityA");
				Cost = Integer.MAX_VALUE;
				//System.out.println("infinityB");
			}
			else
				Cost = Integer.parseInt(t.substring(0 , t.indexOf(':')));//getting the string part before the ":"
			t = t.substring(t.indexOf(':') + 1); // trimming off the cost now that it is saved for this tuple.
			//System.out.println("T is now value : " + t );
			System.out.println("Cost is " + Cost);
			ArrayList<Integer> Inputs = new ArrayList<Integer>();
			
			//getting the inputs using a for loop
			for(int k = 0; k < arity; k ++){
				t.trim();
				//System.out.println("T in the inner loop is : " + t);
				int in = -1;
				
				//if the number we are at is the last in its tuple 
				if(k == arity -1){
					t = t.trim();
					if(j != nbtuples -1){ // if this is not the last tuple look for a '|'
						in = Integer.parseInt(t.substring(0, t.indexOf('|')));
						t = t.substring(t.indexOf('|') +1);
						t = t.trim();
					}//closing 
					else 
						in = Integer.parseInt(t);
					
					//System.out.println("Completed if 68");
					
					
				}//closing if 
				
				//if the number we are at is not the last one in its tuple
				else{
					t = t.trim();
					//System.out.println("T in the else case :  " + t);
					in = Integer.parseInt(t.substring(0, t.indexOf(' ')));
					//System.out.println("Completed else 71.");
					t = t.substring(t.indexOf(' ')+1);
					t = t.trim();
				}//closing else
				System.out.println("The number being added to the input list is " + in);
				Inputs.add(in);
				//System.out.println("T at the end of the inner loop is :" + t);
			}//closing for loop
			
			
			Tuple tp = new Tuple(Cost, Inputs);
			tuples.add(tp);
			//System.out.println("T at the end of the outer loop is :" + t);
		}//closing for loop
		
		
   }//closing createTuples
}//closing class

class Tuple{
	private int cost;
	private ArrayList<Integer> inputs;

	public Tuple(){
		
		cost = -1;
		inputs = null;
	
	}//closing default constructor
	
	public Tuple(int c, ArrayList<Integer> i){
		
		cost = c;
		inputs = i;
		
	}//closing constructor

	
}//closing class Tuple

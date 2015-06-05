import java.util.ArrayList;
public class Relation{

   String name;
   int arity;
   float fval; // the value of the function in the relation
   ArrayList<ArrayList<String>> tuples;
   int nbtuples;
   
   
   public Relation(String n, int a){
      name = n;
      arity = a;
      
      
   }//closing constructor
   
   public void setNBTuples(int nb){
      nbtuples = nb;
   }
   
   public int getNBTuples(){
      return nbtuples;
   }
   public void setFVal(float f){
      fval = f;
   }
   public void addTuple(ArrayList<String> s){
      if(tuples == null)tuples = new ArrayList<ArrayList<String>>();
      tuples.add(s);
   }
   
   public int getArity(){
      return arity;
   }
   
   public float getFVal(){
      return fval;
   }
   
   public String toString(){
      String s = "**Printing Relation** \nName: " + name + "\nResult: " + fval +"\n";
       
      return s;  
   }//closing
}//closing class

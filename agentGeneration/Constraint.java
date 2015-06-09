import java.util.ArrayList;

public class Constraint{
   String name;
   String reference;
   int arity;
   ArrayList<String> scope;
   
   public Constraint( String n, String r, int a, ArrayList<String> sc){
   
      name = n;
      reference = r;
      arity = a;
      scope = sc;
      
   }//closing 

}//closing class 

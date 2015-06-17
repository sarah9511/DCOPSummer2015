import java.util.ArrayList;

public class Constraint{
   String name;
   String reference;
   int arity;
   Relation relation;
   ArrayList<String> scope;
   String rawScope;
   
   public Constraint( String n, String r, int a, ArrayList<String> sc, String rawSc){
   
      name = n;
      reference = r;
      arity = a;
      scope = sc;
      rawScope = rawSc;
   }//closing 

}//closing class 
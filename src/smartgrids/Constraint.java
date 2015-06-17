package smartgrids;


public class Constraint
{
	private String name;
	private int arity;
	private String[] variables;
	private Relation relation;
	
	
	public Constraint(String name, int arity, String scope, Relation relation)
	{
		this.name = name;
		this.arity = arity;
		this.relation = relation;
		
		variables = scope.split(" ");
	}
	
	
	//TODO: getCost function
}
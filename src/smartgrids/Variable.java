package smartgrids;


public class Variable<T>
{
	private String name;
	private String type;
	
	private Domain<T> domain;
	private T value;
	
	public Identifier owner;
	
	
	public Variable(String name, String type, Domain<T> domain)
	{
		this.name = name;
		this.type = type;
		
		this.domain = domain;
		
		this.value =  this.domain.getValues().get( (int)Math.random() * (this.domain.getValues().size() - 1) );
		
	}
	
	
	public String getName(){
		return name;
	}
	
}
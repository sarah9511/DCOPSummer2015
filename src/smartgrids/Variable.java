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
		
		value =  this.domain.getValues().get((int)(Math.random() * this.domain.getValues().size()));
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public Domain<T> getDomain()
	{
		return domain;
	}
	
	public T getValue()
	{
		return value;
	}
}
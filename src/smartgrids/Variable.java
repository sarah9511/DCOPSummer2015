package smartgrids;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Variable<T> implements Serializable
{
	private String name;
	private String type;
	
	private Domain<T> domain;
	private T value;
	
	public Identifier owner;
	
	private boolean valChanged;
	
	
	public Variable(String name, String type, Domain<T> domain)
	{
		this.name = name;
		this.type = type;
		this.domain = domain;
		
		value = domain.getValues().get((int)(Math.random() * domain.getValues().size()));
		valChanged = true;
	}
	
	public Variable(String name, Identifier owner)
	{
		this.name = name;
		this.owner = owner;
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
	
	public boolean valChanged()
	{
		return valChanged;
	}
	
	public void setVal(T value)
	{
		this.value = value; 
		valChanged = true;
	}
	
	public void reset()
	{
		valChanged = false;
	}
}
package smartgrids;

import java.io.Serializable;

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
		
		value =  this.domain.getValues().get((int)(Math.random() * this.domain.getValues().size()));
		this.valChanged = true;
	}
	
	public Variable(String name, Identifier owner)
	{
		this.name = name;
		this.owner = owner;
		// value =  this.domain.getValues().get((int)(Math.random() * this.domain.getValues().size()));
		// this.valChanged = true;
		
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
	
	public boolean getValChanged(){
		return this.valChanged;
	}
	
	public void setVal(T val){
		this.value = val; 
		this.valChanged = true;
	}
	
	public void reset(){
		this.valChanged = false;
	}
	
}
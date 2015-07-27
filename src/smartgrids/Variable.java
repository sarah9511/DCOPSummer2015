package smartgrids;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class Variable<T> implements Serializable
{
	private String name;
	private String type;
	
	private Domain<T> domain;
	private T value;
	
	private ArrayList<Constraint> constraints = new ArrayList<>();
	
	private Identifier owner;
	private boolean ownerSet;
	
	private boolean valChanged;
	
	public boolean set = false;
	
	
	public Variable(String name, String type, Domain<T> domain)
	{
		this.name = name;
		this.type = type;
		this.domain = domain;
		
		value = domain.getValues().get((int)(Math.random() * domain.getValues().size()));
		valChanged = true;
		
		ownerSet = false;
	}
	
	public Variable(String name, Identifier owner)
	{
		this.name = name;
		this.owner = owner;
		
		ownerSet = true;
	}
	
	
	public void addConstraint(Constraint constraint)
	{
		constraints.add(constraint);
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
	
	public ArrayList<Constraint> getConstraints()
	{
		return constraints;
	}
	
	public Identifier getOwner()
	{
		return owner;
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
	
	public void setOwner(Identifier owner)
	{
		if (!ownerSet) this.owner = owner;
		ownerSet = true;
	}
	
	public boolean getSet(){
		return this.set;
	}
	
}
package smartgrids.message;

import java.io.Serializable;

import smartgrids.Variable;

@SuppressWarnings("serial")
public class ValueReport implements Serializable
{
	public String name;
	public Variable<?> var;
	
	
	public ValueReport(String name, Variable<?> var)
	{
		this.name = name;
		this.var = var;
	}
}
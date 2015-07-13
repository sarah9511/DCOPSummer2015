package smartgrids;

import java.util.ArrayList;
import java.io.Serializable;

@SuppressWarnings("serial")
public class Domain<T> implements Serializable
{
	private String name;
	private String type;
	
	private ArrayList<T> values = new ArrayList<>();
	
	
	public Domain(String name, String type)
	{
		this.name = name;
		this.type = type;
	}
	
	
	public void addValue(T value)
	{
		values.add(value);
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public String getType()
	{
		return type;
	}
	
	public ArrayList<T> getValues()
	{
		return values;
	}
	
	public void printValues()
	{
		for (T value : values)
		{
			System.out.println(value);
		}
	}
}
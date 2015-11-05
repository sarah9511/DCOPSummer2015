package smartgrids.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ValueReport implements Serializable
{
	public String ownerName;
	public String varName;
	public int value;
	
	
	public ValueReport(String ownerName, String varName, int value)
	{
		this.ownerName = ownerName;
		this.varName = varName;
		this.value = value;
	}
}
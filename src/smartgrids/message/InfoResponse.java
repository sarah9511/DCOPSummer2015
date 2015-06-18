package smartgrids.message;

import java.io.Serializable;

public class InfoResponse implements Serializable
{
	public String name;
	
	
	public InfoResponse(String name)
	{
		this.name = name;
	}
}

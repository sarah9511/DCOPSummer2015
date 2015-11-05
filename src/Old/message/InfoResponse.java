package smartgrids.message;

import java.io.Serializable;


@SuppressWarnings("serial")
public class InfoResponse implements Serializable
{
	public String name;
	
	
	public InfoResponse(String name)
	{
		this.name = name;
	}
}

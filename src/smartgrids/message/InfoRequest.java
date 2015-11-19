package smartgrids.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class InfoRequest implements Serializable
{
	public String agentName;
	
	
	public InfoRequest(String agentName)
	{
		this.agentName = agentName;
	}
}

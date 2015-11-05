package smartgrids.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ReadyMessage implements Serializable
{
	public String agentName;
	
	
	public ReadyMessage(String agentName)
	{
		this.agentName = agentName;
	}
}


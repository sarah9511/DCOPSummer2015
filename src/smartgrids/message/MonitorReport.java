package smartgrids.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MonitorReport implements Serializable
{
	public boolean active;
	
	
	public MonitorReport(boolean active)
	{
		this.active = active;
	}
}
package smartgrids.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MonitorReport implements Serializable
{
	public boolean active;
	public long totalAgentMemory;
	public long memoryInUse;
	
	public MonitorReport(boolean active, long totalMem,long freeMem)
	{
		this.active = active;
		this.totalAgentMemory = totalMem;
		this.memoryInUse = totalAgentMemory - freeMem;
	}
}
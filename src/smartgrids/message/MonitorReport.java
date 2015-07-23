package smartgrids.message;

import java.util.ArrayList;
import java.io.Serializable;

@SuppressWarnings("serial")
public class MonitorReport implements Serializable
{
	private boolean active;
	private ArrayList<Boolean> variableStatuses;   //only send whether variables are still being changed so as to avoid having one agent that knows everything?
	
	
	public MonitorReport(boolean agentStatus, ArrayList<Boolean> vars)
	{
		this.active = agentStatus;
		this.variableStatuses = vars;
	}
	
	
	public boolean getActive()
	{
		return this.active;
	}
	
	public ArrayList<Boolean> getVariableStatuses()
	{
		return this.variableStatuses;
	}
}
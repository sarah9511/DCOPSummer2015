package smartgrids.message;

import java.util.ArrayList;
import java.io.Serializable;

@SuppressWarnings("serial")
public class MonitorReport implements Serializable
{
	public boolean active;
	public ArrayList<Boolean> variableStatuses;   //only send whether variables are still being changed so as to avoid having one agent that knows everything?
	
	
	public MonitorReport(boolean active, ArrayList<Boolean> variableStatuses)
	{
		this.active = active;
		this.variableStatuses = variableStatuses;
	}
}
package smartgrids;

import akka.actor.ActorRef;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Identifier implements Serializable
{
	private String name;
	private String ip;
	private int port;
	
	private ActorRef actorRef;
	private boolean refSet = false;
	//private List<Variable> agentVars;
	
	private boolean msgSent;
	private boolean msgReceived;
	int cycleCount;
	
	public Identifier(String name, String ip, int port)
	{
		this.name = name;
		this.ip = ip;
		this.port = port;
		
		msgSent = msgReceived = false;
		cycleCount = 0;
		
		
		//this.agentVars = new ArrayList<Variable>();
	}
	
	
	public String getName()
	{
		return name;
	}
	
	public String getIP()
	{
		return ip;
	}
	
	public int getPort()
	{
		return port;
	}
	
	public ActorRef getActorRef()
	{
		return actorRef;
	}
	
	/*public List<Variable> getAgentVars()
	{
		return agentVars;
	}*/
	
	public boolean refSet()
	{
		return refSet;
	}
	
	
	public void setActorRef(ActorRef actorRef)
	{
		if (!refSet) this.actorRef = actorRef;
		refSet = true;
	}
	
	
	@Override
	public String toString()
	{
		return ip + ':' + port;
	}
	
	public void setMsgSent(boolean b){
		msgSent = b;
	}
	
	public void setMsgReceived(boolean b){
		msgReceived = b;
	}

	public boolean getMsgSent (){
		return msgSent;
	}
	
	public boolean getMsgReceived(){
		return msgReceived;
	}
	
	public  int getCycleCount(){
		return cycleCount;
	}
	
}

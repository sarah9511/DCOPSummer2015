package smartgrids;

import akka.actor.ActorRef;

import java.io.Serializable;

public class Identifier implements Serializable
{
	private String name;
	private String ip;
	private int port;
	
	private ActorRef actorRef;
	private boolean refSet = false;
	//private List<Variable> agentVars;
	
	public Identifier(String name, String ip, int port)
	{
		this.name = name;
		this.ip = ip;
		this.port = port;
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
}

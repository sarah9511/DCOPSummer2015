package smartgrids;

import java.io.Serializable;

import akka.actor.ActorRef;

@SuppressWarnings("serial")
public class Identifier implements Serializable
{
	private String name;
	private String ip;
	private int port;
	
	private ActorRef actorRef;
	private boolean refSet = false;
	
	public boolean alive;
	
	
	public Identifier(String name, String ip, int port)
	{
		this.name = name;
		this.ip = ip;
		this.port = port;
		
		alive = true;
	}
	
	
	public void removeActorRef()
	{
		actorRef = null;
		refSet = false;
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

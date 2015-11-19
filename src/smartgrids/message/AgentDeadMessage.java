package smartgrids.message;

import java.io.Serializable;

import akka.actor.ActorRef;

@SuppressWarnings("serial")
public class AgentDeadMessage implements Serializable
{
	public ActorRef agent;
	
	
	public AgentDeadMessage(ActorRef agent)
	{
		this.agent = agent;
	}
}

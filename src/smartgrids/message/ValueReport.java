package smartgrids.message;

import java.io.Serializable;

import smartgrids.Variable;
import akka.actor.ActorRef;


public class ValueReport implements Serializable
{
	public String name;
	public ActorRef sender;
	public Variable<?> var;
	
	
	public ValueReport(String name, ActorRef send, Variable<?> var)
	{
		this.name = name;
		this.sender = send;
		this.var = var;
	}
}
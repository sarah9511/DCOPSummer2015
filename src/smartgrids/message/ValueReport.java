package smartgrids.message;

import smartgrids.Variable;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;


import com.typesafe.config.ConfigFactory;

public class ValueReport{
	String name;
	ActorRef sender;
	Variable var;
	
	public ValueReport( String n, ActorRef send, Variable v ){
		this.name = name;
		this.sender = send;
		this.var = v;
	}

	
}
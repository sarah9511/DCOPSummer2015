import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.UntypedActor;


public class DemoReceiveAgent{
	String senderPath = "akka.tcp://DemoSendAgentSystem@127.0.0.1:2552/user/DemoSendAgent"
	String name = "DemoReceiveAgent";
	
	@Override
	public void onReceive( Object message ){
		
		
	}
	
	public static void main(String args[]){
		
		
	}
	
}
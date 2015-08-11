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
	final ActorRef sender;
	final ActorRef receiver;
	
	@Override
	public void onReceive( Object message ){
		if ( message instanceof Integer ){
			Integer received = ((Integer) message);
			System.out.println( "received value " + received);
			getSender().tell( received + 1, getSelf() );
			
		} else if (message instanceof ActorIdentity){
			sender = (( ActorIdentity )message).getRef(); 
		}
		
	}
	
	public static void main(String args[]){
		final ActorSystem system = ActorSystem.create( name + "System", ConfigFactory.load("config\\" + name) );
		receiver = system.actorOf(Props.create(DemoReceiveAgent.class), name  );
		
		long lastTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - lastTime < 2000);
		
		getContext().actorSelection(senderPath).tell(new Identify( receiverPath ), getSelf()  );
		
		
	}
	
}
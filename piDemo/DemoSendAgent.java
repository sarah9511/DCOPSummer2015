import java.Math;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.UntypedActor;


public class DemoSendAgent extends UntypedActor{
	String receiverPath = "akka.tcp://DemoReceiveAgentSystem@127.0.0.1:2550/user/DemoReceiveAgent"
	String name = "DemoSendAgent";
	final ActorRef sender;
	final ActorRef receiver;
	
	@Override
	public void onReceive(Object message){
		if ( message instanceof ActorIdentity ){
			receiver = ((ActorIdentity)message.getRef() );
			
		}
		
	}
	
	public static void main(String args[]){

		//creating agent
		final ActorSystem system = ActorSystem.create(   name + "System", ConfigFactory.load("config/" + name) );

		sender = system.actorOf(Props.create(DemoSendAgent.class), name);
		
		//wait for other agent to be created.
		long lastTime = System.currentTimeMillis();
		while (System.currentTimeMillis() - lastTime < 2000);
		
		getContext().actorSelection(receiverPath).tell( new Identify( receiverPath ), getSelf()  );  
		
		runDemo();
		
	}
	
	public void runDemo(){
		
		Integer testMessage = (Integer)Math.random()*20;
		System.out.println("The value of test message is: " + testMessage);
		receiver.tell(testMessage, getSelf());
		System.out.println("Message sent to receiver.");
		
	}
	
}
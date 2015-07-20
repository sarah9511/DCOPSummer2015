package smartgrids;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.typesafe.config.ConfigFactory;


public class AgentMonitor extends UntypedActor
{
	private Timer checkAgentTimer;
	private ArrayList<ActorRef> allAgents;
	
	
	public AgentMonitor()
	{
		checkAgentTimer = new Timer();
		allAgents = new ArrayList<ActorRef>();
		
		checkAgentTimer.scheduleAtFixedRate(new checkTask(), 1000, 1000);
		
		System.err.println("AgentMonitor created");
	}
	
	
	@Override
	public void onReceive(Object message)
	{
		if (message instanceof ActorRef)
		{
			System.out.println("Monitor received new reference");
			allAgents.add((ActorRef)message);
		}
	}
	
	public static void main(String args[])
	{
		final ActorSystem monitorSystem = ActorSystem.create("monitorSystem", ConfigFactory.load("config/monitor"));
		monitorSystem.actorOf(Props.create(AgentMonitor.class), "monitor");
	}
	
	private class checkTask extends TimerTask
	{
		public void run()
		{
			System.err.println("check task running");
			for (ActorRef a : allAgents)
			{
				a.tell("report", getSelf());  
			}
		}
	}
}
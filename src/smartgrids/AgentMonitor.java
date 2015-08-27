package smartgrids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import smartgrids.message.MonitorReport;


public class AgentMonitor extends UntypedActor
{
	private Timer checkAgentTimer;
	private ArrayList<ActorRef> allAgents;
	private ArrayList<Boolean> toTerminate;
	
	private boolean killAll;
	
	private HashMap<ActorRef, Boolean> agentTermination; //change to use this map once it is working
	private HashMap<ActorRef, Integer> reportCounts;
	
	private int reportThreshold;
	
	
	public AgentMonitor(int thresh)
	{
		checkAgentTimer = new Timer();
		allAgents = new ArrayList<ActorRef>();
		toTerminate = new ArrayList<Boolean>();
		killAll = false;
		
		reportThreshold = thresh;
		
		agentTermination = new HashMap<ActorRef, Boolean>();
		reportCounts = new HashMap<ActorRef, Integer>();
		
		checkAgentTimer.scheduleAtFixedRate(new checkTask(), 1000, 1000);
		
		System.err.println("AgentMonitor created");
	}
	
	
	@Override
	public void onReceive(Object message)
	{
		if (message instanceof ActorRef)
		{
			System.out.println("Monitor received new reference");
			
			getContext().watch((ActorRef)message);
			agentTermination.put((ActorRef)message, false);
			reportCounts.put((ActorRef)message, 0);
		}
		else if (message instanceof MonitorReport)
		{
			MonitorReport mr = (MonitorReport)message;
			reportCounts.put(getSender(), reportCounts.get(getSender()) + 1);  //increase report count for this agent
			System.err.println("\treceived a MonitorReport\n\t\treadyToTerminate: " + agentTermination.get(getSender()) + "\n");
			
			if ((!mr.active) || reportCounts.get(getSender()) > reportThreshold)
			{
				//TODO: not tested yet because termination conditions not implemented
				agentTermination.put(getSender(), true);
			}
		}
	}
	
	public static void main(String args[])
	{
		final ActorSystem monitorSystem = ActorSystem.create("monitorSystem", ConfigFactory.load("config/monitor"));
		monitorSystem.actorOf(Props.create(AgentMonitor.class,  20  ), "monitor");
	}
	
	private class checkTask extends TimerTask
	{
		@Override
		public void run()
		{
			System.err.println("check task running");
			
			for (ActorRef sendTo : agentTermination.keySet())
			{
				sendTo.tell("report", getSelf());
			}
			
			killAll = true;
			
			for (Boolean b : agentTermination.values())
			{
				if (b == false)
				{
					killAll = false;
					break;
				}
			}
			
			// System.err.println("killAll: " + killAll);
			// if(killAll)
			// {
				// for(ActorRef deadLikeDisco : agentTermination.keySet())
				// {
					// getContext().unwatch(deadLikeDisco);
					// getContext().stop(deadLikeDisco);
				// } 
			// }
		}
	}
}
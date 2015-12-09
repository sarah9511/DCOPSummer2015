package smartgrids;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import smartgrids.message.KillMessage;
import smartgrids.message.MonitorReport;


public class AgentMonitor extends UntypedActor
{
	private Timer checkAgentTimer;
	private HashMap<ActorRef, Boolean> agents = new HashMap<>();
	private AgentDataManager adm;	
	
	public AgentMonitor()
	{
		checkAgentTimer = new Timer();
		checkAgentTimer.scheduleAtFixedRate(new checkTask(), 1000, 1000);
		adm = new AgentDataManager();
		adm.clear();
		System.err.println("AgentMonitor created");
	}
	
	
	@Override
	public void onReceive(Object message)
	{
		if (message instanceof ActorRef)
		{
			System.out.println("Monitor received new reference");
			
			agents.put((ActorRef)message, false);
		}
		else if (message instanceof MonitorReport)
		{
			MonitorReport mr = (MonitorReport)message;
			System.err.println("\treceived a MonitorReport");
			System.err.println("\tmemory usage: \n\t" + mr.memoryInUse + "/" + mr.totalAgentMemory + " megabytes in use");
			if (!mr.active)
			{
				System.out.println("\nagent not active\n");
				
				agents.put(getSender(), true);
				
				boolean killAll = true;
				
				for (Boolean kill : agents.values())
				{
					if (!kill)
					{
						killAll = false;
						break;
					}
				}
				
				if (killAll)
				{
					for (ActorRef agent : agents.keySet())
					{
						agent.tell(new KillMessage(), null);
					}
					
					checkAgentTimer.cancel();
				}
			}
		}
	}
	
	
	private class checkTask extends TimerTask
	{
		@Override
		public void run()
		{
			System.err.println("check task running");
			
			for (ActorRef sendTo : agents.keySet())
			{
				if (!agents.get(sendTo)) sendTo.tell("report", getSelf());
			}
		}
	}
	
	
	public static void main(String args[])
	{
		final ActorSystem monitorSystem = ActorSystem.create("monitorSystem", ConfigFactory.load("config/monitor"));
		monitorSystem.actorOf(Props.create(AgentMonitor.class), "monitor");
	}
}

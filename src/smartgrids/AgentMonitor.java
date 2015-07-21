package smartgrids;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map;
import java.util.HashMap;

import smartgrids.message.MonitorReport;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.typesafe.config.ConfigFactory;


public class AgentMonitor extends UntypedActor
{
	private Timer checkAgentTimer;
	private ArrayList<ActorRef> allAgents;
	private ArrayList<Boolean> toTerminate;
	
	private boolean killAll;
	
	private HashMap<ActorRef, Boolean> agentTermination; //change to use this map once it is working
	
	
	public AgentMonitor()
	{
		checkAgentTimer = new Timer();
		allAgents = new ArrayList<ActorRef>();
		toTerminate = new ArrayList<Boolean>();
		killAll = false;
		
		
		agentTermination = new HashMap<ActorRef, Boolean>();
		
		checkAgentTimer.scheduleAtFixedRate(new checkTask(), 1000, 1000);
		
		System.err.println("AgentMonitor created");
	}
	
	
	@Override
	public void onReceive(Object message)
	{
		if (message instanceof ActorRef)
		{
			System.out.println("Monitor received new reference");
			
			agentTermination.put( (ActorRef)message, false );
		}
		else if (message instanceof MonitorReport){
			System.err.println( "received a MonitorReport" );
			MonitorReport mr = (MonitorReport) message;
			if (!mr.getActive()){										//TODO: not tested yet because termination conditions not implemented
				
				agentTermination.put( getSender(), true );
				
			}
			
			
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
			
			for ( ActorRef sendTo : agentTermination.keySet() ){
				sendTo.tell( "report" , getSelf()  );
			}
			
		}
	}
}
package smartgrids;

import java.util.ArrayList;
import java.util.HashMap;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.UntypedActor;
import smartgrids.message.InfoRequest;
import smartgrids.message.InfoResponse;
import smartgrids.message.MonitorReport;
import smartgrids.message.ValueReport;

public class Mailer extends UntypedActor 
{
	private final String monitorPath = "akka.tcp://monitorSystem@127.0.0.1:2550/user/monitor";
	
	private Agent agent;
	
	
	public Mailer(Identifier id, HashMap<String, Variable<Integer>> variables, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors)
	{
		agent = new Agent(id, variables, constraints, neighbors, this, getSelf());
	}


	@Override
	public void onReceive(Object message)
	{
		if (message instanceof String)
		{
			// initial identification with neighbors (see sendIdentifyRequests())
			if (((String)message).equals("identify"))
			{
				System.out.println("Agent " + agent.getId().getName() + " received " + (String)message);
				
				// wait for all agents to get running
				long lastTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - lastTime < 2000);
				
				sendIdentifyRequests();
			}
			// report to monitor
			else if (((String)message).equals("report"))
			{
				//System.err.println("received report message from monitor");
				ArrayList<Boolean> varsActive = new ArrayList<Boolean>();
				for (Variable<?> v : agent.getVariables().values())
				{
					varsActive.add(v.set);
				}
				getSender().tell(new MonitorReport(agent.active(), varsActive), getSelf());
			}	
		}
		// received an ActorIdentity as response to our identify
		else if (message instanceof ActorIdentity)
		{
			ActorRef responder = ((ActorIdentity)message).getRef();
			
			if (responder != null)
			{
				// send an InfoRequest so that we may find out who this agent is (we just need its name)
				System.out.println(agent.getId().getName() + " received actor identity");
				responder.tell(new InfoRequest(), getSelf());
			}
		}
		else if (message instanceof InfoRequest)
		{
			// respond to InfoRequest with an InfoResponse (contains our name)
			System.out.println(agent.getId().getName() + " received info request");
			getSender().tell(new InfoResponse(agent.getId().getName()), getSelf());
		}
		else if (message instanceof InfoResponse)
		{
			// pass this InfoResponse data (an agent neighbor's name) to the agent so that the agent may deal with it
			agent.infoResponse(((InfoResponse)message).name, getSender());
		}
		else  if (message instanceof ValueReport)
		{
			// pass this ValueReport data to the agent so that the agent may deal with it
			ValueReport vr = (ValueReport)message;
			agent.valueReport(vr.ownerName, vr.varName, vr.value);
		}
		else
		{
			unhandled(message);
		}
	}
	
	
	/*
	 * Attempts to identify with neighbors using their ip and port. If a neighbor exists, it will reply with an ActorIdentity
	 */
	private void sendIdentifyRequests()
	{
		// identify with monitor first
		System.out.println("Reporting to monitor");
		getContext().actorSelection(monitorPath).tell(getSelf(), getSelf());
		
		// identify with neighbors
		for (Identifier neighbor : agent.getNeighbors().values())
		{
			// if neighbor already set, continue
			if (neighbor.refSet()) continue;
			
			// akka path to neighbor
			String path = "akka.tcp://" + neighbor.getName() + "System@" + neighbor + "/user/" + neighbor.getName();
			getContext().actorSelection(path).tell(new Identify(path), getSelf());
			
			System.out.println(agent.getId().getName() + " is trying to connect with " + path);
		}
	}
}

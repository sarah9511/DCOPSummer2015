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
	private Agent agent;
	
	private String monitorPath = "akka.tcp://monitorSystem@127.0.0.1:2550/user/monitor";
	
	
	public Mailer(Identifier id, HashMap<String, Variable<Integer>> variables, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors)
	{
		agent = new Agent(id, variables, constraints, neighbors, this, getSelf());
	}


	@Override
	public void onReceive(Object message)
	{
		if (message instanceof String)
		{
			if (((String)message).equals("identify"))
			{
				System.out.println("Agent " + agent.getId().getName() + " received " + (String)message);
				long lastTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - lastTime < 2000);
				sendIdentifyRequests();
			}
			else if (((String)message).equals("report"))
			{
				//System.err.println("received report message from monitor");
				ArrayList<Boolean> varsActive = new ArrayList<Boolean>();
				for (Variable<?> v : agent.getVariables().values())
				{
					varsActive.add(v.getSet());
				}
				getSender().tell(new MonitorReport(agent.agentActive(), varsActive), getSelf());
			}	
		}
		else if (message instanceof ActorIdentity)
		{
			ActorRef responder = ((ActorIdentity)message).getRef();
			
			if (responder != null)
			{
				System.out.println(agent.getId().getName() + " received actor identity");
				responder.tell(new InfoRequest(), getSelf());
			}
		}
		else if (message instanceof InfoRequest)
		{
			System.out.println(agent.getId().getName() + " received info request");
			getSender().tell(new InfoResponse(agent.getId().getName()), getSelf());
		}
		else if (message instanceof InfoResponse)
		{
			agent.infoResponse(((InfoResponse)message).name, getSender());
		}
		else  if (message instanceof ValueReport)
		{
			ValueReport vr = (ValueReport)message;
			agent.valueReport(vr.ownerName, vr.varName, vr.value);
		}
		else
		{
			unhandled(message);
		}
	}
	
	
	private void sendIdentifyRequests()
	{
		System.out.println("Reporting to monitor");
		getContext().actorSelection(monitorPath).tell(getSelf(), getSelf());
		
		for (Identifier neighbor : agent.getNeighbors().values())
		{
			if (neighbor.refSet()) continue;
			String path = "akka.tcp://" + neighbor.getName() + "System@" + neighbor + "/user/" + neighbor.getName();
			System.out.println(agent.getId().getName() + " is trying to connect with " + path);
			getContext().actorSelection(path).tell(new Identify(path), getSelf());
		}
	}
}

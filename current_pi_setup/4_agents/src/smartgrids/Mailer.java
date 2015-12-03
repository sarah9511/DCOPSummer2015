package smartgrids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

import akka.actor.ActorIdentity;
import akka.actor.ActorRef;
import akka.actor.Identify;
import akka.actor.UntypedActor;
import smartgrids.message.Ack;
import smartgrids.message.InfoRequest;
import smartgrids.message.InfoResponse;
import smartgrids.message.KillMessage;
import smartgrids.message.MonitorReport;
import smartgrids.message.Pack;
import smartgrids.message.ReadyMessage;
import smartgrids.message.ValueReport;

public class Mailer extends UntypedActor 
{
	private final String monitorPath = "akka.tcp://monitorSystem@169.254.123.40:2550/user/monitor";
	
	private Agent agent;
	
	private HashMap<ActorRef, ArrayList<Integer>> messageIDs = new HashMap<>();
	private HashMap<Integer, Timer> acks = new HashMap<>();
	
	private int curID;
	BufferedWriter logWriter;
	File log;
	
	public Mailer(Identifier id, HashMap<String, Variable<Integer>> variables, HashMap<String, Constraint> constraints, HashMap<String, Identifier> neighbors) throws IOException
	{
		agent = new Agent(id, variables, constraints, neighbors, this);
		log = new File("log.txt");
		logWriter = new BufferedWriter(new FileWriter(log));
	}
	
	
	@Override
	public void onReceive(Object message)
	{
		if (message instanceof Pack)
		{
			Pack pack = (Pack)message;
			int id = pack.id;
			Object msg = pack.message;
			ActorRef sender = getSender();
			
			// if this actor isn't in the messageIDs list yet
			if (!messageIDs.containsKey(sender)) messageIDs.put(sender, new ArrayList<Integer>());
			
			sender.tell(new Ack(id), getSelf());
			
			// if we've already received this message
			if (messageIDs.get(sender).contains(id)) return;
			
			messageIDs.get(sender).add(id);
			
			receive(msg);
		}
		else if (message instanceof Ack)
		{
			int id = ((Ack)message).id;
			
			if (acks.containsKey(id))
			{
				acks.get(id).cancel();
				acks.remove(id);
			}
		}
		else
		{
			receive(message);
		}
	}
	
	
	public void send(ActorRef receiver, Object message)
	{
		Timer timer = new Timer();
		acks.put(curID, timer);
		
		receiver.tell(new Pack(curID, message), getSelf());
		
		timer.schedule(new AckTask(receiver, message, curID), 1000);
		
		curID++;
	}

	public void receive(Object message)
	{
		if (message instanceof String)
		{
			String str = (String)message;
			
			// initial identification with neighbors (see sendIdentifyRequests())
			if (str.equals("identify"))
			{
				System.out.println("Agent " + agent.getId().getName() + " received " + (String)message);
				
				// wait for all agents to get running
				long lastTime = System.currentTimeMillis();
				while (System.currentTimeMillis() - lastTime < 2000);
				
				sendIdentifyRequests();
			}
			// report to monitor
			else if (str.equals("report"))
			{
				getSender().tell(new MonitorReport(agent.active(), agent.getTotalMem(), agent.getFreeMem() ), getSelf()); //TODO: add params for memory
				try{
					logWriter.write((agent.getTotalMem()-agent.getFreeMem()) + "\n");
					logWriter.flush();
				} catch (IOException e){
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
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
				send(responder, new InfoRequest());
			}
		}
		else if (message instanceof InfoRequest)
		{
			// respond to InfoRequest with an InfoResponse (contains our name)
			System.out.println(agent.getId().getName() + " received info request");
			send(getSender(), new InfoResponse(agent.getId().getName()));
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
        else if (message instanceof ReadyMessage)
        {
            agent.receiveReadyMessage((ReadyMessage)message);
            System.err.println("Received ready message from neighbor");
        }
        else if (message instanceof KillMessage)
        {
        	System.out.println("agent done");
        	agent.done();
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
	
	
	private class AckTask extends TimerTask
	{
		private ActorRef receiver;
		private Object message;
		private int id;
		
		
		public AckTask(ActorRef receiver, Object message, int id)
		{
			this.receiver = receiver;
			this.message = message;
			this.id = id;
		}
		
		
		@Override
		public void run()
		{
			if (acks.containsKey(id))
			{
				receiver.tell(new Pack(id, message), getSelf());
				
				acks.get(id).cancel();
				
				Timer timer = new Timer();
				timer.schedule(new AckTask(receiver, message, id), 1000);
				acks.put(id, timer);
			}
		}
	}
}

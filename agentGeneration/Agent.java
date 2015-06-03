
import akka.actor.UntypedActor;


public class Agent extends UntypedActor
{
	public Agent(int id)
	{
		System.out.println("Agent created; ID: " + id);
	}


	@Override
	public void onReceive(Object message)
	{
		if (message instanceof Integer)
		{
			//System.out.println("Agent created; ID: " + (Integer)message);
		}
		else
		{
			unhandled(message);
		}
	}
}

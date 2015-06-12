
import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.actor.SuppressedDeadLetter;
import java.util.List;
import java.util.ArrayList;

public class Agent extends UntypedActor
{
    String name;
	List<Variable> assignedVars;
	int sent = 0;
	int rec = 0;
	
	public Agent(int id, String n)
	{
		name = n;
		assignedVars = new ArrayList<Variable>();
	}


	@Override
	public void onReceive(Object message)
	{
		if (message instanceof Integer)
		{
			//System.out.println("Agent created; ID: " + (Integer)message);
		}
		else if(message instanceof Variable){
			System.err.println("Var received");
			assignedVars.add( (Variable) message);
			if (getSender() != ActorRef.noSender()){
				getSender().tell("got variable: " + ((Variable)message).name, null);
			}
		}
		else if (message instanceof String){
			if(  ((String)message).equals("out")  ){
				System.out.println("In agent " + name);
				for (Variable v : assignedVars){
					System.out.println("\tVar assigned to " + name + ": " + v.name);
				}
			} else if (((String)message).equals("getName")){
                
            } else if (((String)message).contains("got variable")){
				System.err.println( ((String)message).substring(13));
				rec++;
				if (rec == sent)
				{
					System.out.println("All done!");
				}
			}
			else 
				System.out.println(  (String)message );
			
		}
		else if ( message instanceof AgentGenerator.PopulateMessage  ){
			System.err.println("Populate Message Received" );
			for( ActorRef a: ((AgentGenerator.PopulateMessage)message).toSend ){
				a.tell( ((AgentGenerator.PopulateMessage)message).value, getSelf());
				sent++;
			}
			
		}
		else
		{
			unhandled(message);
		}
	}
}

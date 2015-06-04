
import akka.actor.UntypedActor;
import java.util.List;
import java.util.ArrayList;

public class Agent extends UntypedActor
{
    String name;
	List<Variable> assignedVars;
	
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
			assignedVars.add( (Variable) message);
		}
		else if (message instanceof String){
			if(  ((String)message).equals("out")  ){
				System.out.println("In agent " + name);
				for (Variable v : assignedVars){
					System.out.println("\tVar assigned to " + name + ": " + v.name);
				}
			} else if (((String)message).equals("getName")){
                
            }
			
		}
		else
		{
			unhandled(message);
		}
	}
}

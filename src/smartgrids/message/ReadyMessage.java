package smartgrids.message;

import java.io.Serializable;
import akka.actor.ActorRef;

@SuppressWarnings("serial")
public class ReadyMessage implements Serializable{
	boolean isReady;
	ActorRef sentFrom;
	String name;
	
	public ReadyMessage(){
		isReady = true;
		sentFrom = null;
	}
	
	public ReadyMessage(boolean r, String s){
		isReady = r;
		name = s;
	}
	
    public String getName(){
        return name;
    }
    
    public boolean getIsReady(){
        return isReady;
    }
    
    public ActorRef getActorRef(){
        return sentFrom;
    }
	
}
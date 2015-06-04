import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.*;
import java.util.*;
import java.io.*;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;


public class AgentGenerator{
	
	public static List<Variable> vars;
	public static List<Domain> doms;
	public static List<ActorRef> agents;
	
	public static void main(String args[]){
		SAXParserFactory spf = SAXParserFactory.newInstance();
	
		try{
			//InputStream file = new FileInputStream("test.txt");
			SAXParser sp = spf.newSAXParser();
			AgentParseHandler handler = new AgentParseHandler();
			sp.parse(new File("test.txt"), handler  );
			
			vars = handler.getVars();
			doms = handler.getDoms();
			agents = handler.getAgents();
            
            //to be improved later
            // for (Variable v : vars){
                // for (ActorRef a : agents){
                    // if (a.name.equals(v.agentName)){
                        // a.tell("out", null);
                        // System.err.println("var assigned");
                        // break;
                    // }
                // }
            // }
            
            
            
			for(ActorRef a : agents){  //agents and domains successfully generated
				//System.out.println(var.agentName);
				a.tell("out" , null);
			}
			
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}
	
}
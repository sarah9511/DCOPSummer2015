package smartgrids;

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


public class AgentGenerator
{
	public static List<Variable> vars;
	public static List<Domain> doms;
	public static List<ActorRef> agents;
	public static List<Constraint> constraints;
    public static List<Relation> relations;
    public static List<AgentVariableMapper> avMap;
    
	
	public static void main(String args[]){
		SAXParserFactory spf = SAXParserFactory.newInstance();
	
		try{
			//InputStream file = new FileInputStream("test.txt");
			SAXParser sp = spf.newSAXParser();
			AgentParseHandler handler = new AgentParseHandler();
			System.out.println("We have arrived1.");
			sp.parse(new File("test/inputs/dcop2agts.xml"), handler  );
			System.out.println("We have arrived.");
			
			vars = handler.getVars();
			doms = handler.getDoms();
			agents = handler.getAgents();
            constraints = handler.getCons();
            relations = handler.getRelations();
            avMap = handler.getMap();
            
            
			//for(ActorRef a : agents){  //agents and domains successfully generated
				//System.out.println(var.agentName);
				agents.get(0).tell(new PopulateMessage( new IntVariable( "test", "three_colors", "all" ) , agents) , null);
				//a.tell("out", null);
			//}
			
            for (Constraint c : constraints){
                for(Relation r : relations){                //find referenced relation
                    if ( r.name.equals( c.reference )  ){
                        c.relation = r; 
                        break;
                    }
                }
            }
			
            for(AgentVariableMapper avm : avMap){
                //System.err.println( "Name in mapper: " + avm.variableName);
                for( Constraint c : constraints ){
                    for( String target : c.scope ){
                        if (  target.equals( avm.variableName )  ){
                            avm.actor.tell(c, ActorRef.noSender() );
                        }
                    }
                }
            }
            
			
			
		} catch (Exception e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static class PopulateMessage{
		public List<ActorRef> toSend;
		public Variable value;
		
		public PopulateMessage( Variable v, List<ActorRef> bros ){
			value = v;
			toSend = bros;
		}
		
	}
}
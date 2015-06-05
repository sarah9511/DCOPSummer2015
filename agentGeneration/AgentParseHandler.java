import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.ConfigFactory;

import javax.xml.parsers.*;
import java.util.*;
import java.io.*;

public class AgentParseHandler extends DefaultHandler{
	
	public static List<Variable> varList = new ArrayList<Variable>();
	public static List<Domain> domList = new ArrayList<Domain>();
	public static List<ActorRef> agentList = new ArrayList<ActorRef>();
	public static List<Constraint> conList = new ArrayList<Constraint>();
   	public static List<Relation> relationList = new ArrayList<Relation>();
   	
	private Variable thingy;
	private Domain domain;
	private ActorRef agent;
	private Constraint constraint;
   	private Relation relation;
	static int tagType = -1; // 1=var 2=Domain 3=agent
	
	final ActorSystem system;
	
	private int agentCounter = 0;
	
	public AgentParseHandler(){
		system = ActorSystem.create("AgentSystem", ConfigFactory.load(("agentConf")));
	}
	
	public List<Variable> getVars(){
		return varList;
	}
	public List<Domain> getDoms(){
		return domList;
	}
	
	public List<ActorRef> getAgents(){
		return agentList;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException
	{
		if(qName.equalsIgnoreCase("variable")){
			//System.err.println("var found");
			tagType = 1;
			if (attributes.getValue("datatype").equals("int")){
				thingy = new IntVariable( attributes.getValue("name"), attributes.getValue("domain"), attributes.getValue("agent"));
			}
            
			
		}
		else if (qName.equalsIgnoreCase("domain")){
			tagType = 2;
			if (attributes.getValue("datatype").equals("int")){
				domain = new Domain<Integer>( attributes.getValue("name"), attributes.getValue("datatype"));
				
			}
			
		}
		else if (qName.equalsIgnoreCase("agent"))
		{
            //because actor vars cannot be declared directly, must use attribute.getValue("name")
            //  so, variables must be created first in xml file first
			tagType = 3;
			agent = system.actorOf(Props.create(Agent.class, agentCounter, attributes.getValue("name")), "agent" + agentCounter);
			agentCounter++;
			for(Variable v: varList){
                if (  v.agentName.equals( attributes.getValue("name") )  ){
                    agent.tell(v, null);
                }
            }
            
              if (qName.equalsIgnoreCase( "relation" )){
      
         	tagType = 4;
         	if(true){
            
            		relation = new Relation(attributes.getValue("name"), Integer.parseInt(attributes.getValue("arity"))); // constructing new relation object
            		relation.setNBTuples(Integer.parseInt(attributes.getValue("nbTuples")));
            
         	}//closing if
      		}//closing relation case 
            
			//agent.tell(new Integer(agentCounter), null);
		}
	}
	
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("variable")) {
            varList.add(thingy);
        }
		else if (qName.equalsIgnoreCase("domain")) {
            domList.add(domain);
        }
          
        if(qName.equalsIgnoreCase("relation")){
            //System.out.println("Got to the relation if");
            relationList.add(relation);
            System.out.println(relation.toString());     
        }
		else if (qName.equalsIgnoreCase("agent"))
		{
            
            agentList.add(agent);
		}
		
    }
	
 public void characters(char ch[], int start, int length) throws SAXException {
      //System.out.println("Got to characters.");
		String toParse = new String(ch, start, length);
      switch(tagType){//open switch
      //DOMAIN case
		case 2:
			if (domain.typeCase.equals("int")){
				int begin = Integer.parseInt( toParse.substring(0, toParse.indexOf("..")  ) ); System.err.println("begin: " + begin);
				int end = Integer.parseInt( toParse.substring(toParse.indexOf("..") + 2 , toParse.length()  ) );System.err.println("end: " + end);
				for (int i = start; i < end; i++){
					domain.possibles.add(i);
				}
				tagType = -1;
			}
		break;
      
      //RELATION case
      case 4:
         String res = toParse.replaceAll("^\\s+", "");//removing leading whitespace

         res = res.substring(0, res.indexOf(":")); //trimming off the function value
         System.out.println("Function value is: " + res);
         if(res.equalsIgnoreCase("infinity") || res.equalsIgnoreCase("infinite")){
            relation.setFVal(Float.POSITIVE_INFINITY); //setting the value equal to numerical infinity.
         }//closing 
         
         else{ // the string is an actual number (or it should be )
            relation.setFVal(Float.parseFloat(res)); //extracting number
         }//closing else
         //System.out.println("toParse before : " + toParse);
         toParse = toParse.substring(toParse.indexOf(":") + 1);
         String tp[] = new String[relation.getNBTuples()];
         tp = toParse.split("[|]");
        // System.out.println("toParse after : " + toParse);
         
         for(int i = 0; i < tp.length; i++){
            //System.out.println("tp[i] 1: " + tp[i]);
	         tp[i] = tp[i].replaceAll("^\\s+", "");//removing leading whitespace
           // System.out.println("tp[i] 2: " + tp[i]);
            ArrayList<String> tps = new ArrayList();
            for(int j = 0; j < relation.getArity(); j++){
               if(j+1 < relation.getArity()){
                  //System.out.println("Adding: " + tp[i].substring(0,tp[i].indexOf(" ")));
                  tps.add(tp[i].substring(0,tp[i].indexOf(" ")));
                  tp[i] = tp[i].substring(tp[i].indexOf(" ") + 1);
                  //System.out.println("Left with: " + tp[i]);
               }
               else {
                 // System.out.println("Adding: " + tp[i]);
                  tps.add(tp[i]);
               }
            }//closing inner for loop
            relation.addTuple(tps);   
               
         }//closing for loop
         tagType = -1;
         break;
      }//closing switch statement 
	}//closing character method 

	
	

}

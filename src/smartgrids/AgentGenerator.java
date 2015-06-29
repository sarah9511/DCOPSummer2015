package smartgrids;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import com.typesafe.config.ConfigFactory;


public class AgentGenerator extends DefaultHandler
{
	private String name;
	private String ip;
	private int port;
	
	private Identifier id;
	
	private HashMap<String, Domain> domains = new HashMap<>();
	private HashMap<String, Variable> variables = new HashMap<>();
	private HashMap<String, Relation> relations = new HashMap<>();
	private HashMap<String, Constraint> constraints = new HashMap<>();
	
	private HashMap<String, Identifier> neighbors = new HashMap<>();
	
	
	private Domain curDomain;
	private Relation curRelation;
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
	{
		if (qName.equals("id"))
		{
			name = attributes.getValue("name");
			ip = attributes.getValue("ip");
			port = Integer.parseInt(attributes.getValue("port"));
			
			id = new Identifier(name, ip, port);
		}
		else if (qName.equals("domain"))
		{
			String name = attributes.getValue("name");
			String type = attributes.getValue("datatype");
			
			if (type.equals("int"))
			{
				curDomain = new Domain<Integer>(name, type);
			}
		}
		else if (qName.equals("variable"))
		{
			String name = attributes.getValue("name");
			String type = attributes.getValue("datatype");
			String domainName = attributes.getValue("domain");
			
			if (type.equals("int"))
			{
				Domain<Integer> domain = domains.get(domainName);
				variables.put(name, new Variable<Integer>(name, type, domain));
			}
		}
		else if (qName.equals("relation"))
		{
			String name = attributes.getValue("name");
			int arity = Integer.parseInt(attributes.getValue("arity"));
			
			String defaultCostString = attributes.getValue("defaultCost");
			int defaultCost;
			if (defaultCostString.equals("infinity"))
			{
				defaultCost = Integer.MAX_VALUE;
			}
			else
			{
				defaultCost = Integer.parseInt(defaultCostString);
			}
			
			String semantics = attributes.getValue("semantics");
			
			curRelation = new Relation(name, arity, defaultCost, semantics);
		}
		else if (qName.equals("constraint"))
		{
			String name = attributes.getValue("name");
			int arity = Integer.parseInt(attributes.getValue("arity"));
			String scope = attributes.getValue("scope");
			String reference = attributes.getValue("reference");
			
			Relation relation = relations.get(reference);
			
			constraints.put(name, new Constraint(name, arity, scope, relation));
		}
		else if (qName.equals("neighbor"))
		{
			String name = attributes.getValue("name");
			String ip = attributes.getValue("ip");
			int port = Integer.parseInt(attributes.getValue("port"));
			
			neighbors.put(name, new Identifier(name, ip, port));
		}
	}
	
	@Override
	public void characters(char ch[], int start, int length) throws SAXException
	{
		String toParse = new String(ch, start, length);
		
		if (curDomain != null)
		{
			int delIndex = toParse.indexOf("..");
			int min = Integer.parseInt(toParse.substring(0, delIndex));
			int max = Integer.parseInt(toParse.substring(delIndex + 2));
			
			for (int i = min; i <= max; i++)
			{
				curDomain.addValue(i);
			}
		}
		else if (curRelation != null)
		{
			curRelation.createTuples(toParse);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if (qName.equals("domain"))
		{
			domains.put(curDomain.getName(), curDomain);
			curDomain = null;
		}
		else if (qName.equals("relation"))
		{
			relations.put(curRelation.getName(), curRelation);
			curRelation = null;
		}
		else if (qName.equals("agent"))
		{
			//generateAgent();
		}
    }
	
	
	public void generateAgent()
	{
		final ActorSystem system = ActorSystem.create(name + "System", ConfigFactory.load("config/" + name + "conf"));
		final ActorRef agent = system.actorOf(Props.create(Agent.class, id, domains, variables, relations, constraints, neighbors), name);
		
		id.setActorRef(agent);
		agent.tell("identify", null);
	}
	 
	 
	public static void main(String[] args)
	{
		SAXParserFactory spf = SAXParserFactory.newInstance();
		
	 	try
	 	{
	 		SAXParser sp = spf.newSAXParser();
	 		AgentGenerator handler = new AgentGenerator();
	 		sp.parse(new File(args[0]), handler);
	 		/*sp.parse(new File("test/inputs/agents/agent1.xml"), handler);
	 		sp.parse(new File("test/inputs/agents/agent2.xml"), handler);
	 		sp.parse(new File("test/inputs/agents/agent3.xml"), handler);
	 		*/
	 	}
	 	catch (Exception e)
	 	{
	 		System.err.println(e.getMessage());
	 		e.printStackTrace();
	 	}
	}
}
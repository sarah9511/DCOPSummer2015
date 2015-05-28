import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.*;
import java.util.*;
import java.io.*;


public class AgentGenerator{
	
	public static List<Variable> vars;
	public static List<Domain> doms;
	
	public static void main(String args[]){
		SAXParserFactory spf = SAXParserFactory.newInstance();
	
		try{
			//InputStream file = new FileInputStream("test.txt");
			SAXParser sp = spf.newSAXParser();
			AgentParseHandler handler = new AgentParseHandler();
			sp.parse(new File("test.txt"), handler  );
			
			vars = handler.getVars();
			doms = handler.getDoms();
			
			for(Domain dom : doms){  //agents and domains successfully generated
				System.out.println(dom.domainName);
			}
			
		} catch (Exception e){
			System.err.println(e.getMessage());
		}
	}
	
}
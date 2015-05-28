import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.*;
import java.util.*;
import java.io.*;

public class AgentParseHandler extends DefaultHandler{
	
	public static List<Variable> varList = new ArrayList<Variable>();
	public static List<Domain> domList = new ArrayList<Domain>();
	
	private Variable thingy;
	private Domain domain;
	
	static int tagType = -1; // 1= var 2=Domain   
	
	public AgentParseHandler(){
		
	}
	
	public List<Variable> getVars(){
		return varList;
	}
	public List<Domain> getDoms(){
		return domList;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
		throws SAXException
	{
		if(qName.equalsIgnoreCase("variable")){
			//System.err.println("var found");
			tagType = 1;
			if (attributes.getValue("datatype").equals("int")){
				thingy = new IntVariable( attributes.getValue("name"), attributes.getValue("domain"), attributes.getValue("agent")  );
			}
			
		}
		
		if (qName.equalsIgnoreCase( "domain" ) ){
			tagType = 2;
			if (attributes.getValue("datatype").equals("int")  ){
				domain = new Domain<Integer>( attributes.getValue("name"), attributes.getValue("datatype") );
				
			}
			
		}
		
	}
	
	
	public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("variable")) {
            varList.add(thingy);
        }
		if (qName.equalsIgnoreCase("domain")) {
            domList.add(domain);
        }
		
    }
	

	 public void characters(char ch[], int start, int length) throws SAXException {
		String toParse = new String(ch, start, length);
		if (tagType == 2){
			if (domain.typeCase.equals("int")){
				int begin = Integer.parseInt( toParse.substring(0, toParse.indexOf("..")  ) ); System.err.println("begin: " + begin);
				int end = Integer.parseInt( toParse.substring(toParse.indexOf("..") + 2 , toParse.length()  ) );System.err.println("end: " + end);
				for (int i = start; i < end; i++){
					domain.possibles.add(i);
				}
				tagType = -1;
			}
		}	
		 
	 }
	
	

}
package smartgrids;


public class IntVariable extends Variable
{
	private int value;
	Domain dm;
	
	public IntVariable(String n, String dn, String aName ){
		name = n;
		domainName = dn;
		agentName = aName;
		value = -1;
	}

	// public Integer getValue(){

		// return (Integer)value;

	// }//closing getValue

	// public void setValue(Integer i){

		// value = i;

	// }

	int getValue(){
		return value;
	}
	void setValue(int O){
		value = O;
	}
	
}
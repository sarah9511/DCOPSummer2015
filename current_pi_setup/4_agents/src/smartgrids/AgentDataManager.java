package smartgrids;
import java.sql.*;//importing the sql packages


public class AgentDataManager {
	
	private Connection connect = null;
	private Statement stmt = null;
	private ResultSet rs = null;
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	
	public AgentDataManager(){
		try{
			Class.forName(JDBC_DRIVER);
			String serverName = "169.254.246.125:3306";
			String mydatabase = "DCOP2015";
			String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
			String username = "agent";
			String password = "password";
			connect = DriverManager.getConnection(url, username, password);
			stmt = connect.createStatement();
			//deleteAgent(agtName);
		}//closing try
		catch(Exception e){
			System.out.println("Did not successfully connect.");
			System.out.println(e);
		}//closing catch
		
		
	}//closing
	
	public void updateValue(String agtName, String varVal, int val){
		
		String call = "call `DCOP2015`.`updateValue`('" + agtName + "' , '" + varVal + "', " + val + ");";//calling our update procedure
		try {
			stmt.execute(call);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}//closing updateValue
	
	public void clear(){
		String call = "call `DCOP2015`. `clear`();";//calling our update procedure
		try {
			stmt.execute(call);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void deleteAgent(String agtName){
		
		
		String call = "call `DCOP2015`, `deleteAgent`('" + agtName + "');";//calling our update procedure
		try {
			stmt.execute(call);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//closing deleteAgent
	
	public void endConnect() throws Exception{
		//deleteAgent(agtName);
		rs.close();
		stmt.close();
		connect.close();
		
		connect = null;
		rs = null;
		stmt = null;
		
	}//closing endConnect
	
}

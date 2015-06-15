import java.util.ArrayList;
public class Domain<T>{
	
	ArrayList<T> possibles;
	String domainName;
	String typeCase;
	
	public Domain(String dn, String dt ){
		domainName = dn;
		typeCase = dt;
	}

}
package smartgrids.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Pack implements Serializable
{
	public int id;
	public Object message;
	
	
	public Pack(int id, Object message)
	{
		this.id = id;
		this.message = message;
	}
}

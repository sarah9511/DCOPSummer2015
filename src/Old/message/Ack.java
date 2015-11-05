package smartgrids.message;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Ack implements Serializable
{
	public int id;
	
	
	public Ack(int id)
	{
		this.id = id;
	}
}

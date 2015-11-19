package zwapi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.zwave4j.Manager;
import org.zwave4j.NativeLibraryLoader;
import org.zwave4j.Notification;
import org.zwave4j.NotificationWatcher;
import org.zwave4j.Options;
import org.zwave4j.ValueId;
import org.zwave4j.ZWave4j;

public class ZWController
{
	private final String controllerPort;
	
	private long homeId;
	private boolean ready = false;
	
	private final Manager manager;
	private final NotificationWatcher watcher;
	
	private HashMap<String, Short> devices;
	
	
	public ZWController(String configPath, String controllerPort)
	{
		this.controllerPort = controllerPort;
		
		NativeLibraryLoader.loadLibrary(ZWave4j.LIBRARY_NAME, ZWave4j.class);
		
		final Options options = Options.create(configPath, "", "");
		options.addOptionBool("ConsoleOutput", false);
		options.lock();

		manager = Manager.create();
		
		devices = new HashMap<>();
		
		final ArrayList<Short> ids = new ArrayList<>();

		watcher = new NotificationWatcher()
		{
			@Override
			public void onNotification(Notification notification, Object context)
			{
				switch (notification.getType())
				{
					case DRIVER_READY:
						System.out.println(String.format("Driver ready\n" + "\thome id: %d", notification.getHomeId()));
						homeId = notification.getHomeId();
						break;
					case DRIVER_FAILED:
						System.out.println("Driver failed");
						break;
					case DRIVER_RESET:
						System.out.println("Driver reset");
						break;
					case AWAKE_NODES_QUERIED:
						System.out.println("Awake nodes queried");
						break;
					case ALL_NODES_QUERIED:
						System.out.println("All nodes queried");
						manager.writeConfig(homeId);
						ready = true;
						break;
					case ALL_NODES_QUERIED_SOME_DEAD:
						System.out.println("All nodes queried some dead");
						manager.writeConfig(homeId);
						ready = true;
						break;
					case POLLING_ENABLED:
						System.out.println("Polling enabled");
						break;
					case POLLING_DISABLED:
						System.out.println("Polling disabled");
						break;
					case NODE_NEW:
						System.out.println(String.format("Node new\n" + "\tnode id: %d", notification.getNodeId()));
						break;
					case NODE_ADDED:
						System.out.println(String.format("Node added\n" + "\tnode id: %d", notification.getNodeId()));
						ids.add(notification.getNodeId());
						break;
					case NODE_REMOVED:
						System.out.println(String.format("Node removed\n" + "\tnode id: %d", notification.getNodeId()));
						break;
					case ESSENTIAL_NODE_QUERIES_COMPLETE:
						System.out.println(String.format("Node essential queries complete\n" + "\tnode id: %d", notification.getNodeId()));
						break;
					case NODE_QUERIES_COMPLETE:
						System.out.println(String.format("Node queries complete\n" + "\tnode id: %d", notification.getNodeId()));
						break;
					case NODE_EVENT:
						System.out.println(String.format("Node event\n" + "\tnode id: %d\n" + "\tevent id: %d", notification.getNodeId(), notification.getEvent()));
						break;
					case NODE_NAMING:
						System.out.println(String.format("Node naming\n" + "\tnode id: %d", notification.getNodeId()));
						break;
					case NODE_PROTOCOL_INFO:
						System.out.println(String.format("Node protocol info\n" + "\tnode id: %d\n" + "\ttype: %s", notification.getNodeId(), manager.getNodeType(notification.getHomeId(), notification.getNodeId())));
						break;
					case VALUE_ADDED:
						System.out.println(String.format("Value added\n" +
								"\tnode id: %d\n" +
								"\tcommand class: %d\n" +
								"\tinstance: %d\n" +
								"\tindex: %d\n" +
								"\tgenre: %s\n" +
								"\ttype: %s\n" +
								"\tlabel: %s\n" +
								"\tvalue: %s",
								notification.getNodeId(),
								notification.getValueId().getCommandClassId(),
								notification.getValueId().getInstance(),
								notification.getValueId().getIndex(),
								notification.getValueId().getGenre().name(),
								notification.getValueId().getType().name(),
								manager.getValueLabel(notification.getValueId()),
								getValue(notification.getValueId())
						));
						break;
					case VALUE_REMOVED:
						System.out.println(String.format("Value removed\n" +
								"\tnode id: %d\n" +
								"\tcommand class: %d\n" +
								"\tinstance: %d\n" +
								"\tindex: %d",
								notification.getNodeId(),
								notification.getValueId().getCommandClassId(),
								notification.getValueId().getInstance(),
								notification.getValueId().getIndex()
						));
						break;
					case VALUE_CHANGED:
						System.out.println(String.format("Value changed\n" +
								"\tnode id: %d\n" +
								"\tcommand class: %d\n" +
								"\tinstance: %d\n" +
								"\tindex: %d\n" +
								"\tvalue: %s",
								notification.getNodeId(),
								notification.getValueId().getCommandClassId(),
								notification.getValueId().getInstance(),
								notification.getValueId().getIndex(),
								getValue(notification.getValueId())
						));
						break;
					case VALUE_REFRESHED:
						System.out.println(String.format("Value refreshed\n" +
								"\tnode id: %d\n" +
								"\tcommand class: %d\n" +
								"\tinstance: %d\n" +
								"\tindex: %d" +
								"\tvalue: %s",
								notification.getNodeId(),
								notification.getValueId().getCommandClassId(),
								notification.getValueId().getInstance(),
								notification.getValueId().getIndex(),
								getValue(notification.getValueId())
						));
						break;
					case GROUP:
						System.out.println(String.format("Group\n" + "\tnode id: %d\n" + "\tgroup id: %d", notification.getNodeId(), notification.getGroupIdx()));
						break;
					case SCENE_EVENT:
						System.out.println(String.format("Scene event\n" + "\tscene id: %d", notification.getSceneId()));
						break;
					case CREATE_BUTTON:
						System.out.println(String.format("Button create\n" + "\tbutton id: %d", notification.getButtonId()));
						break;
					case DELETE_BUTTON:
						System.out.println(String.format("Button delete\n" + "\tbutton id: %d", notification.getButtonId()));
						break;
					case BUTTON_ON:
						System.out.println(String.format("Button on\n" + "\tbutton id: %d", notification.getButtonId()));
						break;
					case BUTTON_OFF:
						System.out.println(String.format("Button off\n" + "\tbutton id: %d", notification.getButtonId()));
						break;
					case NOTIFICATION:
						System.out.println("Notification");
						break;
					default:
						System.out.println(notification.getType().name());
						break;
				}
			}
		};
		manager.addWatcher(watcher, null);

		manager.addDriver(controllerPort);
		
		while (!ready)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		File file = new File("devices.csv");
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader("devices.csv"));
			
			String line;
			while ((line = br.readLine()) != null)
			{
				String[] values = line.replace(" ", "").split(",");
				
				short id = Short.parseShort(values[0]);
				String name = values[1];
				
				if (ids.contains(id))
				{
					devices.put(name, id);
				}
				else
				{
					System.err.println("Missing device! ID: " + id + ", name: " + name);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (br != null) br.close();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		Scanner scan = new Scanner(System.in);
		
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			
			for (Short id : ids)
			{
				if (id != 1 && !devices.values().contains(id))
				{
					System.out.println("New device with id " + id + "; What's its name?");
					String name = scan.nextLine();
					
					devices.put(name, id);
					bw.write(id + ", " + name + '\n');
					
					System.out.println("Added device " + id + " with name " + name);
				}
			}
			
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		scan.close();
	}
	
	
	public void allOn()
	{
		manager.switchAllOn(homeId);
	}
	
	public void allOff()
	{
		manager.switchAllOff(homeId);
	}
	
	
	public void switchOn(String name)
	{
		manager.setNodeOn(homeId, devices.get(name));
	}
	
	public void switchOff(String name)
	{
		manager.setNodeOff(homeId, devices.get(name));
	}
	
	
	public boolean ready()
	{
		return ready;
	}
	
	public Set<String> getDeviceNames()
	{
		return devices.keySet();
	}
	
	
	public void destroy()
	{
		manager.removeWatcher(watcher, null);
		manager.removeDriver(controllerPort);
		Manager.destroy();
		Options.destroy();
	}
	
	
	private Object getValue(ValueId valueId)
	{
		switch (valueId.getType())
		{
			case BOOL:
				AtomicReference<Boolean> b = new AtomicReference<>();
				Manager.get().getValueAsBool(valueId, b);
				return b.get();
			case BYTE:
				AtomicReference<Short> bb = new AtomicReference<>();
				Manager.get().getValueAsByte(valueId, bb);
				return bb.get();
			case DECIMAL:
				AtomicReference<Float> f = new AtomicReference<>();
				Manager.get().getValueAsFloat(valueId, f);
				return f.get();
			case INT:
				AtomicReference<Integer> i = new AtomicReference<>();
				Manager.get().getValueAsInt(valueId, i);
				return i.get();
			case LIST:
				return null;
			case SCHEDULE:
				return null;
			case SHORT:
				AtomicReference<Short> s = new AtomicReference<>();
				Manager.get().getValueAsShort(valueId, s);
				return s.get();
			case STRING:
				AtomicReference<String> ss = new AtomicReference<>();
				Manager.get().getValueAsString(valueId, ss);
				return ss.get();
			case BUTTON:
				return null;
			case RAW:
				AtomicReference<short[]> sss = new AtomicReference<>();
				Manager.get().getValueAsRaw(valueId, sss);
				return sss.get();
			default:
				return null;
		}
	}
	
	
	public static void main(String[] args)
	{
		ZWController controller = new ZWController("C:/Programming/Java/ZWaveAPI/config", "//./COM3");
		
		controller.allOn();
		
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		controller.allOff();
		
		try
		{
			Thread.sleep(500);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

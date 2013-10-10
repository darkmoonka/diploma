package diploma.vb.szt.agent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.hyperic.sigar.Sigar;

public class Main
{
	private final static String ITEM = "item";

	public static void main(String[] args) throws Exception
	{
		System.out.println("java.library.path: "
				+ System.getProperty("java.library.path"));
		Sigar sigar = new Sigar();
		Os os = new Os(sigar);
		Cpu cpu = new Cpu(sigar);
		Storage storage = new Storage(sigar);
		Memory memory = new Memory(sigar);
		Process process = new Process(sigar);

		ArrayList<MonitoredItem> items = new ArrayList<MonitoredItem>();
		items.add(os);
		items.add(cpu);
		items.add(storage);
		items.add(memory);
		items.add(process);

		String result = "<?xml version=\"1.0\" ?> " + "<" + ITEM + ">";

		for (MonitoredItem item : items)
			result += item.toXml();

		result = result + "</" + ITEM + ">";

		System.out.println(result);
		// System.setProperty("java.library.path", System.getProperties()
		// .getProperty("java.library.path") + "/lib");
		// System.out.println(System.getProperties().getProperty(
		// "java.library.path"));
		// Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths"
		// );
		// fieldSysPath.setAccessible( true );
		// fieldSysPath.set( null, null );
		
		sendPost(result);
	}
	private static void sendPost(String data) throws Exception {
		 
		String url = "http://localhost:8080/monitor/postData";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
 
 
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();
 
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + data);
		System.out.println("Response Code : " + responseCode);
 
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
 
		System.out.println(response.toString());
	}
}
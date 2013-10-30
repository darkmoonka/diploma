package diploma.vb.szt.agent;

import java.security.KeyPair;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import org.apache.commons.configuration.XMLConfiguration;
import org.hyperic.sigar.Sigar;

public class Main
{
	private final static String ITEM = "item";

	public static void main(String[] args) throws Exception
	{
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

		XMLConfiguration configer = new XMLConfiguration("config.xml");

		String keyPath = (String) configer.getProperty("Key.Path");
		int keyLength = Integer.valueOf((String) configer
				.getProperty("Key.Length"));

		SecretKey aesKey = Keys.generateSymmetricKey();
		KeyPair keyPair = Keys.getKeyPair(keyPath, keyLength);
		result = Keys.encryptString(result, aesKey);
		byte[] encryptedAES = Keys.encryptAES(keyPair.getPublic(), aesKey);

		// TODO minta
		System.out
				.println("2: "
						+ Keys.decryptString(result, keyPair.getPrivate(),
								encryptedAES));

		String macAddress = Communication.getMacAddress();

		String protocol = (String) configer.getProperty("Server.Protocol");
		String address = (String) configer.getProperty("Server.Address");
		String port = (String) configer.getProperty("Server.Port");
		String url = protocol + "://" + address + ":" + port;

		Communication.register(url + "/monitor/registerAgent", macAddress,
				keyPair.getPublic().getEncoded());

//		 Communication.sendData(url + "/monitor/postData", result);
	}
}
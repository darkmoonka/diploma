package diploma.vb.szt.agent;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;

import javax.crypto.SecretKey;

import org.apache.commons.configuration.XMLConfiguration;
import org.hyperic.sigar.Sigar;

public class Main
{
	private final static String ITEM = "item";

	public static void main(String[] args)
	{
		try
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

			String xmlData = "<?xml version=\"1.0\"?>" + "<" + ITEM + ">";

			for (MonitoredItem item : items)
				xmlData += item.toXml();

			xmlData = xmlData + "</" + ITEM + ">";

			System.out.println(xmlData);

			XMLConfiguration configer = new XMLConfiguration("config.xml");

			SecretKey aesKey = Keys.generateSymmetricKey();
			KeyPair keyPair = Keys.getKeyPair();
			String result = Keys.encryptString(xmlData, aesKey);
			byte[] encryptedAES = Keys.encryptAES(keyPair.getPublic(), aesKey);

			// TODO minta
			System.out.println("2: "
					+ Keys.decryptString(result, keyPair.getPrivate(),
							encryptedAES));

			String macAddress = Communication.getMacAddress();

			String protocol = (String) configer.getProperty("Server.Protocol");
			String address = (String) configer.getProperty("Server.Address");
			String port = (String) configer.getProperty("Server.Port");
			String url = protocol + "://" + address + ":" + port;

			int agentId = Communication.register(
					url + "/monitor/registerAgent", macAddress, keyPair
							.getPublic().getEncoded());

			IO.saveAgentId(agentId);

			PublicKey serverPublicKey = Keys.loadServerPublicKey();

			String encryptedData = Keys.encryptString(xmlData, aesKey);
			String encryptedAgentId = Keys.encryptString("" + agentId, aesKey);
			byte[] encryptedAES2 = Keys.encryptAES(serverPublicKey, aesKey);

			Communication.sendData(url + "/monitor/postData", encryptedData,
					encryptedAgentId, encryptedAES2);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
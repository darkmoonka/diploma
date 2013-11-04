package diploma.vb.szt.agent;

import java.io.File;
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
			XMLConfiguration configer = new XMLConfiguration("config.xml");
			Sigar sigar = new Sigar();

			KeyPair keyPair = Keys.getKeyPair();
			String macAddress = Communication.getMacAddress();

			String protocol = configer.getString("Server.Protocol");
			String address = configer.getString("Server.Address");
			String port = configer.getString("Server.Port");
			String url = protocol + "://" + address + ":" + port;

			String agentIdFile = configer.getString("Agent.idFile");
			File agentFile = new File(agentIdFile);
			int agentId;
			PublicKey serverPublicKey = Keys.loadServerPublicKey();

			if (!agentFile.exists())
			{
				agentId = Communication.register(
						url + "/monitor/registerAgent", macAddress, keyPair
								.getPublic().getEncoded());
				IO.saveAgentId(agentId);
			} else
			{
				agentId = IO.loadAgentId();
			}

			while (true)
			{
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

				SecretKey aesKey = Keys.generateSymmetricKey();

				String result = Keys.encryptString(xmlData, aesKey);
				byte[] encryptedAES = Keys.encryptAES(keyPair.getPublic(),
						aesKey);

				// TODO minta
				System.out.println("2: "
						+ Keys.decryptString(result, keyPair.getPrivate(),
								encryptedAES));

				String encryptedData = Keys.encryptString(xmlData, aesKey);
				String encryptedAgentId = Keys.encryptString("" + agentId,
						aesKey);
				byte[] encryptedAES2 = Keys.encryptAES(serverPublicKey, aesKey);

				os = null;
				cpu = null;
				storage = null;
				memory = null;
				process = null;

				Communication.sendData(url + "/monitor/postData",
						encryptedData, encryptedAgentId, encryptedAES2);
				
				configer = new XMLConfiguration("config.xml");
				int repeatPeriod = configer.getInt("Agent.repeatPeriod");
				Thread.sleep(repeatPeriod);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
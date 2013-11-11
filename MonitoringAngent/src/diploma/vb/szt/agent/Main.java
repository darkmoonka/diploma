package diploma.vb.szt.agent;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

			Iterator<String> allItems = configer.getKeys();
			List<String> features = new ArrayList<String>();
			// TODO use this to get feature list
			while (allItems.hasNext())
			{
				String s = allItems.next();
				if (s.startsWith("Features"))
					features.add(s.toString().replace("Features.", ""));
			}

			List<String> monitoredFeatures = new ArrayList<String>();

			boolean isEnabledOs = configer.getBoolean("Features.Os");
			if (isEnabledOs)
			{
				Field[] fields = Os.class.getDeclaredFields();
				for (Field f : fields)
					if (!((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL))
						monitoredFeatures.add(f.getName());
			}

			boolean isEnabledCpu = configer.getBoolean("Features.Cpu");
			if (isEnabledCpu)
			{
				Field[] fields = Cpu.class.getDeclaredFields();
				for (Field f : fields)
					if (!((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL))
						monitoredFeatures.add(f.getName());
			}

			boolean isEnabledStorage = configer.getBoolean("Features.Storage");
			if (isEnabledStorage)
			{
				Field[] fields = Storage.class.getDeclaredFields();
				for (Field f : fields)
					if (!((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL))
						monitoredFeatures.add(f.getName());
			}

			boolean isEnabledMemory = configer.getBoolean("Features.Memory");
			if (isEnabledMemory)
			{
				Field[] fields = Memory.class.getDeclaredFields();
				for (Field f : fields)
					if (!((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL))
						monitoredFeatures.add(f.getName());
			}

			boolean isEnabledProcess = configer.getBoolean("Features.Process");
			if (isEnabledProcess)
			{
				Field[] fields = Process.class.getDeclaredFields();
				for (Field f : fields)
					if (!((f.getModifiers() & Modifier.FINAL) == Modifier.FINAL))
						monitoredFeatures.add(f.getName());
			}

			KeyPair keyPair = Keys.getKeyPair();

			String protocol = configer.getString("Server.Protocol");
			String address = configer.getString("Server.Address");
			String port = configer.getString("Server.Port");
			String url = protocol + "://" + address + ":" + port;

			String agentName = configer.getString("Agent.Name");

			List<Object> contactNames = configer
					.getList("Contacts.Contact.Name");
			List<Object> contactEmails = configer
					.getList("Contacts.Contact.Email");

			List<Contact> contacts = new ArrayList<Contact>();
			if (contactNames != null && !contactNames.isEmpty()
					&& contactEmails != null && !contactEmails.isEmpty())
			{
				for (int i = 0; i < contactNames.size(); i++)
					contacts.add(new Contact(contactNames.get(i).toString(),
							contactEmails.get(i).toString()));
			}

			String agentIdFile = configer.getString("Agent.idFile");
			File agentFile = new File(agentIdFile);
			int agentId;
			PublicKey serverPublicKey = Keys.loadServerPublicKey();

			if (!agentFile.exists())
			{
				agentId = Communication.register(
						url + "/monitor/registerAgent", keyPair.getPublic()
								.getEncoded(), agentName, monitoredFeatures,
						new Contacts(contacts));

				IO.saveAgentId(agentId);
			} else
			{
				agentId = IO.loadAgentId();
			}

			while (true)
			{
				ArrayList<MonitoredItem> items = new ArrayList<MonitoredItem>();

				boolean isEnabled = configer.getBoolean("Features.Os");
				if (isEnabled)
				{
					Os os = new Os(sigar);
					items.add(os);
				}
				isEnabled = configer.getBoolean("Features.Cpu");
				if (isEnabled)
				{
					Cpu cpu = new Cpu(sigar);
					items.add(cpu);
				}
				isEnabled = configer.getBoolean("Features.Storage");
				if (isEnabled)
				{
					Storage storage = new Storage(sigar);
					items.add(storage);

				}
				isEnabled = configer.getBoolean("Features.Memory");
				if (isEnabled)
				{
					Memory memory = new Memory(sigar);
					items.add(memory);

				}
				isEnabled = configer.getBoolean("Features.Process");
				if (isEnabled)
				{
					Process process = new Process(sigar);
					items.add(process);

				}

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

				if (items.size() != 0)
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
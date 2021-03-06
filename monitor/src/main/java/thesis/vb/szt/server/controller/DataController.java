package thesis.vb.szt.server.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import thesis.vb.szt.server.dao.Dao;
import thesis.vb.szt.server.entity.Agent;
import thesis.vb.szt.server.entity.Contact;
import thesis.vb.szt.server.entity.Contacts;
import thesis.vb.szt.server.security.Keys;
import thesis.vb.szt.server.util.CommunicationData;
import thesis.vb.szt.server.util.Notifier;

@Controller
public class DataController
{
	@Autowired(required = true)
	private Dao dao;

	@Autowired(required = true)
	private Marshaller marshaller;

	@Autowired(required = true)
	private Unmarshaller unMarshaller;

	@Autowired(required = true)
	private ObjectMapper objectMapper;

	@Autowired(required = true)
	private Notifier notifier;

	// @Autowired(required = true)
	// private SecurityService securityService;

	protected static Logger logger = Logger.getLogger("DataController");

	private final String subject = "New registration";
	// TODO kiokosítani
	private final String body = "localhost:8080/monitor/registerUser/";

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void test()
	{
		// try
		// {
		// SecretKey secretKey =
		// Keys.generatySymmetricKeyFromPassword("almafa");
		// String encryptedPassword = securityService.encrypPassword("almafa",
		// secretKey);
		// logger.info("encrypted: " + encryptedPassword);
		// String plainPassword = securityService.decryptPassword(secretKey,
		// encryptedPassword);
		// logger.info("plainPassowrd: " + plainPassword);
		//
		//
		// } catch (Exception e)
		// {
		// logger.error("", e);
		// }
	}

	@RequestMapping(value = "/reports/{address}", method = RequestMethod.GET)
	public String getReportPage(@PathVariable String address, ModelMap model)
	{
		logger.info("Loading reports page");

		model.addAttribute("name", dao.getAgentByAddress(address).getName());
		model.addAttribute("address", address);
		logger.info("Loaded reports page");
		return "reports";
	}

	@RequestMapping(value = "/agent/{mac}", method = RequestMethod.GET)
	public @ResponseBody
	String getAgent(@PathVariable String mac, Model model)
	{
		List<Map<String, String>> reportList = null;
		try
		{
			reportList = dao.getReportsForAgent(mac, -1, -1);
		} catch (Exception e)
		{
			logger.error("Unable to set reports", e);
		}

		try
		{
			String result = objectMapper.writeValueAsString(reportList);
			return result;
		} catch (IOException e)
		{
			logger.error("Unable to marshal reports", e);
		}
		return null;
	}

	// @RequestMapping(value = "/", method = RequestMethod.GET)
	// public String index()
	// {
	// return "index";
	// }

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model)
	{
		List<Agent> agents = dao.getAgents();
		model.addAttribute("agents", agents);
		return "index";
	}

	@RequestMapping(value = "/agent", method = RequestMethod.GET)
	public String agent()
	{
		return "agent";
	}

	@RequestMapping(value = "/init", method = RequestMethod.GET)
	public @ResponseBody
	String init()
	{
		List<Map<String, String>> agentNames = dao.getAgentNamesAndMacs();
		String result = null;
		try
		{
			result = objectMapper.writeValueAsString(agentNames);
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return result;
	}

	// TODO szétszedni 2 kontrollerré, egyik az agent feltöltéseit nézi, másik a
	// felületet szolgálja ki
	@RequestMapping(value = "/postData", method = RequestMethod.POST)
	public @ResponseBody
	void postData(@RequestParam("encryptedData") String encryptedData,
			@RequestParam("encryptedAgentId") String encryptedAgentId,
			@RequestParam("encryptedAES") MultipartFile encryptedAES)
	{
		try
		{
			byte[] encryptedAESKey = encryptedAES.getBytes();

			PrivateKey privateKey = Keys.loadPrivateKey();
			String agentId = Keys.decryptString(encryptedAgentId, privateKey, encryptedAESKey);
			String data = Keys.decryptString(encryptedData, privateKey, encryptedAESKey);

			Agent agent = dao.getAgentById(Integer.parseInt(agentId));

			if (agent == null)
				return;

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			Document document = documentBuilder.parse(new InputSource(
					new ByteArrayInputStream(data.getBytes("utf-8"))));
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("item");

			Node node = nodeList.item(0);

			Element element = (Element) node;

			NodeList nl = node.getChildNodes();

			List<String> keys = new ArrayList<String>();
			for (int i = 0; i < nl.getLength(); i++)
			{
				NodeList nodes = nl.item(i).getChildNodes();
				for (int j = 0; j < nodes.getLength(); j++)
					keys.add(nodes.item(j).getNodeName());
			}

			List<String> values = new ArrayList<String>();
			for (String key : keys)
				values.add(element.getElementsByTagName(key).item(0).getTextContent());

			Map<String, String> keyValues = new HashMap<String, String>();
			for (int i = 0; i < keys.size(); i++)
				keyValues.put(keys.get(i), values.get(i));

			dao.insertReport(keyValues, agent.getAddress());

			// String osName =
			// element.getElementsByTagName("osName").item(0).getTextContent();
			// String osVersion =
			// element.getElementsByTagName("osVersion").item(0)
			// .getTextContent();
			// String architecture =
			// element.getElementsByTagName("architecture").item(0)
			// .getTextContent();
			// int coreNumber =
			// Integer.parseInt(element.getElementsByTagName("coreNumber")
			// .item(0).getTextContent());
			// int frequency =
			// Integer.parseInt(element.getElementsByTagName("frequency").item(0)
			// .getTextContent());
			// String vendor =
			// element.getElementsByTagName("vendor").item(0).getTextContent();
			// int storageSizeGb =
			// Integer.parseInt(element.getElementsByTagName("sizeGb")
			// .item(0).getTextContent());
			// int storageSizeFreeGb = Integer.parseInt(element
			// .getElementsByTagName("SizeFreeGb").item(0).getTextContent());
			// int memorySizeMb =
			// Integer.parseInt(element.getElementsByTagName("sizeMb").item(0)
			// .getTextContent());
			// int memoryFreePercent = Integer.parseInt(element
			// .getElementsByTagName("freePercent").item(0).getTextContent());
			// int processCount =
			// Integer.parseInt(element.getElementsByTagName("processCount")
			// .item(0).getTextContent());
			//
			// Report report = new Report();
			// report.setAgent(agent);
			// report.setArchitecture(architecture);
			// report.setCpuCoreNumber(coreNumber);
			// report.setCpuFrequency(frequency);
			// report.setCpuVendor(vendor);
			// report.setMemoryFreePercent(memoryFreePercent);
			// report.setMemorySizeMb(memorySizeMb);
			// report.setOsName(osName);
			// report.setOsVersion(osVersion);
			// report.setProcessCount(processCount);
			// report.setStorageFreeGb(storageSizeFreeGb);
			// report.setStorageSizeGb(storageSizeGb);
			// report.setTimeStamp(new Date());

			// dao.saveReport(report);

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		// Logger logger = Logger.getLogger("Data Controller");
		// logger.info("Received request to post data: " + data);

		// return URLDecoder.decode(data);
	}

	@RequestMapping(value = "/registerAgent", method = RequestMethod.POST)
	public @ResponseBody
	void registerAgent(HttpServletResponse response,
			@RequestParam("macAddress") String macAddress,
			@RequestParam("publicKey") MultipartFile publicKey,
			@RequestParam("agentName") String agentName,
			@RequestParam("attributes") String attributes,
			@RequestParam("contacts") String contacts)
	{
		try
		{
			logger.info("Received request to register agent with address:" + macAddress);

			Contacts co = (Contacts) unMarshaller.unmarshal(new StreamSource(IOUtils
					.toInputStream(contacts)));

			Agent agent = dao.getAgentByAddress(macAddress);
			byte[] agentPublicKey = publicKey.getBytes();

			SecretKey aesKey = Keys.generateSymmetricKeyForAgent();

			if (agent == null)
			{
				boolean isCreated = dao.createReportTable(getAttributes(attributes),
						macAddress);
				if (!isCreated)
					return;

				agent = new Agent();
				agent.setAddress(macAddress);
				agent.setPublicKey(agentPublicKey);
				agent.setName(agentName);

				Set<Contact> agentContacts = new HashSet<Contact>();

				for (thesis.vb.szt.server.entity.Contact item : co.getContacts())
				{
					Contact contact = dao.getContactByUsername(item.getUsername());
					if (contact != null)
					{
						agentContacts.add(contact);

						notifier.sendInfoMail(contact.getEmail(), contact.getName(), subject, body);
					}
					else {
						response.sendError(HttpStatus.NOT_FOUND.value(), "Contact with username: " + item.getUsername() + " not found");
					}
				}

				agent.setContats(agentContacts);
				agent.setId(dao.saveAgent(agent));
			}

			KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(agentPublicKey);
			PublicKey agentPubKey = rsaKeyFac.generatePublic(keySpec);

			String value = Keys.encryptString("" + agent.getId(), aesKey);
			byte[] encryptedAES = Keys.encryptAES(agentPubKey, aesKey);

			CommunicationData communicationData = new CommunicationData(value, encryptedAES);
			OutputStream responseStream = response.getOutputStream();
			javax.xml.transform.Result xmlResult = new StreamResult(responseStream);
			marshaller.marshal(communicationData, xmlResult);
			responseStream.close();
			response.setStatus(HttpStatus.OK.value());
		} catch (Exception e)
		{
			logger.error("Unable to register agent", e);
			try
			{
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
			} catch (IOException e1)
			{
				logger.error("Unable to send error response", e);
			}
		}
	}

	@RequestMapping(value = "/getPublicKey", method = RequestMethod.GET)
	public @ResponseBody
	String getPublicKey() throws Exception
	{
		Logger logger = Logger.getLogger("Data Controller");
		logger.info("Received request for public key");

		byte[] keyBytes = Keys.getKeyPair().getPublic().getEncoded();

		System.out.println(new String(keyBytes));

		String result = Base64.encodeBase64String(keyBytes);

		return result;
	}

	private List<String> getAttributes(String attributes)
	{
		List<String> result = new ArrayList<String>();

		String[] items = attributes.split(",");

		for (String attribute : items)
		{
			result.add(attribute);
		}

		return result;
	}

	public void setMarshaller(Marshaller marshaller)
	{
		this.marshaller = marshaller;
	}

	public void setUnMarshaller(Unmarshaller unMarshaller)
	{
		this.unMarshaller = unMarshaller;
	}

	public Notifier getNotifier()
	{
		return notifier;
	}

	public void setNotifier(Notifier notifier)
	{
		this.notifier = notifier;
	}

}
package thesis.vb.szt.server.controller;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Controller;
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
import thesis.vb.szt.server.security.Keys;
import thesis.vb.szt.server.util.CommunicationData;
import thesis.vb.szt.server.util.Contacts;
import thesis.vb.szt.server.util.Mail;

@Controller
public class DataController
{
	@Autowired
	private Dao dao;

	@Autowired
	private Marshaller marshaller;
	@Autowired
	private Unmarshaller unMarshaller;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private Mail mail;

	protected static Logger logger = Logger.getLogger("DataController");

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String getHomePage()
	{
		String mac = "alma";
//		List<String> attributes = new ArrayList<String>();
//		attributes.add(new String("attribute1"));
//		attributes.add(new String("attribute2"));
//		dao.createReportTable(attributes, mac);

//		Map<String, String> report = new HashMap<String, String>();
//		report.put("attribute1", "attributeValue1");
//		report.put("attribute2", "attributeValue2");
//		dao.insertReport(report, mac);
//		dao.insertReport(report, mac);
//		dao.listReports(-2, mac);
		dao.getAllReports(10);

		return "index";
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public @ResponseBody
	String list()
	{
		List<List<Map<String, String>>> reportList = dao.getAllReports(10);
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
			Logger logger = Logger.getLogger("Data Controller");
			logger.info("Received request to register agent with address:" + macAddress);

			// TODO ezt a fájlmarhaságot meg kell majd szüntetni
			File file = new File("test.txt");
			if (!file.exists())
			{
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(contacts);
			bw.close();
			Contacts cc = (Contacts) unMarshaller.unmarshal(new StreamSource(
					new FileInputStream(file)));
			file.delete();

			Agent agent = dao.getAgentByAddress(macAddress);
			byte[] agentPublicKey = publicKey.getBytes();

			SecretKey aesKey = Keys.generateSymmetricKey();

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

		} catch (Exception e)
		{
			e.printStackTrace();
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

	// @RequestMapping(value = "/index", method = RequestMethod.GET)
	// public String list(Model model)
	// {
	// List<Report> reportList = dao.getAllReports();
	// model.addAttribute("reportList", reportList);
	//
	// return "index";
	// }

}
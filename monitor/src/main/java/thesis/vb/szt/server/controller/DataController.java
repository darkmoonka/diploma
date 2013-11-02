package thesis.vb.szt.server.controller;

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import thesis.vb.szt.server.entity.Report;
import thesis.vb.szt.server.security.Keys;
import thesis.vb.szt.server.util.CommunicationData;

@Controller
public class DataController
{
	@Autowired
	private Dao dao;

	@Autowired
	private Marshaller marshaller;

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String getHomePage()
	{
		return "index";
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
			
			if(agent == null)
				return;

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new ByteArrayInputStream(data
					.getBytes("utf-8"))));
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("item");

			Node nNode = nList.item(0);

			Element eElement = (Element) nNode;

			String osName = eElement.getElementsByTagName("osName").item(0).getTextContent();
			String osVersion = eElement.getElementsByTagName("osVersion").item(0)
					.getTextContent();
			String architecture = eElement.getElementsByTagName("architecture").item(0)
					.getTextContent();
			int coreNumber = Integer.parseInt(eElement.getElementsByTagName("coreNumber")
					.item(0).getTextContent());
			int frequency = Integer.parseInt(eElement.getElementsByTagName("frequency")
					.item(0).getTextContent());
			String vendor = eElement.getElementsByTagName("vendor").item(0).getTextContent();
			int storageSizeGb = Integer.parseInt(eElement.getElementsByTagName("sizeGb").item(0)
					.getTextContent());
			int storageSizeFreeGb = Integer.parseInt(eElement.getElementsByTagName("SizeFreeGb")
					.item(0).getTextContent());
			int memorySizeMb = Integer.parseInt(eElement.getElementsByTagName("sizeMb").item(0)
					.getTextContent());
			int memoryFreePercent = Integer.parseInt(eElement.getElementsByTagName("freePercent")
					.item(0).getTextContent());
			int processCount = Integer.parseInt(eElement.getElementsByTagName("processCount")
					.item(0).getTextContent());
			
			
			Report report = new Report();
			report.setAgent(agent);
			report.setArchitecture(architecture);
			report.setCpuCoreNumber(coreNumber);
			report.setCpuFrequency(frequency);
			report.setCpuVendor(vendor);
			report.setMemoryFreePercent(memoryFreePercent);
			report.setMemorySizeMb(memorySizeMb);
			report.setOsName(osName);
			report.setOsVersion(osVersion);
			report.setProcessCount(processCount);
			report.setStorageFreeGb(storageSizeFreeGb);
			report.setStorageSizeGb(storageSizeGb);
			report.setTimeStamp(new Date());
			
			dao.saveReport(report);

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
			@RequestParam("publicKey") MultipartFile publicKey)
	{
		try
		{
			Logger logger = Logger.getLogger("Data Controller");
			logger.info("Received request to register agent with address:" + macAddress);

			Agent agent = dao.getAgentByAddress(macAddress);
			byte[] agentPublicKey = publicKey.getBytes();

			SecretKey aesKey = Keys.generateSymmetricKey();

			if (agent == null)
			{
				agent = new Agent();
				agent.setAddress(macAddress);
				agent.setPublicKey(agentPublicKey);
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

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String list(Model model)
	{

		List<Report> reportList = dao.getAllReports();
		model.addAttribute("reportList", reportList);

		return "index";
	}

}
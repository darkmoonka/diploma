package thesis.vb.szt.server.controller;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import thesis.vb.szt.server.dao.Dao;
import thesis.vb.szt.server.entity.Agent;
import thesis.vb.szt.server.security.Keys;
import thesis.vb.szt.server.util.Result;

@Controller
public class DataController
{
	@Autowired
	private Dao dao;

	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String getHomePage()
	{
		return "index";
	}

	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/postData", method = RequestMethod.POST)
	public @ResponseBody
	String postData(@RequestBody String data)
	{
		Logger logger = Logger.getLogger("Data Controller");
		logger.info("	Received request to post data: " + data);

		// TODO lecserélni vmi nem depricatedre :)
		return URLDecoder.decode(data);
	}

	@RequestMapping(value = "/registerAgent", method = RequestMethod.POST)
	public @ResponseBody
	Result registerAgent(@RequestParam("macAddress") String macAddress,
			@RequestParam("publicKey") MultipartFile publicKey)
	{
		try
		{
			// Agent agent = dao.getAgentByAddress(macAddress);
			byte[] agentPublicKey = publicKey.getBytes();

			XMLConfiguration configer = new XMLConfiguration("config.xml");

			String keyPath = (String) configer.getProperty("Key.Path");
			int keyLength = Integer.valueOf((String) configer.getProperty("Key.Length"));

			KeyPair keyPair = Keys.getKeyPair(keyPath, keyLength);
			SecretKey aesKey = Keys.generateSymmetricKey();

			String value = Keys.encryptString("33", aesKey);
			byte[] encryptedAES = Keys.encryptAES(keyPair.getPublic(), aesKey);

			// if (agent != null) // már létezik a DBben
			// return new Result(value, encryptedAES, keyPair.getPublic());
			//
			// agent = new Agent();
			// agent.setAddress(macAddress);
			// agent.setPublicKey(agentPublicKey);

			// dao.saveAgent(agent);

			// return new Result(value, encryptedAES, keyPair.getPublic());

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;

	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public @ResponseBody String register() throws Exception
	{
		KeyPair keyPair = Keys.getKeyPair("Keys", 2048);
		SecretKey aesKey = Keys.generateSymmetricKey();

		String value = Keys.encryptString("33", aesKey);
		byte[] encryptedAES = Keys.encryptAES(keyPair.getPublic(), aesKey);

		// return new Result("10", encryptedAES, keyPair.getPublic());
		return new Result("10").toString();
	}
}
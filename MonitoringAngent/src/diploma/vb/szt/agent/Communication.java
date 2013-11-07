package diploma.vb.szt.agent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class Communication
{
	static void sendData(String url, String encryptedData,
			String encryptedAgentId, byte[] encryptedAES) throws Exception
	{
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		reqEntity.addPart("encryptedAgentId", new StringBody(encryptedAgentId));
		reqEntity.addPart("encryptedData", new StringBody(encryptedData));
		reqEntity.addPart("encryptedAES", new ByteArrayBody(encryptedAES, ""));

		postRequest.setEntity(reqEntity);
		httpClient.execute(postRequest);
	}

	static String getMacAddress() throws UnknownHostException, SocketException
	{
		InetAddress ip = InetAddress.getLocalHost();

		NetworkInterface network = NetworkInterface.getByInetAddress(ip);
		byte[] mac = network.getHardwareAddress();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < mac.length; i++)
		{
			sb.append(String.format("%02X%s", mac[i],
					(i < mac.length - 1) ? "-" : ""));
		}
		return sb.toString();
	}

	static int register(String url, byte[] publicKey, String agentName,
			String contactName, String contactEmail) throws Exception
	{
		String macAddress = getMacAddress();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart("macAddress", new StringBody(macAddress));
		reqEntity.addPart("agentName", new StringBody(agentName));
		reqEntity.addPart("contactName", new StringBody(contactName));
		reqEntity.addPart("contactEmail", new StringBody(contactEmail));
		reqEntity.addPart("publicKey", new ByteArrayBody(publicKey, ""));
		postRequest.setEntity(reqEntity);
		HttpResponse response = httpClient.execute(postRequest);

		JAXBContext jaxbContext = JAXBContext
				.newInstance(CommunicationData.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		CommunicationData communicationData = (CommunicationData) jaxbUnmarshaller
				.unmarshal(new InputStreamReader(response.getEntity()
						.getContent(), "UTF-8"));

		String agentId = Keys.decryptString(communicationData.getValue(),
				Keys.loadPrivateKey(), communicationData.getEncryptedAesKey());

		return Integer.valueOf(agentId);
	}

	static PublicKey getServerPublicKey(String url) throws Exception
	{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		int responseCode = con.getResponseCode();
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}
		in.close();

		String result = response.toString();
		byte[] key = Base64.decodeBase64(result);

		KeyFactory rsaKeyFac = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(key);
		PublicKey serverPublicKey = rsaKeyFac.generatePublic(keySpec);

		return serverPublicKey;
	}
}

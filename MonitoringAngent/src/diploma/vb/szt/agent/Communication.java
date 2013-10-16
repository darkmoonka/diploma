package diploma.vb.szt.agent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class Communication
{
	static void sendData(String url, String data) throws Exception
	{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add reuqest header
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

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null)
		{
			response.append(inputLine);
		}
		in.close();

		System.out.println(response.toString());
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

	static void register(String url, String macAddress, byte[] publicKey)
			throws Exception
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("macAddress", macAddress));
		params.add(new BasicNameValuePair("publicKey", new String(publicKey)));
		
		
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		// Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();

		if (entity != null)
		{
			InputStream instream = entity.getContent();
			try
			{
				// do something useful
			} finally
			{
				instream.close();
			}
		}

		// -------------------------------------------------------------------------
		// HttpClient httpClient = new DefaultHttpClient();
		// HttpPost postRequest = new HttpPost(url);
		// String result = null;
		// try
		// {
		//
		// MultipartEntity reqEntity = new MultipartEntity(
		// HttpMultipartMode.BROWSER_COMPATIBLE);
		// // reqEntity.addPart("macAddress", new StringBody(macAddress));
		// // reqEntity.addPart("publicKey", (ContentBody) new
		// ByteArrayEntity(publicKey));
		// postRequest.setEntity(reqEntity);
		// HttpResponse response = httpClient.execute(postRequest);
		//
		// BufferedReader reader = new BufferedReader(new InputStreamReader(
		// response.getEntity().getContent(), "UTF-8"));
		// String sResponse;
		// StringBuilder s = new StringBuilder();
		//
		// while ((sResponse = reader.readLine()) != null)
		// {
		// s = s.append(sResponse);
		// }
		//
		// result = s.toString();
		// } catch (UnsupportedEncodingException e)
		// {
		// e.printStackTrace();
		// } catch (ClientProtocolException e)
		// {
		// e.printStackTrace();
		// } catch (IOException e)
		// {
		// e.printStackTrace();
		// }

		// -------------------------------------------------------------------------

		// URL obj = new URL(url);
		// HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		//
		// // add reuqest header
		// con.setRequestMethod("POST");
		//
		// // Send post request
		// con.setDoOutput(true);
		// DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		// wr.writeChars(macAddress);
		// // wr.write(publicKey);
		// wr.flush();
		// wr.close();
		//
		// int responseCode = con.getResponseCode();
		//
		// System.out.println("Response Code : " + responseCode);
		//
		// BufferedReader in = new BufferedReader(new InputStreamReader(
		// con.getInputStream()));
		// String inputLine;
		// StringBuffer response = new StringBuffer();
		//
		// while ((inputLine = in.readLine()) != null)
		// {
		// response.append(inputLine);
		// }
		// in.close();
		//
		// System.out.println(response.toString());
	}
}

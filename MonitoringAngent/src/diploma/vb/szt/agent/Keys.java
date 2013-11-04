package diploma.vb.szt.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class Keys
{
	public static KeyPair getKeyPair() throws Exception
	{
		KeyPair keyPair = null;

		XMLConfiguration configer = new XMLConfiguration("config.xml");

		String keyPath = configer.getString("Key.Path");
		int keyLength = configer.getInt("Key.Length");

		File publicKeyPath = new File(keyPath + File.separator + "public.key");
		File privateKeyPath = new File(keyPath + File.separator + "private.key");

		if (!publicKeyPath.exists() || !privateKeyPath.exists())
		{
			File file = new File(keyPath);
			if (!file.exists())
				file.mkdir();

			publicKeyPath.deleteOnExit();
			privateKeyPath.deleteOnExit();

			keyPair = generateKeyPair(keyLength);
			saveKeyPair(keyPath, keyPair);
		} else
			keyPair = loadKeyPair();

		return keyPair;
	}

	private static KeyPair generateKeyPair(int keyLength)
			throws NoSuchAlgorithmException
	{
		KeyPair keyPair;
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keyLength);
		keyPair = keyPairGenerator.genKeyPair();
		return keyPair;
	}

	private static void saveKeyPair(String path, KeyPair keyPair)
			throws IOException
	{
		PrivateKey privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();

		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());
		File file = new File(path);
		if (!file.exists())
			file.mkdir();

		FileOutputStream fos;

		file = new File(path + File.separator + "public.key");
		if (!file.exists())
		{
			file.createNewFile();
			fos = new FileOutputStream(file);
			fos.write(x509EncodedKeySpec.getEncoded());
			fos.close();
		}

		// Store Private Key.
		file = new File(path + File.separator + "private.key");
		if (!file.exists())
		{
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
					privateKey.getEncoded());
			fos = new FileOutputStream(file);
			fos.write(pkcs8EncodedKeySpec.getEncoded());
			fos.close();
		}
	}

	public static KeyPair loadKeyPair() throws Exception
	{
		PublicKey publicKey = loadPublicKey();
		PrivateKey privateKey = loadPrivateKey();

		return new KeyPair(publicKey, privateKey);
	}

	public static PublicKey loadPublicKey() throws Exception
	{
		XMLConfiguration configer = new XMLConfiguration("config.xml");
		String keyPath = configer.getString("Key.Path");

		// Read Public Key.
		File filePublicKey = new File(keyPath + File.separator + "public.key");
		FileInputStream fis = new FileInputStream(filePublicKey);
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		return keyFactory.generatePublic(publicKeySpec);
	}

	public static PrivateKey loadPrivateKey() throws Exception
	{
		XMLConfiguration configer = new XMLConfiguration("config.xml");
		String keyPath = configer.getString("Key.Path");

		// Read Private Key.
		File filePrivateKey = new File(keyPath + File.separator + "private.key");
		FileInputStream fis = new FileInputStream(filePrivateKey);
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(privateKeySpec);
	}

	public static byte[] encryptAES(PublicKey key, SecretKey AES)
			throws Exception
	{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedAES = cipher.doFinal(AES.getEncoded());

		return encryptedAES;
	}

	public static String encryptString(String plainText, SecretKey key)
			throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec secretKey = new SecretKeySpec(key.getEncoded(), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		String encryptedString = Base64.encodeBase64String(cipher
				.doFinal(plainText.getBytes()));

		return encryptedString;
	}

	public static String decryptString(String encryptedText, PrivateKey key,
			byte[] encryptedAES) throws Exception
	{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] plainAES = cipher.doFinal(encryptedAES);

		cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		SecretKeySpec secretKey = new SecretKeySpec(plainAES, "AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		String decryptedString = new String(cipher.doFinal(Base64
				.decodeBase64(encryptedText)));

		return decryptedString;
	}

	public static SecretKey generateSymmetricKey()
			throws NoSuchAlgorithmException
	{
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		return keyGenerator.generateKey();
	}

	public static void saveServerPublicKey(PublicKey publicKey)
			throws IOException, ConfigurationException
	{
		XMLConfiguration configer = new XMLConfiguration("config.xml");
		String path = configer.getString("Key.Path");

		// Store Public Key.
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				publicKey.getEncoded());

		FileOutputStream fos;

		File file = new File(path + File.separator + "serverPublic.key");
		if (!file.exists())
		{
			file.createNewFile();
			fos = new FileOutputStream(file);
			fos.write(x509EncodedKeySpec.getEncoded());
			fos.close();
		}
	}

	public static PublicKey loadServerPublicKey() throws Exception
	{
		XMLConfiguration configer = new XMLConfiguration("config.xml");
		String keyPath = configer.getString("Key.Path");

		// Read Public Key.
		File filePublicKey = new File(keyPath + File.separator
				+ "serverPublic.key");

		if (!filePublicKey.exists())
		{
			String protocol = configer.getString("Server.Protocol");
			String address = configer.getString("Server.Address");
			String port = configer.getString("Server.Port");
			String url = protocol + "://" + address + ":" + port;
			url = url + "/monitor/getPublicKey";

			PublicKey serverPublicKey = Communication.getServerPublicKey(url);
			saveServerPublicKey(serverPublicKey);
			return serverPublicKey;
		}

		FileInputStream fis = new FileInputStream(filePublicKey);
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		return keyFactory.generatePublic(publicKeySpec);
	}
}
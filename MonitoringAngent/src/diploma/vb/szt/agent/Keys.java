package diploma.vb.szt.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Keys
{

	public static void generateKeyPair(String keyPath, int keyLength)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			IOException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchProviderException,
			NoSuchPaddingException
	{
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(keyLength);
		KeyPair keyPair = keyPairGenerator.genKeyPair();

		saveKeyPair(keyPath, keyPair);
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

		file = new File(path + "/public.key");
		if (!file.exists())
		{
			fos = new FileOutputStream(path + "/public.key");
			fos.write(x509EncodedKeySpec.getEncoded());
			fos.close();
		}

		// Store Private Key.
		file = new File(path + "/private.key");
		if (!file.exists())
		{
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
					privateKey.getEncoded());
			fos = new FileOutputStream(path + "/private.key");
			fos.write(pkcs8EncodedKeySpec.getEncoded());
			fos.close();
		}
	}

	public static KeyPair loadKeyPair(String path) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException
	{
		PublicKey publicKey = loadPublicKey(path);
		PrivateKey privateKey = loadPrivateKey(path);

		return new KeyPair(publicKey, privateKey);
	}

	public static PublicKey loadPublicKey(String path) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException
	{
		// Read Public Key.
		File filePublicKey = new File(path + "/public.key");
		FileInputStream fis = new FileInputStream(path + "/public.key");
		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
		fis.read(encodedPublicKey);
		fis.close();

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		return keyFactory.generatePublic(publicKeySpec);
	}

	public static PrivateKey loadPrivateKey(String path) throws IOException,
			NoSuchAlgorithmException, InvalidKeySpecException
	{
		// Read Private Key.
		File filePrivateKey = new File(path + "/private.key");
		FileInputStream fis = new FileInputStream(path + "/private.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);

		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(privateKeySpec);
	}

	public static String encodeString(String plainText, PublicKey key)
			throws IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException
	{
		Cipher cipher = Cipher.getInstance("RSA");

		cipher.init(Cipher.ENCRYPT_MODE, key);

		byte[] cipherText = cipher.doFinal(plainText.getBytes());
		return new String(cipherText);
	}

	public static String decodeString(String encodedText, PrivateKey key)
			throws IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException
	{
		Cipher cipher = Cipher.getInstance("RSA");

		cipher.init(Cipher.DECRYPT_MODE, key);

		byte[] plainText = cipher.doFinal(encodedText.getBytes());
		return new String(plainText);
	}
}
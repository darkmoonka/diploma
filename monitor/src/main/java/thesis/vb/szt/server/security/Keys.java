package thesis.vb.szt.server.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Keys
{
	public static KeyPair getKeyPair(String keyPath, int keyLength)
			throws NoSuchAlgorithmException, InvalidKeySpecException,
			IOException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchProviderException,
			NoSuchPaddingException
	{
		KeyPair keyPair = null;

		File publicKeyPath = new File(keyPath + "/public.key");
		File privateKeyPath = new File(keyPath + "/private.key");

		// SecretKey symmetricKey = generateSymmetricKey();
		//
		// Cipher cipher = Cipher.getInstance("AES");
		// cipher.init(Cipher.ENCRYPT_MODE, symmetricKey);
		// byte[] cipherText = cipher.doFinal("alma".getBytes());
		// String enc = new String(cipherText);
		// System.out.println(enc);
		//
		// cipher = Cipher.getInstance("AES");
		// cipher.init(Cipher.DECRYPT_MODE, symmetricKey);
		// byte[] plainText = cipher.doFinal(enc.getBytes());
		// System.out.println(new String(plainText));

		if (!publicKeyPath.exists() || !privateKeyPath.exists())
		{
			File file = new File(keyPath);
			if (!file.exists())
				file.mkdir();

			publicKeyPath.deleteOnExit();
			privateKeyPath.deleteOnExit();

			keyPair = generateKeyPair(keyLength);
			saveKeyPair(keyPath, keyPair);

			// TODO send keys to server
		} else
			keyPair = loadKeyPair(keyPath);

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

	public static byte[] encryptAES(PublicKey key, SecretKey AES)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, InvalidKeyException
	{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedAES = cipher.doFinal(AES.getEncoded());

		return encryptedAES;
	}

	public static String encryptString(String plainText, SecretKey key)
			throws IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException,
			InvalidParameterSpecException, InvalidAlgorithmParameterException
	{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec secretKey = new SecretKeySpec(key.getEncoded(), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		String encryptedString = Base64.encodeBase64String(cipher
				.doFinal(plainText.getBytes()));

		return encryptedString;
	}

	public static String decryptString(String encryptedText, PrivateKey key,
			byte[] encryptedAES) throws IllegalBlockSizeException,
			BadPaddingException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchProviderException, NoSuchPaddingException,
			InvalidAlgorithmParameterException
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
}
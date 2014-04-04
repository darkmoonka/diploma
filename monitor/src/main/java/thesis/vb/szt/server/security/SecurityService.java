package thesis.vb.szt.server.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesis.vb.szt.server.dao.Dao;

@Service
@Transactional(readOnly = true)
public class SecurityService
{
	private static final byte[] iv = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
	protected static Logger logger = Logger.getLogger("Security Service");

	@Autowired
	Dao dao;

	public String encrypQuery(String query, SecretKey secretKey) throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
		byte[] result = cipher.doFinal(query.getBytes("UTF-8"));
		String encrypted = Base64.encodeBase64String(result);

		return encrypted;
	}

	public String decryptQuery(SecretKey secretKey, String encryptedQuery) throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
		byte[] decodes = Base64.decodeBase64(encryptedQuery);
		return new String(cipher.doFinal(decodes), "UTF-8");
	}
}

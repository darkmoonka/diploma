package thesis.vb.szt.server.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import thesis.vb.szt.server.dao.Dao;

@Service
@Transactional(readOnly = true)
public class SecurityService {

	protected static Logger logger = Logger.getLogger("Security Service");
	
	@Autowired
	Dao dao; 
	
	public String encrypQuery(String query, SecretKey secret) throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES"); // /CBC/PKCS5Padding
		cipher.init(Cipher.ENCRYPT_MODE, secret);

		String ciphertext = Base64.encodeBase64String(cipher.doFinal(query
				.getBytes("UTF-8")));

		return ciphertext;
	}
	
	public String decryptQuery(SecretKey secret, String encryptedPassword) throws Exception
	{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
		cipher.init(Cipher.DECRYPT_MODE, secret);
		String decryptedString = new String(cipher.doFinal(Base64.decodeBase64(encryptedPassword)));
		
		return decryptedString;
	}
}

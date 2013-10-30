package thesis.vb.szt.server.util;


//@XmlRootElement(name = "result")
public class Result
{
	private String value;
//	private byte[] encryptedAesKey;
//	private PublicKey publicKey;
	
//	public Result(String value, byte[] encryptedAesKey, PublicKey publicKey)
//	{
//		super();
//		this.value = value;
//		this.encryptedAesKey = encryptedAesKey;
//		this.publicKey = publicKey;
//	}
	
	public Result(String value)
	{
		super();
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

//	@XmlElement
	public void setValue(String value)
	{
		this.value = value;
	}

//	public byte[] getEncryptedAesKey()
//	{
//		return encryptedAesKey;
//	}
//
//	@XmlElement
//	public void setEncryptedAesKey(byte[] encryptedAesKey)
//	{
//		this.encryptedAesKey = encryptedAesKey;
//	}
//
//	public PublicKey getPublicKey()
//	{
//		return publicKey;
//	}
//
//	@XmlElement
//	public void setPublicKey(PublicKey publicKey)
//	{
//		this.publicKey = publicKey;
//	}
}
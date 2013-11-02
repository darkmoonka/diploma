package thesis.vb.szt.server.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "result")
public class CommunicationData
{
	private String value;
	private byte[] encryptedAesKey;

	public CommunicationData(String value, byte[] encryptedAesKey)
	{
		super();
		this.value = value;
		this.encryptedAesKey = encryptedAesKey;
	}

	public CommunicationData(String value)
	{
		super();
		this.value = value;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public byte[] getEncryptedAesKey()
	{
		return encryptedAesKey;
	}

	public void setEncryptedAesKey(byte[] encryptedAesKey)
	{
		this.encryptedAesKey = encryptedAesKey;
	}

	public CommunicationData()
	{
	}
}
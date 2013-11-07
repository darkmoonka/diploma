package thesis.vb.szt.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Agent")
public class Agent
{
	@Id
	@Column(name = "id")
	@GeneratedValue
	private int id;

	@Column(name = "address", unique = true)
	private String address;

	@Column(name = "publicKey", length = 2048)
	private byte[] publicKey;

	@Column(name = "name")
	private String name;

	@Column(name = "contactName")
	private String contactName;

	@Column(name = "contactEmail")
	private String contactEmail;

	public byte[] getPublicKey()
	{
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey)
	{
		this.publicKey = publicKey;
	}

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getContactName()
	{
		return contactName;
	}

	public void setContactName(String contactName)
	{
		this.contactName = contactName;
	}

	public String getContactEmail()
	{
		return contactEmail;
	}

	public void setContactEmail(String contactEmail)
	{
		this.contactEmail = contactEmail;
	}
}
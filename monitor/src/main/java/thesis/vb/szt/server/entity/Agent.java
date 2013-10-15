package thesis.vb.szt.server.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Agent")
public class Agent
{
	@Id
	@Column(name = "guid")
	UUID guid;

	@Column(name = "publicKey")
	byte[] publicKey;

	@Column(name = "address")
	String address;

	public UUID getGuid()
	{
		return guid;
	}

	public void setGuid(UUID guid)
	{
		this.guid = guid;
	}

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
}
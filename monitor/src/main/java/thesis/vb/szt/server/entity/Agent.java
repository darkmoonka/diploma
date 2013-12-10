package thesis.vb.szt.server.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "agent")
public class Agent
{
	@Id
	@Column(name = "agent_id")
	@GeneratedValue
	private int id;

	@Column(name = "address", unique = true)
	private String address;

	@Column(name = "publicKey", length = 2048)
	private byte[] publicKey;

	@Column(name = "name", unique = true)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "agent_contact", joinColumns = { @JoinColumn(name = "agent_id", nullable = false) }, inverseJoinColumns = { @JoinColumn(name = "contact_id", nullable = false) })
	public Set<Contact> contacts;

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

	public Set<Contact> getContats()
	{
		return contacts;
	}

	public void setContats(Set<Contact> contacts)
	{
		this.contacts = contacts;
	}
}
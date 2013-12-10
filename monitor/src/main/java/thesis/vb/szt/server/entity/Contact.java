package thesis.vb.szt.server.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "contact")
public class Contact
{
	@Id
	@Column(name = "contact_id")
	@GeneratedValue
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "email", unique = true)
	private String email;

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "contacts")
	private Set<Agent> agents;

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

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public Set<Agent> getAgents()
	{
		return agents;
	}

	public void setAgents(Set<Agent> agents)
	{
		this.agents = agents;
	}
}
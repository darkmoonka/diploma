package thesis.vb.szt.server.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

@Entity
@Table(name = "contact")
@XmlRootElement(name = "contact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact
{
	protected static Logger logger = Logger.getLogger("Contact");
	
	@Id
	@Column(name = "contact_id")
	@GeneratedValue
	private int id;

	@Column(name = "name")
	private String name;

	@Column(name = "email", unique = true)
	private String email;

	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "password")
	@XmlTransient
	private String password;

	@Column(name="role", columnDefinition="Decimal(1,0) default '0'")
	private int role;
	
	@Column(name = "phoneNumber")
	private String phoneNumber;
	
	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "contacts")
	@XmlTransient
	private Set<Agent> agents;
	
	@Transient
	private AgentSet agentSet;
	
	public Contact()
	{
		super();
	}

	public Contact(String name, String email)
	{
		super();
		this.name = name;
		this.email = email;
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
		agentSet = new AgentSet();
		agentSet.setAgentSet(agents);
	}
	
	public AgentSet getAgentSet()
	{
		return agentSet;
	}

	public void setAgentSet(AgentSet agentSet)
	{
		this.agentSet = agentSet;
	}
	
	public void fillAgentSet() {
		if(agents != null) {
			agentSet = new AgentSet();
			agentSet.setAgentSet(agents);
		} else {
			logger.warn("Unable to fill agent set. Agents is null.");
		}
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public int getRole()
	{
		return role;
	}

	public void setRole(int role)
	{
		this.role = role;
	}

	public String getPhoneNumber()
	{
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber)
	{
		this.phoneNumber = phoneNumber;
	}
}
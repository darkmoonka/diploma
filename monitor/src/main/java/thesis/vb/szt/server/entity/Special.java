package thesis.vb.szt.server.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "special")
public class Special
{
	@Id
	@Column(name = "id")
	@GeneratedValue
	private int id;
	
	@ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Agent agent;
	
	@Column(name = "attributeName")
	String attributeName;
	
	@Column(name = "attributeValue")
	String attributeValue;

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Agent getAgent()
	{
		return agent;
	}

	public void setAgent(Agent agent)
	{
		this.agent = agent;
	}

	public String getAttributeName()
	{
		return attributeName;
	}

	public void setAttributeName(String attributeName)
	{
		this.attributeName = attributeName;
	}

	public String getAttributeValue()
	{
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue)
	{
		this.attributeValue = attributeValue;
	}
}
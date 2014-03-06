package thesis.vb.szt.server.entity;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="agentSet")
public class AgentSet
{
	@XmlElement(name = "agent", type = Agent.class)
	private Set<Agent> agentSet;

	public AgentSet()
	{
		super();
	}

	public AgentSet(Set<Agent> agentSet)
	{
		super();
		this.agentSet = agentSet;
	}

	public Set<Agent> getAgentSet()
	{
		return agentSet;
	}

	public void setAgentSet(Set<Agent> agentSet)
	{
		this.agentSet = agentSet;
	}
	
	
}
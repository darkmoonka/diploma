package thesis.vb.szt.server.entity;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Report")
public class Report
{
	@Column(name = "id")
	@Id
	@GeneratedValue
	private int id;
	
    @ManyToOne(fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    private Agent agent;
	
	@Column(name = "memorySizeMb")
	private int memorySizeMb;
	
	@Column(name = "memoryFreePercent")
	private int memoryFreePercent;
	
	@Column(name = "cpuCoreNumber")
	private int cpuCoreNumber;
	
	@Column(name = "cpuFrequency")
	private int cpuFrequency;
	
	@Column(name = "cpuVendor")
	private String cpuVendor;
	
	@Column(name = "storageSizeGb")
	private int storageSizeGb;
	
	@Column(name = "storageFreeGb")
	private int storageFreeGb;
	
	@Column(name = "architecture")
	private String architecture;
	
	@Column(name = "osName")
	private String osName;
	
	@Column(name = "osVersion")
	private String osVersion;
	
	@Column(name = "processCount")
	private int processCount;
	
	@Column(name = "timeStamp")
	private Date timeStamp;

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

	public int getMemorySizeMb()
	{
		return memorySizeMb;
	}

	public void setMemorySizeMb(int memorySizeMb)
	{
		this.memorySizeMb = memorySizeMb;
	}

	public int getMemoryFreePercent()
	{
		return memoryFreePercent;
	}

	public void setMemoryFreePercent(int memoryFreePercent)
	{
		this.memoryFreePercent = memoryFreePercent;
	}

	public int getCpuCoreNumber()
	{
		return cpuCoreNumber;
	}

	public void setCpuCoreNumber(int coreNumber)
	{
		this.cpuCoreNumber = coreNumber;
	}

	public int getCpuFrequency()
	{
		return cpuFrequency;
	}

	public void setCpuFrequency(int cpuFrequency)
	{
		this.cpuFrequency = cpuFrequency;
	}

	public String getCpuVendor()
	{
		return cpuVendor;
	}

	public void setCpuVendor(String cpuVendor)
	{
		this.cpuVendor = cpuVendor;
	}

	public int getStorageSizeGb()
	{
		return storageSizeGb;
	}

	public void setStorageSizeGb(int storageSizeGb)
	{
		this.storageSizeGb = storageSizeGb;
	}

	public int getStorageFreeGb()
	{
		return storageFreeGb;
	}

	public void setStorageFreeGb(int storageFreeGb)
	{
		this.storageFreeGb = storageFreeGb;
	}

	public String getArchitecture()
	{
		return architecture;
	}

	public void setArchitecture(String architecture)
	{
		this.architecture = architecture;
	}

	public String getOsName()
	{
		return osName;
	}

	public void setOsName(String osName)
	{
		this.osName = osName;
	}

	public String getOsVersion()
	{
		return osVersion;
	}

	public void setOsVersion(String osVersion)
	{
		this.osVersion = osVersion;
	}

	public int getProcessCount()
	{
		return processCount;
	}

	public void setProcessCount(int processCount)
	{
		this.processCount = processCount;
	}

	public Date getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp)
	{
		this.timeStamp = timeStamp;
	}	
}
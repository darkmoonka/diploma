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
	private int coreNumber;
	
	@Column(name = "cpuFrequency")
	private int cpuFrequency;
	
	@Column(name = "cpuVencor")
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
}

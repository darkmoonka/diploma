package diploma.vb.szt.agent;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SysInfo;

public class Os extends MonitoredItem
{
	private String osName;
	private String osVersion;
	private String architecture;
	
	private final String OSNAME = "osName";
	private final String OSVERSION = "osVersion";
	private final String ARCHITECTURE = "architecture";

	public Os(Sigar sigar) throws SigarException
	{
		super("Os");

		SysInfo systemInfo = new SysInfo();
		systemInfo.gather(sigar);;
		osName = systemInfo.getName();
		osVersion = systemInfo.getVersion();
		architecture = systemInfo.getArch();
	}

	@Override
	public String toXml()
	{
		return    "<" + TYPE + ">" 
				+ "<" + OSNAME + ">" + osName + "</"+ OSNAME + ">" 
				+ "<" + OSVERSION + ">" + osVersion + "</" + OSVERSION + ">" 
				+ "<" + ARCHITECTURE + ">" + architecture + "</" + ARCHITECTURE + ">"
				+ "</" + TYPE + ">";
	}
}
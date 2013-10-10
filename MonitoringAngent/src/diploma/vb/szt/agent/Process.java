package diploma.vb.szt.agent;

import org.hyperic.sigar.ProcStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Process extends MonitoredItem
{
	private Sigar sigar;
	private long processCount;
	
	private final String PROCESSCOUNT = "processCount";
	
	public Process(Sigar sigar) throws SigarException
	{
		super("PROCESS");
		this.sigar = sigar;
		ProcStat ps = sigar.getProcStat();
		processCount = ps.getTotal();
	}
	
	void kill(long pid) throws SigarException
	{
		sigar.kill(pid, 1);
	}
	
	@Override
	public String toXml()
	{
		return    "<" + TYPE + ">" 
				+ "<" + PROCESSCOUNT + ">" + processCount + "</" + PROCESSCOUNT + ">"
				+ "</" + TYPE + ">";
	}
}
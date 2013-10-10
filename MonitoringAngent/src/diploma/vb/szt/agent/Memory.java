package diploma.vb.szt.agent;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Memory extends MonitoredItem
{
	private int sizeMb;
	private float freePercent;

	private final String SIZEMB = "sizeMb";
	private final String FREEPERCENT = "freePercent";

	public Memory(Sigar sigar) throws SigarException
	{
		super("MEMORY");
		Mem mem = sigar.getMem();
		sizeMb = (int) (mem.getTotal() / 1024 / 1024);
		freePercent = (float) mem.getFreePercent();
	}

	@Override
	public String toXml()
	{
		return    "<" + TYPE + ">" 
				+ "<" + SIZEMB + ">" + sizeMb + "</"+ SIZEMB + ">" 
				+ "<" + FREEPERCENT + ">" + freePercent + "</" + FREEPERCENT + ">" 
				+ "</" + TYPE + ">";
	}
}
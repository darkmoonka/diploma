package diploma.vb.szt.agent;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Cpu extends MonitoredItem
{
	private int coreNumber;
	private int frequency;
	private String vendor;

	private final String CORENUMBER = "coreNumber";
	private final String FREQUENCY = "frequency";
	private final String VENDOR = "vendor";

	public Cpu(Sigar sigar) throws SigarException
	{
		super("CPU");
		
		CpuInfo[] cpu = sigar.getCpuInfoList();
		coreNumber = cpu.length;
		frequency = cpu[0].getMhz();
		vendor = cpu[0].getVendor();
	}

	@Override
	public String toXml()
	{
		return    "<" + TYPE + ">" 
				+ "<" + CORENUMBER + ">" + coreNumber + "</"+ CORENUMBER + ">" 
				+ "<" + FREQUENCY + ">" + frequency + "</" + FREQUENCY + ">" 
				+ "<" + VENDOR + ">" + vendor + "</" + VENDOR + ">"
				+ "</" + TYPE + ">";
	}
}
package diploma.vb.szt.agent;

import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Storage extends MonitoredItem
{
	private int sizeGb;
	private int sizeFreeGb;
	
	private final String SIZEGB = "sizeGb";
	private final String SIZEFREEGB = "SizeFreeGb";
	
	public Storage(Sigar sigar) throws SigarException
	{
		super("STORAGE");
		
		FileSystem[] fs = sigar.getFileSystemList();
		for(int i = 0; i < fs.length; i++)
			if (fs[i].getType() == FileSystem.TYPE_LOCAL_DISK)
			{
			    FileSystemUsage usage = sigar.getFileSystemUsage(fs[i].getDirName());
			    sizeGb += usage.getTotal() / 1024 / 1024;
			    sizeFreeGb += usage.getFree() / 1024 / 1024;
			}
	}

	@Override
	public String toXml()
	{
		return    "<" + TYPE + ">" 
				+ "<" + SIZEGB + ">" + sizeGb + "</"+ SIZEGB + ">" 
				+ "<" + SIZEFREEGB + ">" + sizeFreeGb + "</" + SIZEFREEGB + ">" 
				+ "</" + TYPE + ">";
	}
}
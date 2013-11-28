package diploma.vb.szt.agent;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import org.hyperic.sigar.NetInfo;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class Network extends MonitoredItem
{

	private String interfaceName;

	private String defaultGateway;
	private String primaryDns;
	private String secondaryDns;
	private String domainName;
	private String hostName;

	private String address;
	private String netmask;

	private final String INTERFACENAME = "interfaceName";
	private final String DEFAULTGATEWAY = "defaultGateway";
	private final String PRIMARYDNS = "primaryDns";
	private final String SECONDARYDNS = "secondaryDns";
	private final String DOMAINNAME = "domainName";
	private final String HOSTNAME = "hostName";
	private final String ADDRESS = "address";
	private final String NETMASK = "netmask";

	public Network(Sigar sigar) throws SocketException, SigarException
	{
		super("Network");

		interfaceName = getActiveInterface();
		NetInfo netInfo = sigar.getNetInfo();
		NetInterfaceConfig config = sigar.getNetInterfaceConfig(interfaceName);

		defaultGateway = netInfo.getDefaultGateway();
		primaryDns = netInfo.getPrimaryDns();
		secondaryDns = netInfo.getSecondaryDns();
		domainName = netInfo.getDomainName();
		hostName = netInfo.getHostName();
		address = config.getAddress();
		netmask = config.getNetmask();

	}

	String getActiveInterface() throws SocketException
	{
		Enumeration<NetworkInterface> nets = NetworkInterface
				.getNetworkInterfaces();
		for (NetworkInterface netint : Collections.list(nets))
		{
			// TODO windowson mi a localhost interface neve?
			// TODO lehet majd config paraméter kéne
			if (!netint.getName().equalsIgnoreCase("lo0"))
				return netint.getName();
		}

		return null;
	}

	@Override
	public String toXml()
	{
		return    "<" + TYPE + ">" 
				+ "<" + INTERFACENAME + ">" + interfaceName + "</"+ INTERFACENAME + ">" 
				+ "<" + DEFAULTGATEWAY + ">" + defaultGateway + "</" + DEFAULTGATEWAY + ">" 
				+ "<" + PRIMARYDNS + ">" + primaryDns + "</" + PRIMARYDNS + ">" 
				+ "<" + SECONDARYDNS + ">" + secondaryDns + "</" + SECONDARYDNS + ">" 
				+ "<" + DOMAINNAME + ">" + domainName + "</" + DOMAINNAME + ">" 
				+ "<" + HOSTNAME + ">" + hostName + "</" + HOSTNAME + ">" 
				+ "<" + ADDRESS + ">" + address + "</" + ADDRESS + ">" 
				+ "<" + NETMASK + ">" + netmask + "</" + NETMASK + ">" 
				+ "</" + TYPE + ">";
	}
}
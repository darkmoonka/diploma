package thesis.vb.szt.server.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="request")
public class ReportListRequest
{
	private String mac;
	
	//from this report
	private int from;
	
	//get this many reports 
	private int limit;

	public String getMac()
	{
		return mac;
	}

	public void setMac(String mac)
	{
		this.mac = mac;
	}

	public int getFrom()
	{
		return from;
	}

	public void setFrom(int from)
	{
		this.from = from;
	}

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int to)
	{
		this.limit = to;
	}
}
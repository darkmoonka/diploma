package thesis.vb.szt.server.entity;

import java.util.Date;

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
	
	private String fromDate;

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

	public String getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
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
package thesis.vb.szt.server.entity;

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import thesis.vb.szt.server.util.xmladapter.MapAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
public class ReportMap
{
	@XmlJavaTypeAdapter(MapAdapter.class)
	private Map<String, String> report;

	public ReportMap()
	{
		super();
	}

	public ReportMap(Map<String, String> reportMap)
	{
		super();
		this.report = reportMap;
	}

	public Map<String, String> getReportMap()
	{
		return report;
	}

	public void setReportList(Map<String, String> reportMap)
	{
		this.report = reportMap;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> reportEntry : report.entrySet()) {
			sb.append(reportEntry.getKey());
			sb.append(" ");
			sb.append(reportEntry.getValue());
			sb.append("\n");
		}
		return sb.toString();
	}
}
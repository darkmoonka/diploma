package thesis.vb.szt.server.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="reportList")
public class ReportList
{
	@XmlElement(name="reportEntity")
	private List<ReportMap> reportMapList;
	
	public ReportList()
	{
		super();
		reportMapList = new ArrayList<ReportMap>();
	}

	public ReportList(List<Map<String, String>> reportList)
	{
		super();
		reportMapList = new ArrayList<ReportMap>();
		for(Map<String, String> report : reportList) {
			this.reportMapList.add(new ReportMap(report));
		}
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for(ReportMap reportMap : reportMapList) {
			sb.append(reportMap.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}
package diploma.vb.szt.agent;

public abstract class MonitoredItem
{
	final String TYPE;
	public MonitoredItem(String type)
	{
		TYPE = type;
	}
	public abstract String toXml();
}

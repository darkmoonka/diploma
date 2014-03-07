package thesis.vb.szt.server.util.xmladapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class MapAdapter extends XmlAdapter<MapAdapter.ListOfEntries, Map<String, String>> {

	@Override
	public Map<String, String> unmarshal(ListOfEntries v) throws Exception {
		return null;
	}

	@Override
	public ListOfEntries marshal(Map<String, String> map) throws Exception {
		if(map == null)
			return null;
		else {
			ListOfEntries ListOfEntries = new ListOfEntries();
	        for(java.util.Map.Entry<String,String> mapEntry : map.entrySet()) {
	            Entry entry = new Entry();
	            entry.name = mapEntry.getKey();
	            entry.value = mapEntry.getValue();
	            ListOfEntries.property.add(entry);
	        }
	        return ListOfEntries;
		}
	}
	
	public static class ListOfEntries {
        public List<Entry> property = new ArrayList<Entry>();
    }

    public static class Entry {
    	@XmlAttribute
        public String name;
    	@XmlAttribute
        public String value;
    }

}

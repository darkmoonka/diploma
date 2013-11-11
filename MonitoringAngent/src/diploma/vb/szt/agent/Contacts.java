package diploma.vb.szt.agent;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contacts")
public class Contacts
{
	@XmlElement(name = "contact", type = Contact.class)
	private List<Contact> contacts;

	public Contacts()
	{
	}

	public Contacts(List<Contact> contacts)
	{
		super();
		this.contacts = contacts;
	}

	public List<Contact> getContacts()
	{
		return contacts;
	}

	public void setContacts(List<Contact> contacts)
	{
		this.contacts = contacts;
	}
}
package thesis.vb.szt.server.entity;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import thesis.vb.szt.server.entity.Contact;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "contacts")
public class Contacts
{
	@XmlElement(name = "contact", type = Contact.class)
	private Set<Contact> contacts;

	public Contacts()
	{
	}

	public Contacts(Set<Contact> contacts)
	{
		super();
		this.contacts = contacts;
	}

	public Set<Contact> getContacts()
	{
		return contacts;
	}

	public void setContacts(Set<Contact> contacts)
	{
		this.contacts = contacts;
	}
}
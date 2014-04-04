package thesis.vb.szt.server.util;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import thesis.vb.szt.server.entity.Contact;

public class Notifier
{
	@Autowired
	public Mail mail;

	public void error(Logger logger, String message, Throwable throwable)
	{
		Set<Contact> admins = getAdmins();

		for (Contact contact : admins)
		{
			String subject = "monitor error";
			StringBuilder sb = new StringBuilder("");
			sb.append("error message: \n").append(message + "\n\n");
			if (throwable != null)
			{
				sb.append("trace: \n");
				for (StackTraceElement item : throwable.getStackTrace())
					sb.append(item.toString()).append("\n");
			}
			sb.append("\n").append("date: ").append(new Date());

			mail.sendMail(contact.getEmail(), contact.getName(), subject, sb.toString());
		}

		logger.error(message, throwable);
	}

	public void sendInfoMail(String to, String name, String subject, String body)
	{
		mail.sendMail(to, name, subject, body);
	}

	private static Set<Contact> getAdmins()
	{
		XMLConfiguration configer;
		try
		{
			configer = new XMLConfiguration("config.xml");

			List<Object> contactNames = configer.getList("Admins.Admin.Name");
			List<Object> contactEmails = configer.getList("Admins.Admin.Email");

			Set<Contact> admins = new HashSet<Contact>();
			if (contactNames != null && !contactNames.isEmpty() && contactEmails != null
					&& !contactEmails.isEmpty())
			{
				for (int i = 0; i < contactNames.size(); i++)
					admins.add(new Contact(contactNames.get(i).toString(), contactEmails
							.get(i).toString()));
			}

			return admins;

		} catch (ConfigurationException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public Mail getMail()
	{
		return mail;
	}
	
	@Required
	public void setMail(Mail mail)
	{
		this.mail = mail;
	}
}
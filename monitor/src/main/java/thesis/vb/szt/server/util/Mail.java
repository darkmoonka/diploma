package thesis.vb.szt.server.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class Mail
{
	@Autowired
	private MailSender mailSender;

	public void setMailSender(MailSender mailSender)
	{
		this.mailSender = mailSender;
	}

	public void sendMail(String to, String name, String subject, String body)
	{
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(to);
		message.setSubject(subject);
		if(name != null && name.isEmpty())
		{
			body = "Dear " + name + "!\n\n" + body;
		}
		message.setText(body);
		mailSender.send(message);
	}
}
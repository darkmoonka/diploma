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

	// TODO use this to send error reports
	public void sendMail(String to)
	{
		SimpleMailMessage message = new SimpleMailMessage();

		message.setTo(to);
		message.setSubject("testSubject");
		message.setText("testMsg");
		mailSender.send(message);
	}
}
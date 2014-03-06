package thesis.vb.szt.server.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import thesis.vb.szt.server.dao.Dao;

@Controller
@RequestMapping("/security")
public class SecurityController
{
	protected Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private Dao dao;

	@RequestMapping(value = "/registerContact", method = RequestMethod.GET)
	public String getRegisterContact()
	{
		return "registerContact";
	}

	@RequestMapping(value = "/registerContact", method = RequestMethod.POST)
	public String registerContact(@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("password2") String password2, @RequestParam("email") String email)
	{
		if (username == null || password == null || password2 == null
				|| !password.equals(password2))
		{
			logger.error("Invalid credentials: " + username + " " + password + " " + password2);
		}
		else if(!password.equals(password2)) 
		{
			logger.error("The given passwords does not equal: " + password + " != " + password2);
		}
		else
		{
			password = passwordEncoder.encodePassword(password, null);
			dao.updateContact(username, password, email);
			return "redirect:/";
		}
		return "registerContact";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public void login() {
		
	}
}

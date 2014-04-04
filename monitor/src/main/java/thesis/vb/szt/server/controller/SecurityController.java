package thesis.vb.szt.server.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	// TODO /security/login
	public String getLoginPage(@RequestParam(value = "error", required = false) boolean error,
			ModelMap model)
	{

		logger.debug("Received request to show login page");

		if (error)
			model.put("error", "You have entered an invalid username or password!");
		else
			model.put("error", "");

		return "loginpage";
	}

	@RequestMapping(value = "/denied", method = RequestMethod.GET)
	public String getDeniedPage()
	{

		logger.debug("Received request to show denied page");
		return "deniedpage";

	}
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String getRegisterContactPage()
	{
		
		logger.debug("Received request to show denied page");
		return "registerContact";

	}

	@RequestMapping(value = "/registerContact", method = RequestMethod.POST)
	public String registerContact(@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("password2") String password2, @RequestParam("email") String email,
			@RequestParam("name") String name, @RequestParam("phone") String phone)
	{
		if (username == null || password == null || password2 == null
				|| !password.equals(password2))
		{
			logger.error("Invalid credentials: " + username + " " + password + " " + password2);
		} else if (!password.equals(password2))
		{
			logger.error("The given passwords does not equal: " + password + " != "
					+ password2);
		} else
		{
			password = passwordEncoder.encodePassword(password, null);
			dao.createContact(name, username, password, email, phone);
			return "redirect:/";
		}
		return "registerContact";
	}

}

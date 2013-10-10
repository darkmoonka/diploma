package thesis.vb.szt.server.controller;

import java.net.URLDecoder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import thesis.vb.szt.server.dao.Dao;

@Controller
public class DataController 
{
	@Autowired
	private Dao dao;
	
	
	@RequestMapping(value = "/home", method = RequestMethod.GET)
	public String getHomePage() 
	{
		return "index";
	}
	
	
	@SuppressWarnings("deprecation")
	@RequestMapping(value = "/postData", method = RequestMethod.POST)
	public @ResponseBody String postData(@RequestBody String data) {
		Logger logger = Logger.getLogger("Data Controller");
		logger.info("	Received request to post data: " + data);

		
		// TODO lecserélni vmi nem depricatedre :)
		return URLDecoder.decode(data);
	}
}
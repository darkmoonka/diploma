package thesis.vb.szt.server.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import thesis.vb.szt.server.dao.Dao;
import thesis.vb.szt.server.entity.Agent;
import thesis.vb.szt.server.entity.AgentSet;
import thesis.vb.szt.server.entity.Contact;
import thesis.vb.szt.server.entity.Contacts;
import thesis.vb.szt.server.entity.ReportList;

@Controller
@RequestMapping("/mobile")
public class MobileController
{
	protected Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private Marshaller marshaller;
	
	@Autowired
	private Unmarshaller unmarshaller;
	
	@Autowired
	private Dao dao;
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void test (HttpServletRequest request, HttpServletResponse response) {
		
		response.setStatus(HttpStatus.OK.value());
		logger.info("Android test request");
	}
	
//	@RequestMapping(value = "/getAgents", method = RequestMethod.GET)
//	public void getAgents(HttpServletRequest request, HttpServletResponse response) {
//		logger.info("Recieved request to login from mobile client");
//		
//		OutputStream responseStream = null;
//		
//		final String username = request.getParameter("username");
//		final String password = request.getParameter("password");
//		
//		try {
//			Contact contact = dao.getContactByUsername(username);
//			if(contact == null) {
//				response.sendError(HttpStatus.NOT_FOUND.value(), "User with username \"" + username + "\" not found");
//			} else if(!contact.getPassword().equals(password)) {
//				response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid password");
//			} else {
//				AgentSet agentSet = new AgentSet(contact.getAgents());
//				
//				responseStream = response.getOutputStream();
//				marshaller.marshal(agentSet, new StreamResult(responseStream));
//				
//				response.setStatus(HttpStatus.OK.value());
//			}
//		} catch (Exception e) {
//			logger.error("Unable to get agents for " + username, e);
//			response.setStatus(HttpStatus.NOT_FOUND.value());
//		} finally {
//			if (responseStream != null) {
//				try
//				{
//					responseStream.close();
//				} catch (IOException e)
//				{
//					logger.error("Unable to close http response stream", e);
//				}
//			}
//		}
//	}
	
	@RequestMapping(value = "/getAgents", method = RequestMethod.GET)
	public void getAgents(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Recieved request to login from mobile client");
		
		OutputStream responseStream = null;
		
		try {
			Contact contact = fetchContact(request, response);
			if(contact == null) {
				return;
			}
			AgentSet agentSet = new AgentSet(contact.getAgents());
			
			responseStream = response.getOutputStream();
			marshaller.marshal(agentSet, new StreamResult(responseStream));
			
			response.setStatus(HttpStatus.OK.value());
		} catch (Exception e) {
			logger.error("Unable to get agents for " + request.getParameter("username"), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		} finally {
			if (responseStream != null) {
				try
				{
					responseStream.close();
				} catch (IOException e)
				{
					logger.error("Unable to close http response stream", e);
				}
			}
		}
	}
	
	@RequestMapping(value = "/agent/{mac}", method = RequestMethod.GET)
	public void getAgent(@PathVariable String mac, HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("Recieved request to get agent with mac \"" + mac + "\" from mobile client");
	
		OutputStream responseStream = null;
		ReportList reportList = null;
		
		
		try
		{
			if(fetchContact(request, response) == null) 
				return;
		} catch (IOException e)
		{
			logger.error("", e);
			return;
		}
		
		try {
			reportList = new ReportList(dao.getReportsForAgent(mac));
			logger.info("ReportList created:\n" + reportList.toString());
		} catch (Exception e) {
			logger.error("Unable to get reports", e);
			response.setStatus(HttpStatus.NOT_FOUND.value());
			return;
		}
		
		try
		{
			responseStream = response.getOutputStream();
			marshaller.marshal(reportList, new StreamResult(responseStream));
			
			response.setStatus(HttpStatus.OK.value());
		} catch (IOException e)
		{
			logger.error("Unable to marshal reports", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		}
	}
	
	private Contact fetchContact(HttpServletRequest request, HttpServletResponse response) throws IOException {
		final String username = request.getParameter("username");
		final String password = request.getParameter("password");
		
		Contact contact = dao.getContactByUsername(username);
		if(contact == null) {
			logger.error("User with username \"" + username + "\" not found");
			response.sendError(HttpStatus.NOT_FOUND.value(), "User with username \"" + username + "\" not found");
			return null;
		} else if(!contact.getPassword().equals(password)) {
			logger.error("Invalid password");
			response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid password");
			return null;
		} else {
			return contact;
		}
	}
}

package thesis.vb.szt.server.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import thesis.vb.szt.server.dao.Dao;
import thesis.vb.szt.server.entity.AgentSet;
import thesis.vb.szt.server.entity.Contact;
import thesis.vb.szt.server.entity.ReportList;
import thesis.vb.szt.server.security.Keys;
import thesis.vb.szt.server.security.SecurityService;

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

	@Autowired(required = true)
	private SecurityService securityService;

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public void test(HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("Android test request");
		try {
			
			ReportList reportList = null;
			StringWriter sw = new StringWriter();
			PrintWriter writer = null;
			OutputStream responseStream = null;
			String mac = "50_E5_49_4C_65_12";
			try
			{
				reportList = new ReportList(dao.getReportsForAgent(mac));
				logger.info("ReportList created:\n" + reportList.toString());
			} catch (Exception e)
			{
				logger.error("Unable to get reports", e);
				response.setStatus(HttpStatus.NOT_FOUND.value());
				return;
			}

			//encrypt reportlist
			try
			{
				String username = "asd";
				Contact contact = fetchContact(username, response);
				responseStream = response.getOutputStream();
				
				marshaller.marshal(reportList, new StreamResult(sw));
				SecretKey key = Keys.generatySymmetricKeyFromPassword(contact.getPassword());
				String encryptedResponse = securityService.encrypQuery(sw.toString(), key);
				
				logger.info("Encrypted response is: " + encryptedResponse);
				
				writer = new PrintWriter(new OutputStreamWriter(responseStream));
				writer.println(securityService.decryptQuery(key, encryptedResponse));
				writer.flush();
				response.setStatus(HttpStatus.OK.value());
				
				response.setStatus(HttpStatus.OK.value());
			} catch (IOException e)
			{
				logger.error("Unable to marshal reports", e);
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			} 
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	

//	private String decryptAspect (HttpServletRequest request, HttpServletResponse response) {
//		logger.info("Recieved request to login from mobile client");
//
//		final String username = request.getParameter("username");
//		final String encryptedQuery = request.getParameter("encryptedQuery");
//		try
//		{
//			Contact contact = fetchContact(username, response);
//			if (contact != null)
//			{
//				SecretKey key = Keys.generatySymmetricKeyFromPassword(contact.getPassword());
//				return securityService.decryptQuery(key, encryptedQuery);
//			} else {
//				
//				return null;
//			}
//		} catch (Exception e)
//		{
//			logger.error("Invalid agent username " + request.getParameter("username"), e);
//			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//			return null;
//		} 
//	}
	
	
	// @RequestMapping(value = "/getAgents", method = RequestMethod.GET)
		// public void getAgents(HttpServletRequest request, HttpServletResponse
		// response) {
		// logger.info("Recieved request to login from mobile client");
		//
		// OutputStream responseStream = null;
		//
		// try {
		// Contact contact = fetchContact(request, response);
		// if(contact == null) {
		// return;
		// }
		// AgentSet agentSet = new AgentSet(contact.getAgents());
		//
		// responseStream = response.getOutputStream();
		// marshaller.marshal(agentSet, new StreamResult(responseStream));
		//
		// response.setStatus(HttpStatus.OK.value());
		// } catch (Exception e) {
		// logger.error("Unable to get agents for " +
		// request.getParameter("username"), e);
		// response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		// } finally {
		// if (responseStream != null) {
		// try
		// {
		// responseStream.close();
		// } catch (IOException e)
		// {
		// logger.error("Unable to close http response stream", e);
		// }
		// }
		// }
		// }
	
	@RequestMapping(value = "/getAgents", method = RequestMethod.GET)
	public void getAgents(HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("Recieved request to login from mobile client");

		OutputStream responseStream = null;

		final String username = request.getParameter("username");
		final String encryptedQuery = request.getParameter("encryptedQuery");
		StringWriter sw = null;
		PrintWriter writer = null;
		try
		{
			sw = new StringWriter();
			Contact contact = fetchContact(username, response);
			if (contact != null)
			{
				SecretKey key = Keys.generatySymmetricKeyFromPassword(contact.getPassword());
				String decryptedQuery = securityService.decryptQuery(key, encryptedQuery);

				if (validateContact(contact, decryptedQuery, response))
				{
					responseStream = response.getOutputStream();
					
					AgentSet agentSet = new AgentSet(contact.getAgents());
					
					marshaller.marshal(agentSet, new StreamResult(sw)); //new StreamResult(responseStream));
					String encryptedResponse = securityService.encrypQuery(sw.toString(), key);
					
					logger.info("Encrypted response is: " + encryptedResponse);
					
					writer = new PrintWriter(new OutputStreamWriter(responseStream));
					writer.println(encryptedResponse);
					writer.flush();
					response.setStatus(HttpStatus.OK.value());
				}
			}
		} catch (Exception e)
		{
			logger.error("Unable to get agents for " + request.getParameter("username"), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		} finally
		{
			if (responseStream != null)
			{
				try
				{
					if(sw != null ) {
						sw.close();
					}
					if(writer != null) {
						writer.close();
					}
				} catch (IOException e)
				{
					logger.error("Unable to close http response stream, or stringwriter", e);
				}
			}
		}
	}
	
	@RequestMapping(value = "/agent/{mac}", method = RequestMethod.GET)
	public void getAgent(@PathVariable String mac, HttpServletRequest request,
			HttpServletResponse response)
	{
		logger.info("Recieved request to get agent with mac \"" + mac
				+ "\" from mobile client");

		final String username = request.getParameter("username");
		final String encryptedQuery = request.getParameter("encryptedQuery");
		
		StringWriter sw = null;
		PrintWriter writer = null;
		OutputStream responseStream = null;
		
		try
		{
			sw = new StringWriter();
			Contact contact = null;
			
			//get contact
			try
			{
				contact = fetchContact(username, response);
				if (contact == null)
					return;
			} catch (IOException e)
			{
				logger.error("Unable to fetch contact " + username, e);
				return;
			}
			
			SecretKey key = Keys.generatySymmetricKeyFromPassword(contact.getPassword());
			//Decrypted sting is the mac address
			String decryptedQuery = securityService.decryptQuery(key, encryptedQuery);
			
			if (validateContact(contact, decryptedQuery, response))
			{
				ReportList reportList = null;
				//get reportlist
				try
				{
					reportList = new ReportList(dao.getReportsForAgent(mac));
					logger.info("ReportList created:\n" + reportList.toString());
				} catch (Exception e)
				{
					logger.error("Unable to get reports", e);
					response.setStatus(HttpStatus.NOT_FOUND.value());
					return;
				}
	
				//encrypt reportlist
				try
				{
					responseStream = response.getOutputStream();
					
					marshaller.marshal(reportList, new StreamResult(sw));
					String encryptedResponse = securityService.encrypQuery(sw.toString(), key);
					
					logger.info("Encrypted response is: " + encryptedResponse);
					
					writer = new PrintWriter(new OutputStreamWriter(responseStream));
					writer.println(encryptedResponse);
					writer.flush();
					response.setStatus(HttpStatus.OK.value());
					
					response.setStatus(HttpStatus.OK.value());
				} catch (IOException e)
				{
					logger.error("Unable to marshal reports", e);
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				} 
			}
		} catch (Exception e) {
			logger.error("Unable to get reports for " + username, e);
		} finally {
			if (responseStream != null)
			{
				try
				{
					if(sw != null ) {
						sw.close();
					}
					if(writer != null) {
						writer.close();
					}
				} catch (IOException e)
				{
					logger.error("Unable to close http response stream, or stringwriter", e);
				}
			}
		}
	}
	/** Handles response status (with NOT FOUND in case of error)
	 * 
	 * @param username
	 * @param response
	 * @return
	 * @throws IOException
	 */
	private Contact fetchContact(String username, HttpServletResponse response)
			throws IOException
	{
		Contact contact = dao.getContactByUsername(username);
		if (contact == null)
		{
			logger.error("User with username \"" + username + "\" not found");
			response.sendError(HttpStatus.NOT_FOUND.value(), "User with username \""
					+ username + "\" not found");
			return null;
		}

		return contact;
	}

	private boolean validateContact(Contact contact, String password,
			HttpServletResponse response) throws IOException
	{
		if (!contact.getPassword().equals(password))
		{
			logger.error("Invalid password");
			response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid password");
			return false;
		} else
		{
			return true;
		}
	}

	// public SecurityService getSecurityService()
	// {
	// return securityService;
	// }
	//
	// public void setSecurityService(SecurityService securityService)
	// {
	// this.securityService = securityService;
	// }

}

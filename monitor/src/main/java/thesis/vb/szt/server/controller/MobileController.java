package thesis.vb.szt.server.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URLDecoder;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import thesis.vb.szt.server.dao.Dao;
import thesis.vb.szt.server.entity.AgentSet;
import thesis.vb.szt.server.entity.Contact;
import thesis.vb.szt.server.entity.ReportList;
import thesis.vb.szt.server.entity.ReportListRequest;
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
		try
		{
			SecretKey key = Keys.generateSymmetricKeyForMobiles("userpasswordhash");
			String encryptedResponse = securityService.encrypQuery("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><agentSet><agent><id>4</id><address>50_E5_49_4C_65_11</address><name>Dummy</name></agent><agent><id>3</id><address>50_E5_49_4C_65_12</address><name>RemoteAlma</name></agent></agentSet>", key);
			logger.info("encrypted: " + encryptedResponse);
			String decrypted = securityService.decryptQuery(key, encryptedResponse);
			logger.info("encrypted: " + decrypted);
			
		} catch (Exception e)
		{
			logger.error("", e);
		}
	}

	/** Get agents for a given contact. Fetches agents based on username
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/getAgents", method = RequestMethod.GET)
	public void getAgents(HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("Recieved request to login from mobile client");

		OutputStream responseStream = null;
		StringWriter sw = null;
		PrintWriter writer = null;
		try
		{
			final String username = request.getParameter("username");
			//DO NOT USE only here for reference
//			final String encryptedQuery = request.getParameter("encryptedQuery"); //URLDecoder.decode(request.getParameter("encryptedQuery"), "utf-8");

//			if(username == null || encryptedQuery == null ||
//					"".equals(username) || "".equals(encryptedQuery)) {
//				response.sendError(HttpStatus.BAD_REQUEST.value(), "Username, or password parameter was not provided");
//				logger.error("Invalid parameters: " + username + " " + encryptedQuery);
//				return;
//			}
			sw = new StringWriter();
			Contact contact = fetchContact(username, response);
			if (contact != null)
			{
				final SecretKey key = Keys.generateSymmetricKeyForMobiles(contact.getPassword());
//				String decryptedQuery = securityService.decryptQuery(key, encryptedQuery);

				{
					responseStream = response.getOutputStream();

					AgentSet agentSet = new AgentSet(contact.getAgents());

					marshaller.marshal(agentSet, new StreamResult(sw)); 
					
					String encryptedResponse = securityService.encrypQuery(sw.toString(), key);

//					logger.info("Encrypted response is: " + encryptedResponse);
//					logger.info("plain response is: " + decryptedQuery);

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
					if (sw != null)
					{
						sw.close();
					}
					if (writer != null)
					{
						writer.close();
					}
				} catch (IOException e)
				{
					logger.error("Unable to close http response stream, or stringwriter", e);
				}
			}
		}
	}
	
	@RequestMapping(value = "/getLatestReports", method = RequestMethod.GET)
	private void getLatestReports(HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("Recieved request to get latest reports from mobile client");
		final String username = request.getParameter("username");
		/**		Contains the mac address and timestamp	  */
		final String encryptedQuery = request.getParameter("encryptedQuery");
		
		StringWriter sw = null;
		PrintWriter writer = null;
		OutputStream responseStream = null;
		
		try {
			if(username == null || encryptedQuery == null ||
					"".equals(username) || "".equals(encryptedQuery)) {
				response.sendError(HttpStatus.BAD_REQUEST.value(), "Username, or query parameter was not provided");
				logger.error("Invalid parameters: " + username + " " + encryptedQuery);
				return;
			}
			sw = new StringWriter();
			Contact contact = null;

			/** Get contact	*/
			try
			{
				contact = fetchContact(username, response);
				if (contact == null) {
					response.sendError(HttpStatus.BAD_REQUEST.value(), "Unable to find user " + username + " or password is incorrect");
					return;
				}
			} catch (IOException e)
			{
				logger.error("Unable to fetch contact " + username, e);
				return;
			}
			SecretKey key = Keys.generateSymmetricKeyForMobiles(contact.getPassword());
			
			// Decrypted sting is the mac address
			String decryptedQuery = securityService.decryptQuery(key, encryptedQuery);
			
			
			ReportListRequest reportListRequest = (ReportListRequest) unmarshaller
					.unmarshal(new StreamSource(new StringReader(decryptedQuery)));
			
			ReportList reportList = null;
			// get reportlist
			try
			{
				reportList = new ReportList(dao.getReportsForAgentByTimestamp(
						reportListRequest.getMac(), reportListRequest.getFromDate(),
						reportListRequest.getLimit(), false));
				
				//TODO
//				reportList.setCount(dao.getReportCount(reportListRequest.getMac()));
				
				logger.info("ReportList created:\n" + reportList.toString());
			} catch (Exception e)
			{
				logger.error("Unable to get reports", e);
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				return;
			}

			// encrypt reportlist
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
			} catch (IOException e)
			{
				logger.error("Unable to marshal reports", e);
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
			
			
		} catch (Exception e) {
			logger.error("Unable to get latest reports for " + username, e);
		}
		
		
	}
	
	@RequestMapping(value = "/getAgent", method = RequestMethod.GET)
	public void getAgent(HttpServletRequest request, HttpServletResponse response)
	{
		logger.info("Recieved request to get agent from mobile client");

		final String username = request.getParameter("username");
		//Contains the mac address
		final String encryptedQuery = request.getParameter("encryptedQuery");

		StringWriter sw = null;
		PrintWriter writer = null;
		OutputStream responseStream = null;

		try
		{
			if(username == null || encryptedQuery == null ||
					"".equals(username) || "".equals(encryptedQuery)) {
				response.sendError(HttpStatus.BAD_REQUEST.value(), "Username, or query parameter was not provided");
				logger.error("Invalid parameters: " + username + " " + encryptedQuery);
				return;
			}
			
			sw = new StringWriter();
			Contact contact = null;

			// get contact
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
			SecretKey key = Keys.generateSymmetricKeyForMobiles(contact.getPassword());
			
			// Decrypted sting is the mac address
			String decryptedQuery = securityService.decryptQuery(key, encryptedQuery);
			ReportListRequest reportListRequest = (ReportListRequest) unmarshaller
					.unmarshal(new StreamSource(new StringReader(decryptedQuery)));

			ReportList reportList = null;
			// get reportlist
			try
			{
//					reportList = new ReportList(dao.getReportsForAgent(
//							reportListRequest.getMac(), reportListRequest.getFrom(),
//							reportListRequest.getLimit()));
				reportList = new ReportList(dao.getReportsForAgentByTimestamp(
						reportListRequest.getMac(), reportListRequest.getFromDate(),
						reportListRequest.getLimit(), true));
				
				reportList.setCount(dao.getReportCount(reportListRequest.getMac()));
				logger.info("ReportList created:\n" + reportList.toString());
			} catch (Exception e)
			{
				logger.error("Unable to get reports", e);
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				return;
			}

			// encrypt reportlist
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
			} catch (IOException e)
			{
				logger.error("Unable to marshal reports", e);
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
			
		} catch (Exception e)
		{
			logger.error("Unable to get reports for " + username, e);
		} finally
		{
			if (responseStream != null)
			{
				try
				{
					if (sw != null)
					{
						sw.close();
					}
					if (writer != null)
					{
						writer.close();
					}
				} catch (IOException e)
				{
					logger.error("Unable to close http response stream, or stringwriter", e);
				}
			}
		}
	}

	/**
	 * Handles response status (with NOT FOUND in case of error)
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

//	private boolean validateContact(Contact contact, String password,
//			HttpServletResponse response) throws IOException
//	{
//		if (!contact.getPassword().equals(password))
//		{
//			logger.error("Invalid password");
//			response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid password");
//			return false;
//		} else
//		{
//			return true;
//		}
//	}
}

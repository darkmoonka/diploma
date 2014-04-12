package thesis.vb.szt.server.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import thesis.vb.szt.server.entity.Agent;
import thesis.vb.szt.server.entity.Contact;
import thesis.vb.szt.server.util.Notifier;

@Transactional
@Repository
public class Dao
{
	protected static Logger logger = Logger.getLogger("Dao");
	private final String SQL_SEPARATOR = ", ";
	private final String TABLE_PREFIX = "report_";

	@Autowired(required = true)
	Notifier notifier;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	BasicDataSource dataSource;

	public SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

	public Agent getAgentByAddress(String macAddress)
	{
		try
		{
			String hql = "select agent from Agent agent where agent.address = :address";
			Agent agent = (Agent) sessionFactory.getCurrentSession().createQuery(hql)
					.setString("address", macAddress).uniqueResult();

			return agent;
		} catch (Exception e)
		{
			notifier.error(logger, "Agent not found in DB with address: " + macAddress, e);
			// logger.error("Agent not found in DB with address: " + macAddress,
			// e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<Agent> getAgents()
	{
		return (List<Agent>) sessionFactory.getCurrentSession()
				.createSQLQuery("SELECT * FROM Agent").addEntity(Agent.class).list();
	}

	@SuppressWarnings("unchecked")
	public List<Agent> getAgentsByContactId(int id)
	{
		List<Agent> result = null;
		try
		{
			result = (List<Agent>) sessionFactory
					.getCurrentSession()
					.createSQLQuery(
							"SELECT * FROM Agent JOIN Agent_contact ON Agent_contact.agent_id = Agent.agent_id WHERE "
									+ "Agent_contact.agent_id = :agent_id")
					.addEntity(Agent.class).setParameter("agent_id", id).list();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		return result;
	}

//	public Contact getContactByEmail(String email)
//	{
//		try
//		{
//			final String queryString = "SELECT c FROM Contact c " + "WHERE c.email = :email";
//			Contact contact = (Contact) sessionFactory.getCurrentSession()
//					.createQuery(queryString).setString("email", email).uniqueResult();
//			return contact;
//		} catch (Exception e)
//		{
//			logger.error("Unable to get contact by email: " + email);
//			return null;
//		}
//	}
	
	public Contact getContactById(int id)
	{
		try
		{
			return (Contact) sessionFactory.getCurrentSession().get(Contact.class, id);
		} catch (Exception e)
		{
			notifier.error(logger, "Contact not found in DB with id: " + id, e);
			// logger.error("Contact not found in DB with id: " + id, e);
			return null;
		}
	}

	public Agent getAgentById(int agentId)
	{
		try
		{
			return (Agent) sessionFactory.getCurrentSession().get(Agent.class, agentId);
		} catch (Exception e)
		{
			notifier.error(logger, "Unable to get agent with agentId: " + agentId, e);
			// logger.error("Unable to get agent with agentId: " + agentId, e);
			return null;
		}
	}

	public Integer saveAgent(Agent agent)
	{
		logger.info("Received request to add agent to database with address: "
				+ agent.getAddress());

		try
		{
			return (Integer) sessionFactory.getCurrentSession().save(agent);
		} catch (HibernateException e)
		{
			notifier.error(logger,
					"Unable to add agent to database with address: " + agent.getAddress(), e);
			// logger.error(
			// "Unable to add agent to database with address: " +
			// agent.getAddress(), e);
			return null;
		}
	}

	public Integer saveContact(Contact contact)
	{
		logger.info("Received request to add contact to database");

		try
		{
			return (Integer) sessionFactory.getCurrentSession().save(contact);
		} catch (HibernateException e)
		{
			notifier.error(logger, "Unable to add contact to database", e);
			// logger.error("Unable to add contact to database");
			return null;
		}
	}

	public boolean createReportTable(List<String> attributes, String mac)
	{
		StringBuilder queryString = new StringBuilder("CREATE TABLE " + TABLE_PREFIX + mac
				+ " (id int NOT NULL AUTO_INCREMENT" + SQL_SEPARATOR + "timestamp datetime"
				+ SQL_SEPARATOR);
		for (String attribute : attributes)
		{
			queryString.append(attribute + " varchar(100)" + SQL_SEPARATOR);
		}
		queryString.append("PRIMARY KEY (id));");

		Query q = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString());

		try
		{
			q.executeUpdate();
			return true;
		} catch (Exception e)
		{
			logger.error("Cannot create table " + TABLE_PREFIX + mac, e);
			notifier.error(logger, "Cannot create table " + TABLE_PREFIX + mac, e);
			return false;
		}
	}

//	@SuppressWarnings("unchecked")
//	public List<List<Map<String, String>>> getAllReports(int count)
//	{
//		List<List<Map<String, String>>> result = new ArrayList<List<Map<String, String>>>();
//
//		Set<String> tableNames = getTableNames();
//
//		for (String tableName : tableNames)
//		{
//			List<Map<String, String>> tableData = new ArrayList<Map<String, String>>();
//
//			String queryString = "SELECT * FROM " + tableName;
//			Query q = sessionFactory.getCurrentSession().createSQLQuery(queryString);
//
//			if (count > 0)
//			{
//				logger.info("Listing last " + count + " reports from " + tableName);
//				q.setMaxResults(count);
//			} else
//			{
//				logger.info("Listing reports from " + tableName);
//			}
//
//			List<String> reportKeys = getReportAttributes(tableName);
//			List<Object[]> table = (List<Object[]>) q.list();
//
//			for (int row = 0; row < table.size(); row++)
//			{
//				Map<String, String> reportInstance = new HashMap<String, String>();
//				Object tableValues[] = table.get(row);
//				int tableValuesLength = tableValues.length;
//				for (int column = 0; column < tableValuesLength; column++)
//				{
//					String key = reportKeys.get(column);
//					String value = tableValues[column].toString();
//					reportInstance.put(key, value);
//				}
//				tableData.add(reportInstance);
//			}
//			result.add(tableData);
//		}
//		return result;
//	}

	/**
	 * 
	 * @param mac
	 * @param from set to -1 to get all reports
	 * @param limit set to -1 to get all reports
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getReportsForAgent(String mac, int from, int limit)
	{
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		// Set<String> tableNames = getTableNames();

		String tableName = TABLE_PREFIX + mac;
		String query = "SELECT * FROM " + tableName;

		Query q = sessionFactory.getCurrentSession().createSQLQuery(query);
		if(!(from == -1 && limit == -1)) {
			q.setFirstResult(from).setMaxResults(limit);
		}
		
		List<String> reportKeys = getReportAttributes(tableName);
		List<Object[]> table = (List<Object[]>) q.list();

		for (int row = 0; row < table.size(); row++)
		{
			Map<String, String> reportInstance = new HashMap<String, String>();
			Object tableValues[] = table.get(row);
			int tableValuesLength = tableValues.length;
			for (int column = 0; column < tableValuesLength; column++)
			{
				String key = reportKeys.get(column);
				String value = tableValues[column].toString();
				reportInstance.put(key, value);
			}
			result.add(reportInstance);
		}

		return result;
	}

	public int getReportCount(String mac) {
		String tableName = TABLE_PREFIX + mac;
		String query = "SELECT COUNT(*) FROM " + tableName;
		Query q = sessionFactory.getCurrentSession().createSQLQuery(query);
		BigInteger tmp = (BigInteger) q.uniqueResult();
		
		return tmp.intValue();
	}
	
	public boolean insertReport(Map<String, String> report, String mac)
	{
		StringBuilder keyString = new StringBuilder(" (timestamp" + SQL_SEPARATOR + " ");

		for (Entry<String, String> entry : report.entrySet())
		{
			keyString.append(entry.getKey() + SQL_SEPARATOR);
		}
		keyString.replace(keyString.lastIndexOf(SQL_SEPARATOR), keyString.length(), ")");

		StringBuilder valuesString = new StringBuilder(" VALUES (");

		try
		{
			// Date now = new Date();
			// java.sql.Timestamp sqlDate = new
			// java.sql.Timestamp(now.getTime());
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			String dateString = formatter.format(new Date());
			valuesString.append("\"" + dateString + "\"" + SQL_SEPARATOR);
		} catch (Exception e)
		{
			notifier.error(logger, "Cannot parse date", e);
			// logger.error("cannot parse date", e);
		}

		// FIXME keys for sql?
		for (Entry<String, String> entry : report.entrySet())
		{
			valuesString.append("\"" + entry.getValue() + "\"" + SQL_SEPARATOR);
		}
		valuesString.replace(valuesString.lastIndexOf(SQL_SEPARATOR), valuesString.length(),
				");");

		String queryString = "INSERT INTO " + TABLE_PREFIX + mac + keyString.toString()
				+ valuesString.toString();
		// TODO parameters for query instead of query string
		Query q = sessionFactory.getCurrentSession().createSQLQuery(queryString);
		int updatedNum = q.executeUpdate();
		if (updatedNum == 1)
		{
			logger.info("Saved report into table " + TABLE_PREFIX + mac);
			return true;
		} else
		{
			notifier.error(logger, "Inserted " + updatedNum + " reports instead of 1.", null);
			// logger.error("Inserted " + updatedNum +
			// " reports instead of 1.");
			return false;
		}
	}

	private Set<String> getTableNames()
	{
		Set<String> tableNames = new HashSet<String>();
		DatabaseMetaData databaseMetaData;
		Connection conn = null;
		try
		{
			conn = dataSource.getConnection();
			databaseMetaData = conn.getMetaData();

			ResultSet columns = databaseMetaData.getTables("monitor", "monitor", null, null);
			while (columns.next())
			{
				String tableName = columns.getString(3);
				if (tableName.contains(TABLE_PREFIX))
					tableNames.add(tableName);
			}
		} catch (Exception e)
		{
			notifier.error(logger, "Unable to get table columns", e);
			// logger.error("Unable to get table columns", e);
			return null;
		} finally
		{
			try
			{
				conn.close();
			} catch (Exception e)
			{
				notifier.error(logger, "Unable to close database connection", e);
				// logger.error("Unable to close database connection", e);
			}
		}

		return tableNames;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getAgentNamesAndMacs()
	{
		String query = "SELECT name, address FROM Agent";
		List<Map<String, String>> result = sessionFactory.getCurrentSession()
				.createQuery(query).list();

		return result;
	}

	private List<String> getReportAttributes(String tableName)
	{
		List<String> result = new ArrayList<String>();
		DatabaseMetaData databaseMetaData;
		Connection conn = null;
		try
		{
			conn = dataSource.getConnection();
			databaseMetaData = conn.getMetaData();

			ResultSet columns = databaseMetaData.getColumns("monitor", "monitor", tableName,
					null);
			while (columns.next())
			{
				try
				{
					String columnName = columns.getString(4); // TODO column
																// name
					if (!StringUtils.isEmpty(columnName))
					{
						logger.info(columnName);
						result.add(columnName);
					} else
					{
						logger.info("Empty attribute found");
					}
				} catch (Exception e)
				{
					logger.info("Attribute error", e);
				}
			}
			return result;
		} catch (Exception e)
		{
			notifier.error(logger, "Unable to get table columns", e);
			// logger.error("Unable to get table columns", e);
			return null;
		} finally
		{
			try
			{
				conn.close();
			} catch (Exception e)
			{
				notifier.error(logger, "Unable to close database connection", e);
				// logger.error("Unable to close database connection", e);
			}
		}
	}

	// public List<Map<String, String>> getReports(String address)
	// {
	// //TODO
	// return null;
	// }

	public boolean createContact(String name, String username, String password, String email, String phone)
	{
		try
		{
			Contact contact = new Contact();
			contact.setName(name);
			contact.setUsername(username);
			contact.setPassword(password);
			contact.setPhoneNumber(phone);
			contact.setEmail(email);
			
			sessionFactory.getCurrentSession().persist(contact);

//			sessionFactory.getCurrentSession().flush();
			logger.info("Successfully created contact: " + username);
			return true;
		} catch (Exception e)
		{
			logger.error("Error during creating user with username: " + username, e);
			return false;
		}
	}

	public Contact getContactByUsername(String username)
	{
		try
		{
			final String queryString = "SELECT c FROM Contact c"
					+ " WHERE c.username = :username";
			Contact contact = (Contact) sessionFactory.getCurrentSession()
					.createQuery(queryString).setString("username", username).uniqueResult();
			if(contact != null) {
				contact.fillAgentSet();
			}
			return contact;
		} catch (Exception e)
		{
			logger.error("Unable to fetch user \"" + username + "\" from database", e);
			return null;
		}
	}

}
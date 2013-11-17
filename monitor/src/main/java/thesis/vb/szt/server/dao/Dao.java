package thesis.vb.szt.server.dao;

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
	private final String TABLE_PREFIX = "Report_";

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
			Notifier.error(logger, "Agent not found in DB with address: " + macAddress, e);
			// logger.error("Agent not found in DB with address: " + macAddress,
			// e);
			return null;
		}
	}

	public Contact getContactById(int id)
	{
		try
		{
			return (Contact) sessionFactory.getCurrentSession().get(Contact.class, id);
		} catch (Exception e)
		{
			Notifier.error(logger, "Contact not found in DB with id: " + id, e);
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
			Notifier.error(logger, "Unable to get agent with agentId: " + agentId, e);
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
			Notifier.error(logger,
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
			Notifier.error(logger, "Unable to add contact to database", e);
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
			Notifier.error(logger, "Cannot create table " + TABLE_PREFIX + mac, e);
			// logger.error("Cannot create table " + TABLE_PREFIX + mac, e);
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public List<List<Map<String, String>>> getAllReports(int count)
	{
		List<List<Map<String, String>>> result = new ArrayList<List<Map<String, String>>>();

		Set<String> tableNames = getTableNames();

		for (String tableName : tableNames)
		{
			List<Map<String, String>> tableData = new ArrayList<Map<String, String>>();

			String queryString = "SELECT * FROM " + tableName;
			Query q = sessionFactory.getCurrentSession().createSQLQuery(queryString);

			if (count > 0)
			{
				logger.info("Listing last " + count + " reports from " + tableName);
				q.setMaxResults(count);
			} else
			{
				logger.info("Listing reports from " + tableName);
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
				tableData.add(reportInstance);
			}
			result.add(tableData);
		}
		return result;
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
//			Date now = new Date();
//			java.sql.Timestamp sqlDate = new java.sql.Timestamp(now.getTime());
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			String dateString = formatter.format(new Date());
			valuesString.append("\"" + dateString + "\"" + SQL_SEPARATOR);
		} catch (Exception e)
		{
			Notifier.error(logger, "Cannot parse date", e);
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
			Notifier.error(logger, "Inserted " + updatedNum + " reports instead of 1.", null);
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
			Notifier.error(logger, "Unable to get table columns", e);
			// logger.error("Unable to get table columns", e);
			return null;
		} finally
		{
			try
			{
				conn.close();
			} catch (Exception e)
			{
				Notifier.error(logger, "Unable to close database connection", e);
				// logger.error("Unable to close database connection", e);
			}
		}

		return tableNames;
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
			Notifier.error(logger, "Unable to get table columns", e);
			// logger.error("Unable to get table columns", e);
			return null;
		} finally
		{
			try
			{
				conn.close();
			} catch (Exception e)
			{
				Notifier.error(logger, "Unable to close database connection", e);
				// logger.error("Unable to close database connection", e);
			}
		}
	}
}
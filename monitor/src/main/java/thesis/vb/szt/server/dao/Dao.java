package thesis.vb.szt.server.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
import thesis.vb.szt.server.entity.Report;

@Transactional
@Repository
public class Dao
{
	protected static Logger logger = Logger.getLogger("Dao");
	private final String SQL_SEPARATOR = ", ";
	private final String TABLE_PREFIX = "Report_";

	@Autowired
	private SessionFactory sessionFactory;

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
			logger.error("Agent not found in DB with address: " + macAddress, e);
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
			logger.error(
					"Unable to add agent to database with address: " + agent.getAddress(), e);
			return null;
		}
	}

	public void saveReport(Report report)
	{
		logger.info("Received request to add report to database");

		try
		{
			sessionFactory.getCurrentSession().save(report);
		} catch (HibernateException e)
		{
			logger.error("Unable to add report to database");
		}
	}

	@Autowired
	BasicDataSource dataSource;

	// @SuppressWarnings("unchecked")
	// public List<Report> getAllReports()
	// {
	// DatabaseMetaData databaseMetaData;
	// try
	// {
	// Connection c = dataSource.getConnection();
	// databaseMetaData = c.getMetaData();
	// // ResultSet catalogs = databaseMetaData.getCatalogs();
	//
	// ResultSet columns = databaseMetaData.getColumns("monitor", "monitor",
	// "Agent",
	// null);// getTables("monitor", "monitor", "agent", null);
	// int i = 0;
	// while (columns.next())
	// {
	// try
	// {
	// i++;
	// logger.info(columns.getString(4)); //TODO column name
	// // Ref ref = columns.getRef(3);
	// // ref.toString();
	// } catch (Exception e)
	// {
	// logger.info("", e);
	// }
	// }
	//
	// // ResultSetMetaData columnsMetaData = columns.getMetaData();
	// // int columnCount = columnsMetaData.getColumnCount();
	// // for(int i = 1; i < columnCount; i++) {
	// // String columnName =
	// // columnsMetaData.getTableName(i);//ColumnLabel(i);
	// // logger.info(columnName);
	// // }
	//
	// // while(catalogs.next()) {
	// // catalogs.getm
	// // }
	//
	// logger.info("");
	// // Type[] types =
	// //
	// sessionFactory.getCurrentSession().createSQLQuery("Select * from Report");
	// } catch (Exception e)
	// {
	// String a = "";
	// e.printStackTrace();
	// }
	//
	// return
	// sessionFactory.getCurrentSession().createQuery("from Report").list();
	// }

	public boolean createReportTable(List<String> attributes, String mac)
	{
		StringBuilder queryString = new StringBuilder("CREATE TABLE " + TABLE_PREFIX + mac
				+ " (");
		for (String attribute : attributes)
		{
			queryString.append(attribute + " varchar(100)" + SQL_SEPARATOR);
		}
		queryString
				.replace(queryString.lastIndexOf(SQL_SEPARATOR), queryString.length(), ");");

		Query q = sessionFactory.getCurrentSession().createSQLQuery(queryString.toString());
		int insertNum = 0;

		try
		{
			insertNum = q.executeUpdate();
		} catch (Exception e)
		{
			logger.error("Cannot create table " + TABLE_PREFIX + mac, e);
			return false;
		}

		if (insertNum == 1)
		{
			logger.info("Created report " + mac);
			return true;
		} else
		{
			logger.error("Unable to create report. Query execution returned with " + insertNum
					+ " instead of 1.");
			return false;
		}

	}

	@SuppressWarnings("unchecked")
	public List<Map<String, String>> listReports(int count, String mac)
	{
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();

		String queryString = "SELECT * FROM " + TABLE_PREFIX + mac;
		Query q = sessionFactory.getCurrentSession().createSQLQuery(queryString);

		if (count > 0)
		{
			logger.info("Listing last " + count + "reports from " + TABLE_PREFIX + mac);
			q.setMaxResults(count);
		} else
		{
			logger.info("Listing reports from " + TABLE_PREFIX + mac);
		}

		List<String> reportKeys = getReportAttributes(mac);
		List<Object[]> table = (List<Object[]>) q.list();
		int reportAttrNum = reportKeys.size();

		for (int i = 0; i < reportAttrNum; i++)
		{
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
		}

		return result;
	}

	public boolean insertReport(Map<String, String> report, String mac)
	{
		// INSERT INTO table_name (column1,column2,column3,...)
		// VALUES (value1,value2,value3,...);

		StringBuilder keyString = new StringBuilder(" (");

		for (Entry<String, String> entry : report.entrySet())
		{
			keyString.append(entry.getKey() + SQL_SEPARATOR);
		}
		keyString.replace(keyString.lastIndexOf(SQL_SEPARATOR), keyString.length(), ")");

		StringBuilder valuesString = new StringBuilder(" VALUES (");

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
			logger.info("Saved report " + mac);
			return true;
		} else
		{
			logger.error("Inserted " + updatedNum + " reports instead of 1.");
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getReport(String mac)
	{

		Map<String, String> result = new HashMap<String, String>();
		List<String> attributes = getReportAttributes(mac);
		List<String> values = sessionFactory.getCurrentSession()
				.createQuery("from Report-" + mac).list();

		if (attributes.size() != values.size())
		{
			logger.error("Unable to get report. Attribute and value cound isn't equal.");
			logger.error("Number of attributes:" + attributes.size());
			logger.error("Number of values:" + values.size());
			return null;
		}

		for (int i = 0; i < attributes.size(); i++)
		{
			result.put(attributes.get(i), values.get(i));
		}

		return result;
	}

	private List<String> getReportAttributes(String mac)
	{
		List<String> result = new ArrayList<String>();
		DatabaseMetaData databaseMetaData;
		try
		{
			Connection c = dataSource.getConnection();
			databaseMetaData = c.getMetaData();

			ResultSet columns = databaseMetaData.getColumns("monitor", "monitor", "Report_"
					+ mac, null);
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
			logger.error("Unable to get table columns", e);
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
			logger.error("Unable to get agent with agentId: " + agentId, e);
			return null;
		}
	}
	//
	//
	// public List<Vehicle> getVehicles(Criteria criteria)
	// {
	// logger.debug("Received request to search for a vehicles with criteria: "
	// + criteria.toString());
	//
	// //Query query =
	// sessionFactory.getCurrentSession().getNamedQuery("critieria.search");
	// String queryString =
	// "SELECT * FROM vehicle WHERE price BETWEEN :price_min AND :price_max AND "
	// + "year BETWEEN :year_min AND :year_max";
	//
	// Query query =
	// sessionFactory.getCurrentSession().createSQLQuery(queryString,"",Vehicle.class);
	// query.setParameter("price_min", criteria.getPrice_min());
	// query.setParameter("price_max", criteria.getPrice_max());
	//
	// query.setParameter("year_min", criteria.getYear_min());
	// query.setParameter("year_max", criteria.getYear_max());
	//
	// List<Vehicle> result = (List<Vehicle>) query.list();
	// //Vehicle c = (Vehicle) result.get(0);
	// return result;
	// }

	// public Poi getPoi(Integer id) {
	// logger.info("Received request to retrieve a poi from the database");
	//
	// Poi poi = null;
	// try {
	// poi = (Poi) sessionFactory.getCurrentSession().get(Poi.class, id);
	// } catch (HibernateException e) {
	// logger.error("Unable to load poi from database", e);
	// }
	// return poi;
	// }
	//
	// public boolean addPoi(Poi poi) {
	// logger.info("Received request to add poi to database.");
	//
	// try {
	// sessionFactory.getCurrentSession().save(poi);
	// return true;
	// } catch (HibernateException e) {
	// logger.error("Unable to add poi to database.", e);
	// return false;
	// }
	// }
	//
	// public boolean updatePoi(Poi poi) {
	// logger.info("Received request to update poi in database.");
	//
	// try {
	// sessionFactory.getCurrentSession().update(poi);
	// return true;
	// } catch (HibernateException e) {
	// logger.error("Unable to add poi to database.", e);
	// return false;
	// }
	// }
	//
	// @SuppressWarnings("unchecked") // the result list of the query may
	// contain any kind of object, not only Pois
	// public List<Poi> search(Poi criteria) {
	// logger.debug("Received request to search for a poi in the database");
	//
	// Query query =
	// sessionFactory.getCurrentSession().getNamedQuery("poi.search");
	// query.setParameter("name", "%" + criteria.getName() + "%");
	// query.setParameter("type", "%" + criteria.getType() + "%");
	// query.setParameter("address", "%" + criteria.getAddress() + "%");
	//
	// return query.list();
	// }
	//
	// public User getUser(String username) throws NotFoundException{
	// logger.info("Received request to retrieve a user from the database.");
	//
	// User user = null;
	// try {
	// user = (User) sessionFactory.getCurrentSession().get(User.class,
	// username);
	// if(user == null)
	// throw new NotFoundException("No such user.");
	// } catch (HibernateException e) {
	// logger.error("Unable to load user from database.", e);
	// }
	// return user;
	// }
	//
	// public void addUser(User user) {
	// logger.info("Received request to add user to database.");
	//
	// try {
	// sessionFactory.getCurrentSession().save(user);
	// } catch (HibernateException e) {
	// logger.error("Unable to add user to database.", e);
	// }
	// }
}

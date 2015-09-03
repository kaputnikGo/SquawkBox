import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SqlTalk {
	private boolean DEBUG = true;
	private SquawkView squawkView;
	
	private static final String DEFAULT_DB = "squawk.db";	
	private static final String ADMIN_TABLE = "ADMIN";
	private static final String DEVICE_TABLE = "DEVICES";
	private static final String WRAPPER_TABLE = "WRAPPERS";
	
	private Connection sqlConnection = null;
	private Statement sqlStatement = null;
	
	SqlTalk(final SquawkView squawkView) {	
		this.squawkView = squawkView;
		
		// yar constuctor
		// need to include a button and method that wraps the device in site specific html so it
		// looks like what it will on the actual site.	
	}
	
	public String getDefaultDB() {
		return DEFAULT_DB;
	}	
	public String getAdminTable() {
		return ADMIN_TABLE;
	}
	public String getDeviceTable() {
		return DEVICE_TABLE;
	}
	public String getWrapperTable() {
		return WRAPPER_TABLE;
	}
	
/************************************************************
* 
* 		// public methods 
* 
************************************************************/		
	protected void installDB() {
		// testing method, print to console.
		installAdminTable();
		debug(Utilities.countAdminDB(DEFAULT_DB));				
		installDeviceTable();
		installWrapperTable();
	}
	
	protected void saveDeviceRecord(final String webcode, final String devicetype, final String deviceHtml) {
		//TODO
		// deal with errors here
		// need webdcode and devicetype
		if (!Utilities.checkString(webcode)) {
			debug("Save device webcode is null or empty.");
			return;
		}
		if (!Utilities.checkString(devicetype)) {
			debug("Save device type is null or empty.");
			return;
		}
		// db design allows for empty html, in case of reserving a record for future device
		if (!Utilities.checkString(deviceHtml)) {
			debug("Save device html is null or empty.");
			return;
		}
		// check if device table exists
		if (checkTableExists(DEVICE_TABLE)) {
			// skip it all.
			debug("Device table exists, can write.");
			if (Utilities.isValidWebcode(webcode)) {
				if (Utilities.isValidDevicetype(devicetype)) {
					insertDeviceRecord(webcode, devicetype, deviceHtml);
					Utilities.debugDeviceDB(DEFAULT_DB);
				}			
			}
			else {
				debug("Device save webcode and/or devicetype invalid");
			}
		}
		else {
			debug("Device table not found");
		}		
		debug("Device " + devicetype + " record for " + webcode + " saved in DB.");
	}
	
	protected void saveWrapperRecord(final String webcode, final String devicetype, final String startHtml, final String endHtml) {
		// deal with errors here
		// need webdcode and devicetype
		if (!Utilities.checkString(webcode)) {
			debug("Save wrapper webcode is null or empty.");
			return;
		}
		if (!Utilities.checkString(devicetype)) {
			debug("Save wrapper type is null or empty.");
			return;
		}
		// db design allows for empty html, in case of reserving a record for future device
		if (!Utilities.checkString(startHtml)) {
			debug("Save wrapper start html is null or empty.");
			return;
		}
		if (!Utilities.checkString(endHtml)) {
			debug("Save wrapper end html is null or empty.");
			return;
		}
		// check if device table exists
		if (checkTableExists(WRAPPER_TABLE)) {
			// skip it all.
			debug("Wrapper table exists, can write.");
			if (Utilities.isValidWebcode(webcode)) {
				if (Utilities.isValidDevicetype(devicetype)) {
					insertWrapperRecord(webcode, devicetype, startHtml, endHtml);
					//Utilities.debugDeviceDB(DEFAULT_DB);
				}			
			}
			else {
				debug("Wrapper save webcode and/or devicetype invalid");
			}
		}
		else {
			debug("Wrapper table not found");
		}		
		debug("Wrapper device " + devicetype + " record for " + webcode + " saved in DB.");
	}
	
	protected void addNewWebsite() {
		//TODO
		// add new site
		// need webcode, website url, stylesheets?
		String webcode = "";
		String site_url = "";
		
		//String style1url = "no";
		//String style2url = "none";
		//String style3url = "huh";
		
		addWebsiteRecord(webcode, site_url, null, null, null);
	}
	
/************************************************************
* 
* 		// internal methods 
* 
************************************************************/
	private void debug(final String message) {
		if (DEBUG) squawkView.updateConsole(message);
	}
	private boolean installAdminTable() {
		// is it already created?
		if (checkTableExists(ADMIN_TABLE)) {
			// skip it all.
			debug("Admin table already exists.");
			return true;
		}
		else {
			createAdminTable();
			populateAdminTable();		
			return true;
		}
	}
	
	private boolean installDeviceTable() {		
		if (checkTableExists(DEVICE_TABLE)) {
			// skip it all.
			debug("Device table already exists.");
			return true;
		}
		else {
			createDeviceTable();
			return true;
		}
	}
	
	private boolean installWrapperTable() {
		if (checkTableExists(WRAPPER_TABLE)) {
			// skip it all.
			debug("Wrapper table already exists.");
			return true;
		}
		else {
			createWrapperTable();
			return true;
		}
	}
	
/************************************************************
* 
* 		// database methods 
* 
***********************************************************/	
	private boolean createAdminTable() {			
		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
			return false;
		}
		try {
			sqlStatement = null;
			sqlStatement = sqlConnection.createStatement();
			String sqlString = "CREATE TABLE IF NOT EXISTS " + ADMIN_TABLE +
					"(ID INTEGER PRIMARY KEY	AUTOINCREMENT," +
					"WEBCODE			TEXT	NOT NULL," +
					"WEBURL				TEXT	NOT NULL," +					
					"DEVICE_1			TEXT," +
					"DEVICE_2			TEXT," +
					"DEVICE_3			TEXT," +
					"DEVICE_4			TEXT," +
					"STYLE_URL_1		TEXT," +
					"STYLE_URL_2		TEXT," +
					"STYLE_URL_3		TEXT)";
					
			sqlStatement.executeUpdate(sqlString);
			sqlStatement.close();
			// then populate table
		} catch (SQLException e) {
			debug("SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Database " + DEFAULT_DB + " and Table " + ADMIN_TABLE + " created successfully.");
		            return true;
		        }
			}
		    catch(SQLException e) {
		    	debug("SQL connection close failure.");
		        System.err.println(e);
		        return false;
		    }
		}
		return false;
	}
	
	private boolean populateAdminTable() {
		debug("populateAdminTable called.");		
		// in order, current as of : 28/08/2015
		String mmUrl = "http://www.moneymorning.com.au";
		String mm1 = "http://www.moneymorning.com.au/wp-content/themes/shoestrap-leadgen/style.css";
		String mm2 = "http://www.moneymorning.com.au/wp-content/uploads/ss-style.css";

		String drUrl = "http://www.dailyreckoning.com.au";
		String dr1 = "http://www.dailyreckoning.com.au/wp-content/themes/shoestrap-leadgen/style.css";
		String dr2 = "http://www.dailyreckoning.com.au/wp-content/uploads/ss-style.css";
		// not used?
		//String dr3 = "http://www.dailyreckoning.com.au/wp-content/themes/zenko/custom.css";

		String eskyUrl ="http://escapologist.com.au";
		String esky1 = "http://escapologist.com.au/wp-content/themes/escapologist/css/bootstrap.min.css";
		String esky2 = "http://escapologist.com.au/wp-content/themes/escapologist/style.css";
		String esky3 = "http://escapologist.com.au/wp-content/themes/escapologist/css/esky-custom.css";

		String ilaUrl ="http://internationalliving.com.au";
		String ila1 = "http://internationalliving.com.au/wp-content/uploads/ss-style.css";
		String ila2 = "http://internationalliving.com.au/wp-content/themes/shoestrap-leadgen/style.css";		 

		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
			return false;
		}
		try {
			if (sqlConnection != null) {
				debug("sqlConnection made...");
			}
// BUILD PROPER INSERT			
			sqlConnection.setAutoCommit(false);
			
			// MoneyMorning			
			String insertSQL = "INSERT INTO ADMIN (WEBCODE, WEBURL, "
					+ "DEVICE_1, DEVICE_2, DEVICE_3, DEVICE_4, "
					+ "STYLE_URL_1, STYLE_URL_2, STYLE_URL_3) "
					+ "VALUES (?,?,?,?,?,?,?,?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
		    query.setString(1, "MM");
		    query.setString(2, mmUrl);
		    query.setString(3, "EOA");
		    query.setString(4, "IF");
		    query.setString(5, "PU");
		    query.setString(6, "SF");
		    query.setString(7, mm1);
		    query.setString(8, mm2);
		    query.setString(9, null);
		    query.addBatch();
		    debug("Record for MM batched.");
		    
		    // daily reckoning
		    query.setString(1, "DR");
		    query.setString(2, drUrl);
		    query.setString(3, "EOA");
		    query.setString(4, "IF");
		    query.setString(5, "PU");
		    query.setString(6, "SF");
		    query.setString(7, dr1);
		    query.setString(8, dr2);
		    query.setString(9, null);		    
		    query.addBatch();
		    debug("Record for DR batched.");
		    
		    // Escapologist
		    query.setString(1, "ESKY");
		    query.setString(2, eskyUrl);
		    query.setString(3, "EOA");
		    query.setString(4, "IF");
		    query.setString(5, "PU");
		    query.setString(6, "SF");
		    query.setString(7, esky1);
		    query.setString(8, esky2);
		    query.setString(9, esky3);		    
		    query.addBatch();
		    debug("Record for ESKY added successfully.");
		    
		    // IL Postcards
		    query.setString(1, "ILA");
		    query.setString(2, ilaUrl);
		    query.setString(3, "EOA");
		    query.setString(4, "IF");
		    query.setString(5, "PU");
		    query.setString(6, "SF");
		    query.setString(7, ila1);
		    query.setString(8, ila2);
		    query.setString(9, null);
		    query.addBatch();
		    debug("Record for ILA added successfully.");
		    
		    int[] updateCounts = query.executeBatch();
		    if (updateCounts != null) {		    	
		    	debug("Install DB batch count: " + updateCounts.length);		    	
		    }
		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    
		    if (query != null) {
		    	query = null;
		    }

		} catch (SQLException e) {
			debug("SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Database " + DEFAULT_DB + " and Table " + ADMIN_TABLE + " connection closed.");
		            return true;
		        }
			}
		    catch(SQLException e) {
		    	debug("SQL connection close failure.");
		        System.err.println(e);
		        return false;
		    }
		}
		return false;
	}
	
	private boolean addWebsiteRecord(final String webcode, final String site_url, 
			final String style1url, final String style2url, final String style3url) {
		
		// assume by here that all the variables are valid
		// webcode and website url can not be null - all others can.
		
		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
			return false;
		}
		try {
			if (sqlConnection != null) {
				debug("sqlConnection made...");
			}
// BUILD PROPER INSERT			
			sqlConnection.setAutoCommit(false);
			
			// MoneyMorning			
			String insertSQL = "INSERT INTO ADMIN (WEBCODE, WEBURL, "
					+ "DEVICE_1, DEVICE_2, DEVICE_3, DEVICE_4, "
					+ "STYLE_URL_1, STYLE_URL_2, STYLE_URL_3) "
					+ "VALUES (?,?,?,?,?,?,?,?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
		    // new site
		    query.setString(1, webcode);
		    query.setString(2, site_url);
		    query.setString(3, "EOA");
		    query.setString(4, "IF");
		    query.setString(5, "PU");
		    query.setString(6, "SF");
		    query.setString(7, style1url);
		    query.setString(8, style2url);
		    query.setString(9, style3url);		    
		    query.addBatch();
		    debug("Record for new site added successfully.");
			
			
			int[] updateCounts = query.executeBatch();
		    if (updateCounts != null) {		    	
		    	debug("Install DB batch count: " + updateCounts.length);		    	
		    }
		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    
		    if (query != null) {
		    	query = null;
		    }
	
		} catch (SQLException e) {
			debug("SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Database " + DEFAULT_DB + " and Table " + ADMIN_TABLE + " connection closed.");
		            return true;
		        }
			}
		    catch(SQLException e) {
		    	debug("SQL connection close failure.");
		        System.err.println(e);
		        return false;
		    }
		}
			return false;
	}
	
	private boolean createDeviceTable() {					
		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
			return false;
		}
		try {
			sqlStatement = null;
			sqlStatement = sqlConnection.createStatement();
			// create table first - an entry of ID has a WEB_CODE, DEVICE_TYPE and possibly the actual
			// this is to allow reserving the devicetype in case in is not yet implemented on the website
			String sqlString = "CREATE TABLE IF NOT EXISTS " + DEVICE_TABLE +
					"(ID INTEGER PRIMARY KEY	AUTOINCREMENT," +
					"WEBCODE			TEXT	NOT NULL," +
					"DEVICETYPE			TEXT	NOT NULL," +					
					"DEVICE_HTML		TEXT)";
					
			sqlStatement.executeUpdate(sqlString);
			sqlStatement.close();
			// then populate table
		} catch (SQLException e) {
			debug("SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Database " + DEFAULT_DB + " and Table " + DEVICE_TABLE + " created successfully.");
		            return true;
		        }
			}
		    catch(SQLException e) {
		    	debug("SQL connection close failure.");
		        System.err.println(e);
		        return false;
		    }
		}
		return false;
	}
	
	private boolean insertDeviceRecord(final String webcode, final String devicetype, final String devicehtml) {
		// receives data from public methods and inserts into db 
		// 
		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
			return false;
		}
		try {
			if (sqlConnection != null) {
				debug("sqlConnection made...");
			}			
			sqlStatement = null; 
			sqlConnection.setAutoCommit(false);
			String insertSQL = "INSERT INTO " + DEVICE_TABLE 
					+ " (WEBCODE, DEVICETYPE, DEVICE_HTML) "
					+ "VALUES (?,?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
			
		    query.setString(1, webcode);
		    query.setString(2, devicetype);
		    query.setString(3, devicehtml);
		    query.addBatch();
		    
		    debug("Record for device batched.");
		    int[] updateCounts = query.executeBatch();
		    if (updateCounts != null) {		    	
		    	debug("Install DB batch count: " + updateCounts.length);		    	
		    }
		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    
		    if (query != null) {
		    	query = null;
		    }		    
		} 
		catch (SQLException e) {
			debug("SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Database " + DEFAULT_DB + " and Table " + DEVICE_TABLE + " connection closed.");
		            return true;
		        }
			}
		    catch(SQLException e) {
		    	debug("SQL connection close failure.");
		        System.err.println(e);
		        return false;
		    }
		}
		return false;
	}
	
	private boolean createWrapperTable() {
		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
			return false;
		}
		try {
			sqlStatement = null;
			sqlStatement = sqlConnection.createStatement();
			// create table first - an entry of ID has a WEB_CODE, DEVICE_TYPE, START_HTML, END_HTML
			String sqlString = "CREATE TABLE IF NOT EXISTS " + WRAPPER_TABLE +
					"(ID INTEGER PRIMARY KEY	AUTOINCREMENT," +
					"WEBCODE			TEXT	NOT NULL," +
					"DEVICETYPE			TEXT	NOT NULL," +					
					"START_HTML			TEXT," + 
					"END_HTML			TEXT)";
					
			sqlStatement.executeUpdate(sqlString);
			sqlStatement.close();
			// then populate table
		} catch (SQLException e) {
			debug("SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Database " + DEFAULT_DB + " and Table " + DEVICE_TABLE + " created successfully.");
		            return true;
		        }
			}
		    catch(SQLException e) {
		    	debug("SQL connection close failure.");
		        System.err.println(e);
		        return false;
		    }
		}
		return false;
	}
	
	private boolean insertWrapperRecord(final String webcode, final String devicetype, final String startHtml, final String endHtml) {
		// receives data from public methods and inserts into db 
		// WEB_CODE, DEVICE_TYPE, START_HTML, END_HTML
		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
			return false;
		}
		try {
			if (sqlConnection != null) {
				debug("sqlConnection made...");
			}			
			sqlStatement = null; 
			sqlConnection.setAutoCommit(false);
			String insertSQL = "INSERT INTO " + WRAPPER_TABLE 
					+ " (WEBCODE, DEVICETYPE, START_HTML, END_HTML) "
					+ "VALUES (?,?,?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
			
		    query.setString(1, webcode);
		    query.setString(2, devicetype);
		    query.setString(3, startHtml);
		    query.setString(4, endHtml);
		    query.addBatch();
		    
		    debug("Record for device batched.");
		    int[] updateCounts = query.executeBatch();
		    if (updateCounts != null) {		    	
		    	debug("Install DB batch count: " + updateCounts.length);		    	
		    }
		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    
		    if (query != null) {
		    	query = null;
		    }		    
		} 
		catch (SQLException e) {
			debug("SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Database " + DEFAULT_DB + " and Table " + WRAPPER_TABLE + " connection closed.");
		            return true;
		        }
			}
		    catch(SQLException e) {
		    	debug("SQL connection close failure.");
		        System.err.println(e);
		        return false;
		    }
		}
		return false;
	}
	
	private boolean checkTableExists(final String tablename) {
		// query that a "tablename" exists
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
			DatabaseMetaData dbm;
			
			dbm = sqlConnection.getMetaData();
			ResultSet rs = dbm.getTables(null,  null,  tablename, null);
			if (rs.next()) {
				debug("checkTables found table: " + tablename);
				return true;
			}
			else {
				debug("checkTable unable to find: " + tablename);
			}
		} 
		catch (SQLException e1) {
			debug("checkTables SQL error.");
			e1.printStackTrace();
		} 
		catch (Exception e) {
			debug("checkTables connection error.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("checkTables connection closed.");
		            return true;
		        }
			}
		    catch(SQLException e) {
		    	debug("SQL connection close failure.");
		        System.err.println(e);
		        return false;
		    }
		}
		return false;		
	}
}

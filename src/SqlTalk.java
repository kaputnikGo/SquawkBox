import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class SqlTalk {
	private boolean DEBUG = true;
	private SquawkView squawkView;
	
	private static final String DEFAULT_DB = "squawk.db";	
	private static final String ADMIN_TABLE = "ADMIN";
	private static final String DEVICE_TABLE = "DEVICES";
	
	private Connection sqlConnection = null;
	private Statement sqlStatement = null;
	
	SqlTalk(final SquawkView squawkView) {	
		this.squawkView = squawkView;	
		// yar constuctor
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
	
/************************************************************
* 
* 		// public methods 
* 
************************************************************/		
	protected void installDB() {
		// testing method, print to console.
		installAdminTable();				
		//installDeviceTable();
		// debug routines
		debug(Utilities.debugCountTableDB(DEFAULT_DB, ADMIN_TABLE));
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
		if (Utilities.checkTableExists(DEFAULT_DB, DEVICE_TABLE)) {
			// skip it all.
			debug("Device table exists, can write.");
			if (Utilities.isValidWebcode(webcode)) {
				if (Utilities.isValidDevicetype(devicetype)) {
					insertDeviceRecord(webcode, devicetype, deviceHtml);
					Utilities.debugDumpTableDB(DEFAULT_DB, DEVICE_TABLE);
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
	
	protected void addNewRecord() {
		//TODO
		// add new site
		// need webcode, website url, stylesheets?
		String webcode = "PTR";
		String name = "testing new";
		
		String comp1 = "no";
		String comp2 = "none";
		String comp3 = "huh";
		String comp4 = "yup";
		
		insertNewRecord(webcode, name, comp1, comp2, comp3, comp4);
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
		if (Utilities.checkTableExists(DEFAULT_DB, ADMIN_TABLE)) {
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
	
	/*
	private boolean installDeviceTable() {		
		if (Utilities.checkTableExists(DEFAULT_DB, DEVICE_TABLE)) {
			// skip it all.
			debug("Device table already exists.");
			return true;
		}
		else {
			createDeviceTable();
			return true;
		}
	}
	*/
	
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
					"NAME				TEXT	NOT NULL," +					
					"COMP_1			TEXT," +
					"COMP_2			TEXT," +
					"COMP_3			TEXT," +
					"COMP_4			TEXT)";
					
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
			
			// 			
			String insertSQL = "INSERT INTO ADMIN (WEBCODE, NAME, "
					+ "COMP_1, COMP_2, COMP_3, COMP_4) "
					+ "VALUES (?,?,?,?,?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
		    query.setString(1, "MM");
		    query.setString(2, "money morning premium");
		    query.setString(3, null);
		    query.setString(4, null);
		    query.setString(5, null);
		    query.setString(6, null);
		    query.addBatch();
		    debug("Record for MM batched.");
		    
		    // 
		    query.setString(1, "ELH");
		    query.setString(2, "employment law handbook bulletin");
		    query.setString(3, null);
		    query.setString(4, null);
		    query.setString(5, null);
		    query.setString(6, null);		    
		    query.addBatch();
		    debug("Record for ELH batched.");
		    
		    //
		    query.setString(1, "HSH");
		    query.setString(2, "health and safety handbook bulletin");
		    query.setString(3, null);
		    query.setString(4, null);
		    query.setString(5, null);
		    query.setString(6, null);		    
		    query.addBatch();
		    debug("Record for HSH added successfully.");
		    
		    //
		    query.setString(1, "PTR");
		    query.setString(2, "portner press generic");
		    query.setString(3, null);
		    query.setString(4, null);
		    query.setString(5, null);
		    query.setString(6, null);
		    query.addBatch();
		    debug("Record for PTR added successfully.");
		    
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
	
	private boolean insertNewRecord(final String webcode, final String name, 
			final String comp1, final String comp2, final String comp3, final String comp4) {
		// should make this an array of components...
		
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
						
			String insertSQL = "INSERT INTO ADMIN (WEBCODE, NAME, "
					+ "COMP_1, COMP_2, COMP_3, COMP_4) "
					+ "VALUES (?,?,?,?,?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
		    // new site
		    query.setString(1, webcode);
		    query.setString(2, name);
		    query.setString(3, comp1);
		    query.setString(4, comp2);
		    query.setString(5, comp3);
		    query.setString(6, comp4);		    
		    query.addBatch();
		    debug("New record " + name + " for " + webcode + " added successfully.");
			
			
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
}

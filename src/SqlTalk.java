import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class SqlTalk {
	private boolean DEBUG = true;
	private SquawkView squawkView;
	private static String DEFAULT_DB = "squawk.db";
	
	private static String ADMIN_TABLE = "ADMIN";
	private static String DEVICE_TABLE = "DEVICES";
	
	private Connection sqlConnection = null;
	private Statement sqlStatement = null;
	
	SqlTalk(SquawkView squawkView) {	
		this.squawkView = squawkView;
		
		// yar constuctor
		// base this on returning a record in a table of a particular device type.
		// minimal connections and connection time to the db as a matter of design
		// gather, verify and then sql a record
		
		// all static, default vars should be stored in an admin db
		// point is to allow user-updated urls, stylesheets, etc.
		// separate function to be able to add a new device, add/update website url
		//		add/update stylesheet url

		
		// need to include a button and method that wraps the device in site specific html so it
		// looks like what it will on the actual site.
		
		//TODO
		// enable a listing of devices saved to the DB for selection, display, edit, save, export, etc
		// allow a given template file to load into browser and get parsed into the squawkBox view form fields
		// allow for a change in style sheets?
		// JFaceDataBinding for better MVC in this app
		
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
		
	}
	
	protected void saveDeviceRecord(String webcode, String devicetype, String deviceHtml) {
		// deal with errors here
		// need webdcode and devicetype
		if (webcode == null || webcode == "") {
			debug("Save device webcode is null or empty.");
			return;
		}
		if (devicetype == null || devicetype == "") {
			debug("Save device type is null or empty.");
			return;
		}
		// db design allows for empty html, in case of reserving a record for future device
		if (deviceHtml == null || deviceHtml == "") {
			debug("Save device html is null or empty.");
			return;
		}
		
		if (Utilities.isValidWebcode(webcode)) {
			if (Utilities.isValidDevicetype(devicetype)) {
				insertDeviceRecord(webcode, devicetype, deviceHtml);
				Utilities.debugDeviceDB(DEFAULT_DB);
			}			
		}
		else {
			debug("Device save webcode and/or devicetype invalid");
		}
		
		debug("Device " + devicetype + " record for " + webcode + " saved in DB.");
	}
	
/************************************************************
* 
* 		// internal methods 
* 
************************************************************/
	protected void debug(String message) {
		if (DEBUG) squawkView.updateConsole(message);
	}
	private boolean installAdminTable() {
		// first time use etc, get plaintext file?
		// device types and website code (MM, DR, etc), website urls, stylesheet urls
		
		// is it already created?
		createAdminTable();
		// is already populated
		populateAdminTable();		
		return true;
	}
	
	private boolean installDeviceTable() {
		
		createDeviceTable();
		return true;
	}

	/*
	protected boolean insertWebsiteUrl(String websiteUrl) {
		// all the if's are for reporting to the user any errors encountered
		if (checkString(websiteUrl)) {
			if (isValidUrl(websiteUrl)) { 				
				if (checkWebsiteUp(websiteUrl)) {
					// ehm...
					return true;
				}
				// website not reachable
				System.out.format("ERROR: website not reachable: ", websiteUrl);
				return false;
			}
			// not a valid url syntax
			System.out.format("ERROR: syntax error for url: ", websiteUrl);
			return false;
		}
		// not a string
		System.out.format("ERROR: website url not a string.");
		return false;
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
			
			// testing state, drop if exists
			sqlStatement.executeUpdate("DROP TABLE IF EXISTS ADMIN;");
			
			// create table first - an entry of ID has a WEB_CODE and other deets
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
			
			//TODO
			// testing state, drop if exists --- LOOK OUT
			sqlStatement.executeUpdate("DROP TABLE IF EXISTS " + DEVICE_TABLE + ";");
			
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
	
	private boolean insertDeviceRecord(String webcode, String devicetype, String devicehtml) {
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

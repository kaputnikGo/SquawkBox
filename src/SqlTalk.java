import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class SqlTalk {
	private boolean DEBUG = true;
	private SquawkView squawkView;
	
	private static final String DEFAULT_DB = "squawk.db";	
	private static final String ADMIN_TABLE = "ADMIN"; // not in use yet
	private static final String TEMPLATE_LIST_TABLE = "TEMPLATE_LIST"; // not in use yet
	private static final String TEMPLATES_TABLE = "TEMPLATES";
	
	private Connection sqlConnection = null;
	private Statement sqlStatement = null;
	
	SqlTalk(final SquawkView squawkView) {	
		this.squawkView = squawkView;	
		// yar constuctor
	}
	
	public String getDefaultDB() {
		return DEFAULT_DB;
	}
	/*
	public String getAdminTable() {
		return ADMIN_TABLE;
	}
	*/
	/*
	public String getTemplateTable() {
		return TEMPLATE_LIST_TABLE;
	}
	*/
	public String getTemplatesTable() {
		return TEMPLATES_TABLE;
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
		debug(Utilities.debugCountTableDB(DEFAULT_DB, TEMPLATES_TABLE));
	}
	
	protected void addNewTemplate(final String webcode, final String name, final String templateHtml) {		
		//TODO
		// expecting a fully constructed template with correct <div> id's for templates
		// as a single html file

		if (!Utilities.checkString(webcode)) {
			debug("Add New Template webcode is null or empty.");
			return;
		}
		if (!Utilities.checkString(name)) {
			debug("Add New Template name is null or empty.");
			return;
		}
		// db design allows for empty html, in case of reserving a record for future device
		if (!Utilities.checkString(templateHtml)) {
			debug("Add New Template templateHtml is null or empty.");
			return;
		}
		// check if device table exists
		String check = Utilities.checkTableExistsOK(DEFAULT_DB, TEMPLATES_TABLE);
		if (check.equals("OK")) {
			// skip it all.
			debug("Device table exists, can write.");
			if (Utilities.isValidWebcode(webcode)) {
				insertNewTemplate(webcode, name, templateHtml);
				debug("Template " + name + " record for " + webcode + " saved in DB.");
			}
			else {
				debug("Template webcode invalid");
			}
		}
		else {
			debug("Check for table result: " + check);
		}		
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
		String check = Utilities.checkTableExistsOK(DEFAULT_DB, TEMPLATES_TABLE);
		if (check.equals("OK")) {
			// skip it all.
			debug("Template table already exists.");
			return true;
		}
		else {
			debug("check table exists returned: " + check);
			createTemplatesTable();
			populateTemplatesTable();		
			return true;
		}
	}

	
/************************************************************
* 
* 		// database methods 
* 
***********************************************************/	
	private boolean createTemplatesTable() {			
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
			String sqlString = "CREATE TABLE IF NOT EXISTS " + TEMPLATES_TABLE +
					"(ID INTEGER PRIMARY KEY	AUTOINCREMENT," +
					"WEBCODE			TEXT	NOT NULL," +
					"NAME				TEXT	NOT NULL," +					
					"HTML				TEXT)";
					
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
		        	debug("Database " + DEFAULT_DB + " and Table " + TEMPLATES_TABLE + " created successfully.");
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
	
	private boolean populateTemplatesTable() {
		debug("populateTemplatesTable called.");
		// need to check if this is first time install,
		// ie has not already run.
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
			String insertSQL = "INSERT INTO " + TEMPLATES_TABLE + " (WEBCODE, NAME, HTML) "
					+ "VALUES (?,?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
		    query.setString(1, "MM");
		    query.setString(2, "premium");
		    query.setString(3, null);
		    query.addBatch();
		    debug("Record for MM batched.");
		    
		    // 
		    query.setString(1, "ELH");
		    query.setString(2, "ebulletin");
		    query.setString(3, null);		    
		    query.addBatch();
		    debug("Record for ELH batched.");
		    
		    //
		    query.setString(1, "HSH");
		    query.setString(2, "bulletin");
		    query.setString(3, null);
		    query.addBatch();
		    debug("Record for HSH batched.");
		    
		    //
		    query.setString(1, "PTR");
		    query.setString(2, "generic");
		    query.setString(3, null);
		    query.addBatch();
		    debug("Record for PTR batched.");
		    
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
		        	debug("Database " + DEFAULT_DB + " and Table " + TEMPLATES_TABLE + " connection closed.");
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

	
	
	private boolean insertNewTemplate(final String webcode, final String name, final String templateHtml) {
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
						
			String insertSQL = "INSERT INTO " + TEMPLATES_TABLE + " (WEBCODE, NAME, HTML)"
					+ "VALUES (?,?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
		    // new site
		    query.setString(1, webcode);
		    query.setString(2, name);
		    query.setString(3, templateHtml);    
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
		        	debug("Database " + DEFAULT_DB + " and Table " + TEMPLATES_TABLE + " connection closed.");
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

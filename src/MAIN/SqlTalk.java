package MAIN;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import UI.SquawkView;


public class SqlTalk {
	private boolean DEBUG = true;
	private SquawkView squawkView;
	
	private static final String DEFAULT_DB = "squawk.db";	
	private static final String ADMIN_TABLE = "ADMIN";
	private static final String COMPONENT_TABLE = "COMPONENTS";
	private static final String TEMPLATES_TABLE = "TEMPLATES";
	//private static final String TEMPLATE_TAGS = "TEMPLATE_TAGS";
	
	
	// this list should match the actual div id name
	/*
	private String[] FORM_FIELD_LABEL_LIST = new String[] {
		"title", "templatePreHeader", "templatePara1", "templateSignoff", "templateFooter",
		"templateLogo", "templateDate", "templateHeading", "templateAuthor", "templateIntro"
	};
	private String[] FORM_FIELD_LIST = new String[] {
		"userTitle", "userPreheader", "userPara1", "userSignoff", "userFooter", 
		"userBannerLogo", "userDate", "userHeading", "userAuthor", "userIntro"
	};
	*/
	
	
	private Map<String, String> FORM_FIELDS;
	
	private Connection sqlConnection = null;
	private Statement sqlStatement = null;
	
	public SqlTalk(final SquawkView squawkView) {	
		this.squawkView = squawkView;	
		// yar constuctor
	}
	
	public String getDefaultDB() {
		return DEFAULT_DB;
	}	
	public String getAdminTable() {
		return ADMIN_TABLE;
	}	
	public String getTemplatesTable() {
		return TEMPLATES_TABLE;
	}
	 public String getComponentTable() {
		return COMPONENT_TABLE;
	}
	 
	/*
	public int getNumFormFields() {
		return FORM_FIELDS.size();		
	}
	
	public String[] getFormFieldList() {
		return FORM_FIELD_LIST;
	}

	public String[] getFormFieldLabelList() {
		return FORM_FIELD_LABEL_LIST;
	}
	*/
	public Map<String, String> getFormFields() {
		// has a label and a value (Title : title)
		// form-field-label-list : form-field-list
		return FORM_FIELDS;
	}
	
	
/************************************************************
* 
* 		// public methods 
* 
************************************************************/		
	public Map<String, String> initForDisplays() {
		//TODO create a pool of fields of default or max size
		FORM_FIELDS = new HashMap<String, String>();		
		//FORM_FIELDS.put("default label", "default field");
		for (int i = 0; i < 10; i++) {
			FORM_FIELDS.put("default " + i, "default field");
		}
		return FORM_FIELDS;
	}
	
	public void installDB() {
		installAdminTable();		
		installTemplatesTable();
		installComponentsTable();
		// debug routines
		//debug(Utilities.debugCountTableDB(DEFAULT_DB, TEMPLATES_TABLE));
	}
	
	public String[] getTemplateList(String webcode) {
		// from a given webcode return a string array of its templates
		// form the db list in admin table, if none, or error, return "not available"
		//TODO
		String[] stringArray = new String[1];
		if (!Utilities.isValidWebcode(webcode)) {
			// not the best here...
			stringArray[0] = "n/a";
			return stringArray;
		}	
		else {
			// convert arraylist to string[]			
			List<String> list = getTemplateListByWebcode(webcode);
			stringArray = list.toArray(new String[list.size()]);
			return stringArray;
		}
	}
	
	public String[] getComponentList() {
		String[] stringArray = new String[1];
		List<String> list = getComponentListArray();
		stringArray = list.toArray(new String[list.size()]);
		return stringArray;
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
* 		// init methods 
* 
************************************************************/	
	
	private boolean installAdminTable() {
		String check = Utilities.checkTableExistsOK(DEFAULT_DB, ADMIN_TABLE);
		if (check.equals("OK")) {
			// skip it all.
			debug("Admin table already exists.");
			return true;
		}
		else {
			debug("check table exists returned: " + check);
			createAdminTable();
			populateAdminTable();	
			return true;
		}
	}
	
	private boolean installTemplatesTable() {
		String check = Utilities.checkTableExistsOK(DEFAULT_DB, TEMPLATES_TABLE);
		if (check.equals("OK")) {
			// skip it all.
			debug("Template table already exists.");
			return true;
		}
		else {
			debug("check table exists returned: " + check);
			createTemplatesTable();
			//TODO
			// this needs to get templates from the EMAIL folders
			// and save to the DB so user can edit etc.
			populateTemplatesTable();		
			return true;
		}
	}
	
	private boolean installComponentsTable() {
		String check = Utilities.checkTableExistsOK(DEFAULT_DB, COMPONENT_TABLE);
		if (check.equals("OK")) {
			// skip it all.
			debug("Component table already exists.");
			return true;
		}
		else {
			debug("check table exists returned: " + check);
			createComponentTable();
			//TODO
			// this needs to get templates from the EMAIL folders
			// and save to the DB so user can edit etc.
			populateComponentTable();		
			return true;
		}
	}
	
/************************************************************
* 
* 		// database methods 
* 
***********************************************************/	
	
/*	admin database table	*/
	
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
					"TEMPLATE_NAME		TEXT	NOT NULL)";
					
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
		// this uses a default list of templates currently
		// available in templates/EMAIL/
		// each one should have its own webcode folder (MM, DR etc)
		// the user can add to the list later.
		//TODO		
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
			String insertSQL = "INSERT INTO " + ADMIN_TABLE + " (WEBCODE, TEMPLATE_NAME) "
					+ "VALUES (?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
		    
		    query.setString(1, "DR");
		    query.setString(2, "endo");	    
		    query.addBatch();		    
		    //
		    query.setString(1, "ELH");
		    query.setString(2, "bulletin");	    
		    query.addBatch();	    
		    //
		    query.setString(1, "ESKY");
		    query.setString(2, "escapologist");	    
		    query.addBatch();	    
		    //
		    query.setString(1, "HSH");
		    query.setString(2, "bulletin");
		    query.addBatch();	    
		    //
		    query.setString(1, "ILA");
		    query.setString(2, "postcards");	    
		    query.addBatch();	    
		    //
			query.setString(1, "MM");
		    query.setString(2, "premium");
		    query.addBatch();	    
		    // 
		    query.setString(1, "PTR");
		    query.setString(2, "generic");
		    query.addBatch();
		    
		    int[] updateCounts = query.executeBatch();
		    if (updateCounts != null) {		    	
		    	debug("Install admin into DB batch count: " + updateCounts.length);		    	
		    }
		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    
		    if (query != null) {
		    	query.close();
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

/*	templates database table	*/	
	
	private boolean createTemplatesTable() {			
		// can reserve a given webcode and name without a template html yet.
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
		// TODO
		// this needs to load from templates/EMAIL folder?
		// assume user cannot create new templates and save in the app?
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
		    debug("prepStatement batch start...");
		    
		    String[] str = new String[7];
		    str[0] = getTemplateHtml("DR", "endo");
		    str[1] = getTemplateHtml("ELH", "bulletin");
		    str[2] = getTemplateHtml("ESKY", "escapologist");
		    str[3] = getTemplateHtml("HSH", "bulletin");
		    str[4] = getTemplateHtml("ILA", "postcards");
		    str[5] = getTemplateHtml("MM", "premium");
		    str[6] = getTemplateHtml("PTR", "generic");
		    
			query.setString(1, "DR");
		    query.setString(2, "endo");
		    query.setString(3, str[0]);
		    query.addBatch();
		    //
		    query.setString(1, "ELH");
		    query.setString(2, "bulletin");
		    query.setString(3, str[1]);		    
		    query.addBatch();		    
		    //
		    query.setString(1, "ESKY");
		    query.setString(2, "escapologist");
		    query.setString(3, str[2]);		    
		    query.addBatch();	    
		    //
		    query.setString(1, "HSH");
		    query.setString(2, "bulletin");
		    query.setString(3, str[3]);
		    query.addBatch();		    
		    //
		    query.setString(1, "ILA");
		    query.setString(2, "postcards");
		    query.setString(3, str[4]);
		    query.addBatch();	    
		    //
			query.setString(1, "MM");
		    query.setString(2, "premium");
		    query.setString(3, str[5]);
		    query.addBatch();		    
		    // 
		    query.setString(1, "PTR");
		    query.setString(2, "generic");
		    query.setString(3, str[6]);
		    query.addBatch();
		    
		    int[] updateCounts = query.executeBatch();
		    if (updateCounts != null) {		    	
		    	debug("Install templates into DB batch count: " + updateCounts.length);		    	
		    }
		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    
		    if (query != null) {
		    	query.close();
		    	str = null;
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
		    	query.close();
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
	
	private List<String> getTemplateListByWebcode(final String webcode) {
		// return all the records of a given webcode
		//TODO
		// hardcoded for this method, make generic
		List<String> templateList = new ArrayList<String>();
		
		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
		}
		try {
			if (sqlConnection != null) {
				debug("sqlConnection made...");
			}			
			sqlConnection.setAutoCommit(false);

			String selectSQL = "SELECT TEMPLATE_NAME FROM " + ADMIN_TABLE + " WHERE WEBCODE = ?";					
		    PreparedStatement query = sqlConnection.prepareStatement(selectSQL);
		    query.setString(1, webcode);
		    
		    ResultSet rs = query.executeQuery();
		    while (rs.next()) {
		    	templateList.add(rs.getString(1));
		    }		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    rs.close();
		    query.close();
	
		} catch (SQLException e) {
			debug("Get templateList for " + webcode + " SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Get templateList for " + webcode + " SQL connection closed.");		            
		        }
			}
		    catch(SQLException e) {
		    	debug("Get templateList for " + webcode + " SQL connection close failure.");
		        System.err.println(e);		        
		    }
		}		
		return templateList;
	}
	
	
/*	components database table	*/
	
	private boolean createComponentTable() {
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
			String sqlString = "CREATE TABLE IF NOT EXISTS " + COMPONENT_TABLE +
					"(ID INTEGER PRIMARY KEY	AUTOINCREMENT," +
					"COMPONENT_NAME			TEXT	NOT NULL," +
					"COMPONENT_HTML			TEXT	NOT NULL)";
					
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
		        	debug("Database " + DEFAULT_DB + " and Table " + COMPONENT_TABLE + " created successfully.");
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
	
	private boolean populateComponentTable() {
		debug("populateComponentTable called.");
		// TODO
		// this needs to load from templates/EMAIL folder?
		// assume user cannot create new templates and save in the app?
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
			String insertSQL = "INSERT INTO " + COMPONENT_TABLE + " (COMPONENT_NAME, COMPONENT_HTML) "
					+ "VALUES (?,?)";
			PreparedStatement query = sqlConnection.prepareStatement(insertSQL);
		    debug("prepStatement batch start...");
		    
		    //String[] str = new String[7];
		    String path = "templates/EMAIL/components";
		    List<String> fileNames = Utilities.getComponentFileNameList(path);
		    if (fileNames == null) {
		    	debug("load component resource list error for: " + path);
		    	return false;
		    }
		  
		    if (fileNames.size() != Utilities.fileNameHtml.size()) {
		    	debug("file name list count does not match file html count");
		    }
		    
		    for (int i = 0; i < fileNames.size(); i++) {
			    query.setString(1, fileNames.get(i));
			    query.setString(2, Utilities.fileNameHtml.get(i));
			    query.addBatch();
		    }
		    
		    int[] updateCounts = query.executeBatch();
		    if (updateCounts != null) {		    	
		    	debug("Install Components batch count: " + updateCounts.length);		    	
		    }
		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    
		    if (query != null) {
		    	query.close();
		    	fileNames = null;
		    }

		} catch (SQLException e) {
			debug("SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Database " + DEFAULT_DB + " and Table " + COMPONENT_TABLE + " connection closed.");
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
	
	private List<String> getComponentListArray() {
		// return all the records of a given webcode
		//TODO
		// hardcoded for this method, make generic
		List<String> componentList = new ArrayList<String>();
		
		sqlConnection = null;
		try {
			sqlConnection = Utilities.getConnection(DEFAULT_DB);
		} catch (Exception e) {
			debug("getConnection failure.");
			e.printStackTrace();
		}
		try {
			if (sqlConnection != null) {
				debug("sqlConnection made...");
			}			
			sqlConnection.setAutoCommit(false);

			String selectSQL = "SELECT COMPONENT_NAME FROM " + COMPONENT_TABLE;					
		    PreparedStatement query = sqlConnection.prepareStatement(selectSQL);		    
		    ResultSet rs = query.executeQuery();
		    
		    while (rs.next()) {
		    	componentList.add(rs.getString(1));
		    }		    
		    sqlConnection.commit();
		    sqlConnection.setAutoCommit(true);
		    rs.close();
		    query.close();
	
		} catch (SQLException e) {
			debug("Get componentList SQL connection open failure.");
			e.printStackTrace();
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	debug("Get componentList SQL connection closed.");		            
		        }
			}
		    catch(SQLException e) {
		    	debug("Get componentList for SQL connection close failure.");
		        System.err.println(e);		        
		    }
		}		
		return componentList;
	}
	
/*******************************/
	
	// utilities
	
/*******************************/
	
	private String getTemplateHtml(final String webcode, final String name) {
		// internal method called from installing template DB for first time
		// return the html string from templates/EMAIL/webcode/name.html
		// this can return null from utilities
		return Utilities.getTemplateHtml(webcode, name);
	}
	
	
	private void debug(final String message) {
		if (DEBUG) squawkView.updateConsole(message);
	}
}

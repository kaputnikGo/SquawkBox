package MAIN;
import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jsoup.safety.Whitelist;

public class Utilities {
	// db and general utilities
	
	public static final int SHELL_WIDTH = 1400;
	public static final int SHELL_HEIGHT = 1000;
	public static final int SHELL_PADDING = 120;
	public static final int DEFAULT_FIELD_MAX = 4;
	public static final int FORM_FIELD_HEIGHT = 60;
	public static final int FORM_LABEL_WIDTH = 120;
	
	public static final String[] WEBCODE_LIST = new String[] {
		"MM", "DR", "PTR", "ILA", "ESKY", "HSH", "ELH"};
	
	//TODO
	// these two need a resource folder scanner	
	public static String[] COMPONENT_LIST;
	public static List<String> fileNameHtml;
	
	// to be replaced with a DB entry
	// still called at init so needs a nominal entry
	public static String[] TEMPLATE_LIST = new String[] {
		"default", "premium", "free", "endo"};
	
	// grid blue thing
	public static final String DESIGN_GRID_ON = "var css = 'table, th, td {border: 1px solid #99CCFF;}' ,"
			+ "head = document.head || document.getElementByTagName('head')[0],"
			+ "style = document.createElement('style');"
			+ "style.type = 'text/css';"
			+ "if (style.styleSheet) {"
			+ "style.styleSheet.cssText = css;"
			+ "} else {"
			+ "style.appendChild(document.createTextNode(css));"
			+ "}"
			+ "head.appendChild(style);";
	
	public static final String DESIGN_GRID_OFF = "var css = 'table, th, td {border: 0;}' ,"
			+ "head = document.head || document.getElementByTagName('head')[0],"
			+ "style = document.createElement('style');"
			+ "style.type = 'text/css';"
			+ "if (style.styleSheet) {"
			+ "style.styleSheet.cssText = css;"
			+ "} else {"
			+ "style.appendChild(document.createTextNode(css));"
			+ "}"
			+ "head.appendChild(style);";
	
	// wobbly js via swt browser string...
	// best try some jsoup
	public static final String SELECT_HTML = "if(window.getSelection()){return window.getSelection().toString();}";
	public static final String SELECT_HTML_ELEMENT = "if(window.getSelection()){return window.getSelection().parentElement();}";
	
	public static final String DOCTYPE_DECLARE_EMAIL = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
	public static final String DOCTYPE_DECLARE_HTML3 = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">";
	public static final String DOCTYPE_DECLARE_HTML5 = "<!DOCTYPE html>";
	
	public static List<String> getComponentFileNameList(String path) {
		// returns list of names and adds their contents to the fileNameHtml List
		List<String> fileNameList = new ArrayList<String>();
		fileNameHtml = new ArrayList<String>();
		File directory = null;
		
		URL resource = Utilities.class.getClassLoader().getResource(path);
		if (resource == null) {
			System.out.println("Resource null for path: " + path);
			return null;
		}

		try {
			directory = new File(resource.toURI());			
		}
		catch (URISyntaxException e ) {
			System.out.println("Not a valid URI: " + resource);
		}
		if (directory != null && directory.exists()) {
			String[] fileNames = directory.list();
			for (String str : fileNames) {
				fileNameList.add(str);
				fileNameHtml.add(returnComponentFileHtml(str));
			}	
			return fileNameList;
		}
		else {
			System.out.println("Directory null exists error for: " + path);
			return null;
		}
	}
	//TODO clunky
	public static String returnComponentFileHtml(String filename) {
		String path = "templates/EMAIL/components/" +  filename;		
		String fileHtml = "";
		
		try {
		    InputStream inputStream = Utilities.class.getClassLoader().getResourceAsStream(path);
		    if (inputStream == null) {
		    	System.out.println("inputStream is null.");
		    	fileHtml = null;
		    }
		    else {
			    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        fileHtml += inputLine;
			    }
			    in.close();	
		    }
		}
		catch (IOException e) {
			System.out.println("IO error accessing path: " + path);
			return null;
		}		
		return fileHtml;
		
	}
	
	public static List<String> getComponentFileHtml(String path) {
		// need to get the filenames and add to seek
		List<String> fileHtml = new ArrayList<String>();
		try {
		    InputStream inputStream = Utilities.class.getClassLoader().getResourceAsStream(path);
		    if (inputStream == null) {
		    	System.out.println("inputStream is null.");
		    	fileHtml = null;
		    }
		    else {
			    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        fileHtml.add(inputLine);
			    }
			    in.close();	
		    }
		}
		catch (IOException e) {
			System.out.println("IO error accessing path: " + path);
			return null;
		}		
		return fileHtml;
	}
	
	
	public static boolean checkString (final String candidate) {
		return candidate != null && !candidate.isEmpty();
	}
	
	public static boolean checkMapEmpty(final Map< ?, ? > map) {
		return map == null || map.isEmpty();
	}
	
	@SuppressWarnings("unused")
	public static boolean isValidUrl(final String candidate) {
		// this returns only if it appears to be a valid url string
		// suppression is cos we only test if can get a URI, not do anything with it
		try {
			URI uri = new URI(candidate);
			return true;
		}
		catch (URISyntaxException e) {
			return false;
		}
	}
	
	public static boolean checkWebsiteUp(final String candidate) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			// may also need this:
			//HttpURLConnection.setInstanceFollowRedirects(false);
			HttpURLConnection urlConn = (HttpURLConnection) new URL(candidate).openConnection();
			urlConn.setRequestMethod("HEAD");
			return (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	public static Connection getConnection(final String dbName) throws Exception {
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection("jdbc:sqlite:" + dbName);
	}

	public static boolean isValidWebcode(final String candidate) {
		if (candidate == null) 
			return false;
	 
		for (String element : WEBCODE_LIST) {
			if (element.equals(candidate)) 
				return true;
		}
		return false;	
	}
	
	public static String getTemplateHtml(final String webcode, final String name) {
		String htmlString = "";
		if (!isValidWebcode(webcode)) {
			System.out.println("ERROR: getTemplateHtml bad webcode - " + webcode);
			htmlString = null;
			return htmlString;
		}
		
		try {
			// construct the path string
			String seek = "templates/EMAIL/" + webcode + "/" + name + ".html";
			System.out.println("template string: " + seek);			
			// need to check for file extension (html or htm)
		    InputStream inputStream = Utilities.class.getClassLoader().getResourceAsStream(seek);
		    if (inputStream == null) {
		    	System.out.println("inputStream is null.");
		    	htmlString = null;
		    }
		    else {
			    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
				String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        htmlString += inputLine;
			    }		 
			    in.close();	
		    }
		}
		catch (IOException e) {
		    e.printStackTrace();
		    System.out.println("ERROR: getTemplateHtml IO exception for: " + webcode + ", " + name);
		    htmlString = null;
		}
		return htmlString;
	}
	
	public static String unicodeToEntities(String str) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			// non-ASCII char
			//if ((ch >= 0x2013) && (ch <= 0x2026)) {			
			if (!((ch >= 0x0020) && (ch <= 0x007e))) {
				strBuf.append("&#x");
				String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);
				if (hex.length() == 2)
					strBuf.append("00");
				strBuf.append(hex.toUpperCase(Locale.ENGLISH));
				strBuf.append(";");
			}
			else {
				// ASCII char
				strBuf.append(ch);
			}
		}
		return new String(strBuf);
	}
	
	/*
	public static String replaceWithHtmlEntity(String inputString) { 
		//TODO		
		// non-functioning now that ide is in utf-8	
		//  this is to catch the use of entities in user added content	
		// 	(&amp; &lsquo; &rsquo; &ldquo; &rdquo &mdash; &ndash; &copy; etc)
		//  avoid <!-- --> comments !				
		// some way to add this to the database so user can add new.		
		inputString = inputString.replaceAll("'", "&apos;");
		inputString = inputString.replaceAll("�", "&lsquo;");
		inputString = inputString.replaceAll("�", "&rsquo;");
		inputString = inputString.replaceAll("�", "&ldquo;");
		inputString = inputString.replaceAll("�", "&rdquo;");
		inputString = inputString.replaceAll("�", "&hellip;");
		inputString = inputString.replaceAll("�", "&ndash;");
		inputString = inputString.replaceAll("�", "&mdash;");
		inputString = inputString.replaceAll("�", "&copy;");
		inputString = inputString.replaceAll("�", "&bull;");			
		return inputString;		
	}
	*/
	
	public static Whitelist getDocumentWhitelist() {		
		Whitelist whitelist = new Whitelist();
		whitelist = Whitelist.relaxed();		
		// example use:
		//String cleaned = Jsoup.clean(browser.getText(), whitelist);		
		return whitelist;
	}
	
	public static String stripFormatting(String inputString) {
		inputString = inputString.replaceAll("\n", "");
		inputString = inputString.replaceAll("\t", "");
		inputString = inputString.replaceAll("\r", ""); 
		
		return inputString;
	}
	
/************************************************************
* 
* 		// debug methods 
* 
*************************************************************/
	public static void openInNotepad(final String filename) {		
		// test opening the notepad program
		if (!Desktop.isDesktopSupported()) {
			System.out.println("Desktop run commands not supported.");
			return;
		}
		Desktop desktop = Desktop.getDesktop();
		if (!desktop.isSupported(Desktop.Action.EDIT)) {
			System.out.println("Edit command not supported on this Desktop.");
		}
		try {
			// need an actual file
			desktop.edit(new File(filename));
		}
		catch (IOException ex) {
			System.out.println("File edit exception.");
			ex.printStackTrace();
		}
	}
	
	public static String checkTableExistsOK(final String dbname, final String tablename) {
		// returns OK or an error message string for console
		if (!checkString(dbname)) return ("dump DB name not valid.");
		if (!checkString(tablename)) return ("dump table name not valid.");
		
		String result = "start";
		Connection sqlConnection = null;
		
		try {
			sqlConnection = getConnection(dbname);
			DatabaseMetaData dbm;
			
			dbm = sqlConnection.getMetaData();
			ResultSet rs = dbm.getTables(null,  null,  tablename, null);
			if (rs.next()) {
				result = "OK";
			}
			else {
				result = "checkTable unable to find: " + tablename;
			}
		} 
		catch (SQLException e1) {
			e1.printStackTrace();
			result = "checkTables SQL error.";
		} 
		catch (Exception e) {
			e.printStackTrace();
			result = "checkTables connection error.";
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        }
			}
		    catch(SQLException e) {
		        System.err.println(e);
		        result += " + SQL connection close failure.";
		    }
		}
		return result;	
	}
	
	public static String debugDumpTableDB(final String dbname, final String tablename) {
		System.out.println("debugDumpTableDB called for db: " + dbname + ", table: " + tablename);
		if (!checkString(dbname)) return ("dump DB name not valid.");
		if (!checkString(tablename)) return ("dump table name not valid.");
		
		Connection sqlConnection = null;
		Statement sqlStatement = null;
		String outputString = "";
		
		try {
			sqlConnection = getConnection(dbname);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return ("getConnection failure.");
		}
		
		try {
			sqlConnection.setAutoCommit(false);
			sqlStatement = null;
			sqlStatement = sqlConnection.createStatement();
			ResultSet rs = sqlStatement.executeQuery("SELECT * FROM " + tablename);
			ResultSetMetaData rsMeta = rs.getMetaData();
			
			StringBuilder stringBuilder = new StringBuilder();
			int numColumns = rsMeta.getColumnCount();
			
			while (rs.next()) {
				stringBuilder.append("------------------------<br />");
				for (int i = 1; i <= numColumns; i++) {						
					stringBuilder.append(rsMeta.getColumnName(i) + " = " + rs.getString(rsMeta.getColumnName(i)) + "<br />");
				}
				stringBuilder.append("------------------------<br />");
			}

			outputString = stringBuilder.toString();
			rs.close();
			sqlStatement.close();	
		} catch (SQLException e) {
			e.printStackTrace();
			return ("SQL connection open failure.");
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	return outputString;
		        }
			}
		    catch(SQLException e) {
		        System.err.println(e);
		        return ("SQL connection close failure.");
		    }
		}
		return ("DebugAdminDB failure.");
	}
	
	public static String debugCountTableDB(final String dbname, final String tablename) {
		if (!checkString(dbname)) return ("dump DB name not valid.");
		if (!checkString(tablename)) return ("dump table name not valid.");
		
		Connection sqlConnection = null;
		Statement sqlStatement = null;
		int rowCount = 0;
		
		
		try {
			sqlConnection = getConnection(dbname);
		} catch (Exception e) {
			e.printStackTrace();
			return ("getConnection failure.");
		}
		try {
			sqlConnection.setAutoCommit(false);
			sqlStatement = null;
			sqlStatement = sqlConnection.createStatement();
			ResultSet rs = sqlStatement.executeQuery("SELECT * FROM " + tablename);
			rs = sqlStatement.executeQuery("SELECT COUNT(*) FROM " + tablename);
			
		    // get the number of rows from the result set
		    rs.next();
		    rowCount = rs.getInt(1);
		    rs.close();
		    sqlStatement.close();
		    sqlConnection.setAutoCommit(true);
		    //return ("row count: " + rowCount);
		} 
		catch (SQLException e) {
			e.printStackTrace();
			return ("SQL connection open failure.");
		}
		finally {
			try {
		        if(sqlConnection != null) {
		        	sqlConnection.close();
		        	return ("DebugCountTableDB of table " + tablename + " closed with count: " + rowCount);
		        }
			}
		    catch(SQLException e) {
		        // connection close failed.
		        System.err.println(e);
		        return ("SQL connection close failure.");
		    }
		}
		return ("DebugCountTableDB failure.");
		
	}
	
	public static void dumpStringList(List<String> list) {
		if (list.isEmpty()) 
			System.out.println("list empty");
		else {
			System.out.println("Start list dump: ");
			for (String element : list) {
				System.out.println(element);
			}
		}		
	}	
}

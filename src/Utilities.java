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



public class Utilities {
	
	public static final String[] WEBCODE_LIST = new String[] {
		"MM", "DR", "PTR", "ILA", "ESKY", "HSH", "ELH"};
	// need to verbose these.
	
	// to be replaced with a DB entry
	// still called at init so needs a nominal entry
	public static String[] TEMPLATE_LIST = new String[] {
		"default", "premium", "free", "endo"};
	
	public static boolean checkString (final String candidate) {
		return candidate != null && !candidate.isEmpty();
	}
	
	@SuppressWarnings("unused")
	public static boolean isValidUrl(final String candidate) {
		// this returns only if it appears to be a valid url string
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
	
		System.out.println("isvalidWeb: " + candidate);		 
		for (String element : WEBCODE_LIST) {
			if (element.equals(candidate)) 
				return true;
		}
		return false;	
	}
	
	public static String getTemplateHtml(final String webcode, final String name) {
		//TODO
		String htmlString = "";
		if (!isValidWebcode(webcode)) {
			System.out.println("ERROR: getTemplateHtml bad webcode - " + webcode);
			htmlString = null;
			return htmlString;
		}
		
		try {
			// construct the path string
			String seek = "templates/EMAIL/" + webcode + "/" + name + ".html";
			//TODO
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
}

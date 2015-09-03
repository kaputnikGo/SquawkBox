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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;



public class Utilities {
	
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
		// need to get an enum of valid webcodes
		System.out.println("isvalidWeb: " + candidate);
		if (candidate == null) 
			return false;
		else { 
			return (candidate.equals("DR") || candidate.equals("ESKY") 
				|| candidate.equals("ILA") || candidate.equals("MM"));
		}
	}
	
	public static boolean isValidDevicetype(final String candidate) {
		// need to get an enum of valid devicetype
		System.out.println("isvalidDevice: " + candidate);
		if (candidate == null) 
			return false;
		else {
			return (candidate.equals("EOA") || candidate.equals("IF") 
				|| candidate.equals("PU") || candidate.equals("SF"));	
		}
	}
	
	public static String getTemplate(final String webcode, final String devicetype) {
		if (Utilities.isValidWebcode(webcode) == false) {
			return ("ERROR: bad webcode - " + webcode);
		}
		if (Utilities.isValidDevicetype(devicetype) == false) {
			return ("ERROR: bad devicetype - " + devicetype);
		}
		
		String htmlString = "";
		try {
			// construct the path string
			String seek = "templates" + File.separator + webcode + File.separator + "template_" + devicetype + ".html";			
		    InputStream inputStream = Utilities.class.getClassLoader().getResourceAsStream(seek);		    
		    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
			String inputLine;
		 
			// TODO: is reading the first line at start and again at end...
		    while ((inputLine = in.readLine()) != null) {
		        System.out.println(inputLine);
		        htmlString += inputLine;
		    }		 
		    in.close();	
		}
		catch (IOException e) {
		    e.printStackTrace();
		    return ("getTemplate IO exception.");
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
	
	public static void dumpTemplate(final String webcode, final String devicetype) {
		
		if (isValidWebcode(webcode) && isValidDevicetype(devicetype)) {		
			String getHtml = Utilities.getTemplate(webcode, devicetype);
			if (getHtml == null) 
				System.out.println("getHtml template dump is null.");
			else
				System.out.println(getHtml);
		}
		else System.out.println("dump template webcode and or devicetype not valid.");
		
	}
	
	//TODO
	// these debug methods need to be a generic method that gets all from a user table
	public static String debugDumpTableDB(final String dbname, final String tablename) {
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
			/*
			while (rs.next()) {
				stringBuilder.append("------------------------<br />");				
				stringBuilder.append("ID = " + rs.getInt("ID") + "<br />");
				stringBuilder.append(rsMeta.getColumnName(counter) + "=");
				// clearing
				stringBuilder.append("------------------------<br />");
				counter++;
			}
			*/

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
	
	
	public static String debugAdminDB(final String tablename) {
		//print the db to console
		// ID, WEBCODE, WEBURL, DEVICE_1(-4), STYLE_URL_1(-3)
		Connection sqlConnection = null;
		Statement sqlStatement = null;
		String outputString = "";
		
		try {
			sqlConnection = getConnection("squawk.db");
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
			StringBuilder stringBuilder = new StringBuilder();
			
			while (rs.next()) {
				int id = rs.getInt("ID");
				String webcode = rs.getString("WEBCODE");
				String weburl = rs.getString("WEBURL");
				// below can return null
				String device_1 = rs.getString("DEVICE_1");
				String device_2 = rs.getString("DEVICE_2");
				String device_3 = rs.getString("DEVICE_3");
				String device_4 = rs.getString("DEVICE_4");
				String style_url_1 = rs.getString("STYLE_URL_1");
				String style_url_2 = rs.getString("STYLE_URL_2");
				String style_url_3 = rs.getString("STYLE_URL_3");
				// dump to squawkBrowser
				stringBuilder.append("------------------------<br />");				
				stringBuilder.append("ID = " + id + "<br />");				
				stringBuilder.append("WEBCODE = " + webcode + "<br />");
				stringBuilder.append("WEBURL = " + weburl + "<br />");
				
				// this is bad code...
				if (device_1 != null) {
					stringBuilder.append("DEVICE_1 = " + device_1 + "<br />");
				}
				if (device_2 != null) {
					stringBuilder.append("DEVICE_2 = " + device_2 + "<br />");
				}
				if (device_3 != null) {
					stringBuilder.append("DEVICE_3 = " + device_3 + "<br />");
				}				
				if (device_4 != null) {
					stringBuilder.append("DEVICE_4 = " + device_4 + "<br />");
				}
				if (style_url_1 != null) {
					stringBuilder.append("STYLE_URL_1 = " + style_url_1 + "<br />");
				}
				if (style_url_2 != null) {
					stringBuilder.append("STYLE_URL_2 = " + style_url_2 + "<br />");
				}
				if (style_url_3 != null) {
					stringBuilder.append("STYLE_URL_3 = " + style_url_3 + "<br />");
				}				
				// clearing
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
	
	public static String countAdminDB(final String adminDB) {
		Connection sqlConnection = null;
		Statement sqlStatement = null;
		int rowCount = 0;
		
		
		try {
			sqlConnection = getConnection(adminDB);
		} catch (Exception e) {
			e.printStackTrace();
			return ("getConnection failure.");
		}
		try {
			sqlConnection.setAutoCommit(false);
			sqlStatement = null;
			sqlStatement = sqlConnection.createStatement();
			ResultSet rs = sqlStatement.executeQuery("SELECT * FROM ADMIN");
			rs = sqlStatement.executeQuery("SELECT COUNT(*) FROM ADMIN");
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
		        	return ("DebugAdminDB closed with count: " + rowCount);
		        }
			}
		    catch(SQLException e) {
		        // connection close failed.
		        System.err.println(e);
		        return ("SQL connection close failure.");
		    }
		}
		return ("CountAdminDB failure.");
	}
	
	public static String debugDeviceDB(final String deviceDB) {
		//print the db to console
		// ID, WEBCODE, DEVICETYPE, DEVICE_HTML
		Connection sqlConnection = null;
		Statement sqlStatement = null;
		String outputString = "";
		
		try {
			sqlConnection = getConnection(deviceDB);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return ("getConnection failure.");
		}
		
		try {
			sqlConnection.setAutoCommit(false);
			sqlStatement = null;
			sqlStatement = sqlConnection.createStatement();
			ResultSet rs = sqlStatement.executeQuery("SELECT * FROM DEVICES");
			StringBuilder stringBuilder = new StringBuilder();
			
			while (rs.next()) {
				int id = rs.getInt("ID");
				String webcode = rs.getString("WEBCODE");
				String devicetype = rs.getString("DEVICETYPE");
				// below can return null
				String device_html = rs.getString("DEVICE_HTML");
				// dump to squawkBrowser
				stringBuilder.append("------------------------<br />");
				stringBuilder.append("ID = " + id + "<br />");				
				stringBuilder.append("WEBCODE = " + webcode + "<br />");
				stringBuilder.append("DEVICETYPE = " + devicetype + "<br />");
				
				if (device_html != null) 
					stringBuilder.append("DEVICETYPE = " + devicetype + "<br />");
				// clearing
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
		return ("DebugDeviceDB failure.");
	}
	
	public static String debugWrapperDB(final String deviceDB) {
		
		return null;
	}
}

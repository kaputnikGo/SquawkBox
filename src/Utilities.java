import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Utilities {
	
	public static boolean checkString (String candidate) {
		return candidate != null && !candidate.isEmpty();
	}
	
	@SuppressWarnings("unused")
	public static boolean isValidUrl(String candidate) {
		// this returns only if it appears to be a valid url string
		try {
			URI uri = new URI(candidate);
			return true;
		}
		catch (URISyntaxException e) {
			return false;
		}
	}
	
	public static boolean checkWebsiteUp(String candidate) {
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
	
	public static Connection getConnection(String dbName) throws Exception {
		Class.forName("org.sqlite.JDBC");
		return DriverManager.getConnection("jdbc:sqlite:" + dbName);
	}
	
	
/************************************************************
* 
* 		// debug methods 
* 
***********************************************************/
	public static String debugAdminDB(String adminDB) {
		//print the db to console
		// ID, WEBCODE, WEBURL, DEVICE_1(-4), STYLE_URL_1(-3)
		Connection sqlConnection = null;
		Statement sqlStatement = null;
		
		try {
			sqlConnection = getConnection(adminDB);
		} 
		catch (Exception e) {
			e.printStackTrace();
			return ("getConnection failure.");
		}
		
		try {
			sqlConnection.setAutoCommit(false);
			sqlStatement = null;
			sqlStatement = sqlConnection.createStatement();
			ResultSet rs = sqlStatement.executeQuery("SELECT * FROM ADMIN");
			
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
				// print to console
				System.out.println("------------------------");
				System.out.println("ID = " + id);
				System.out.println("WEBCODE = " + webcode);
				System.out.println("WEBURL = " + weburl);
				
				if (device_1 != null) 
					System.out.println("DEVICE_1 = " + device_1);
				else 
					System.out.println("DEVICE_1 = null");
				if (device_2 != null) 
					System.out.println("DEVICE_2 = " + device_2);
				else 
					System.out.println("DEVICE_2 = null");
				if (device_3 != null) 
					System.out.println("DEVICE_3 = " + device_3);
				else 
					System.out.println("DEVICE_3 = null");
				if (device_4 != null) 
					System.out.println("DEVICE_4 = " + device_4);
				else 
					System.out.println("DEVICE_4 = null");
				
				if (style_url_1 != null) 
					System.out.println("STYLE_URL_1 = " + style_url_1);
				else 
					System.out.println("STYLE_URL_1 = null");
				if (style_url_2 != null)
					System.out.println("STYLE_URL_2 = " + style_url_2);
				else 
					System.out.println("STYLE_URL_2 = null");
				if (style_url_3 != null) 
					System.out.println("STYLE_URL_3 = " + style_url_3);
				else 
					System.out.println("STYLE_URL_3 = null");
				// clearing
				System.out.println("------------------------");
			}
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
		        	return ("DebugAdminDB connection closed.");
		        }
			}
		    catch(SQLException e) {
		        // connection close failed.
		        System.err.println(e);
		        return ("SQL connection close failure.");
		    }
		}
		return ("DebugAdminDB failure.");
	}
	
	public static String countAdminDB(String adminDB) {
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
}

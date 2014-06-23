import java.sql.*;
import java.util.ArrayList;

public class MySQLManager {
	private Connection con;
	
	public MySQLManager() {
		
	}
	
	public MySQLManager(String url) throws SQLException {
		con = DriverManager.getConnection(url);
	}
	
	public MySQLManager(String hostname, String port, String username, String password) throws SQLException {
		String url = "jdbc:mysql://" + hostname + ":port" + "?user=" + username + "&password=" + password;
		con = DriverManager.getConnection(url);
	}
	
	public MySQLManager(String url, String username, String password) throws SQLException{
		String connectString = url + "?user=" + username + "&password=" + password;
		con = DriverManager.getConnection(connectString);
	}
	
	public MySQLManager(String url, String username) throws SQLException{
		String connectString = url + "?user=" + username;
		con = DriverManager.getConnection(connectString);
	}
	
	public ArrayList<String[]> query(String query) throws SQLException{
		ResultSet rs;
		Statement stmt = con.createStatement();
		rs = stmt.executeQuery(query);
		ArrayList<String[]> result = getStrings(rs);
		stmt.close();
		return result;
	}
	
	private ArrayList<String[]> getStrings(ResultSet rs) {
		try {
		ResultSetMetaData meta = rs.getMetaData();
		ArrayList<String[]> result = new ArrayList<String[]>();
		
		int j = 0;
			while(rs.next()) {
				result.add(new String[meta.getColumnCount()]);
				for(int i=0; i<meta.getColumnCount() ; i++) {
					result.get(j)[i] = rs.getString(i+1);
				}
				j++;
			}
			rs.close();
			return result;
		} catch(SQLException e) {
			System.err.println("Error reading result set");
			e.printStackTrace();
		}
		System.err.println("returning null");
		return null;
	}

	public int update(String update) throws SQLException {
		Statement stmt = con.createStatement();
		int result = stmt.executeUpdate(update);
		stmt.close();
		return result;
	}
	
	public int getNumCols(String table) throws SQLException {
		Statement stmt = con.createStatement();
		String query = "SELECT * FROM " + table;
		ResultSet rs = stmt.executeQuery(query);
		ResultSetMetaData meta = rs.getMetaData();
		int result = meta.getColumnCount();
		stmt.close();
		rs.close();
		return result;
	}
	
	public void close() throws SQLException {
		con.close();
	}

}

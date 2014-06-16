import java.sql.*;

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
	
	public ResultSet query(String query) throws SQLException{
		ResultSet result;
		Statement stmt = con.createStatement();
		result = stmt.executeQuery(query);
		return result;
	}
	
	public int update(String update) throws SQLException {
		Statement stmt = con.createStatement();
		return stmt.executeUpdate(update);
	}
	
	public int getNumCols(String table) throws SQLException {
		Statement stmt = con.createStatement();
		String query = "SELECT * FROM " + table;
		ResultSet rs = stmt.executeQuery(query);
		ResultSetMetaData meta = rs.getMetaData();
		return meta.getColumnCount();
	}

}

import java.sql.*;
import java.util.ArrayList;
public class DBTester {
	

	public static void main(String[] args) throws ClassNotFoundException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			MySQLManager manager = new MySQLManager("jdbc:mysql://localhost:3306/RestaurantDatabase", "root");
			String query = "SELECT * FROM orders";
			ArrayList<String[]> result = manager.query(query);
			for(int i=0; i<result.size(); i++) {
				for(int j=0; j<result.get(i).length; j++) {
					System.out.print(result.get(i)[j] + " ");
				}
				System.out.println();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}

	}

}

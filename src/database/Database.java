package database;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:mysql://localhost[XAMPP's mysql port]/[database]";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String DELETE_ROW = "DELETE FROM cached WHERE city = ?";
    private static final String INSERT_NEW = "INSERT INTO cached(city, cached_forecast) VALUES(?, ?)";
    private static final String SELECT_ALL = "SELECT * FROM cached";
    private static Connection conn;
    
	public Database() {
	    try {
	    	Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	    	Database.conn = DriverManager.getConnection(URL, USER, PASSWORD);
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}
	
	public int addRow(String city, String forecast) throws SQLException {
		PreparedStatement preparedStatement = Database.conn.prepareStatement(INSERT_NEW);
		preparedStatement.setString(1, city.toLowerCase());
		preparedStatement.setString(2, forecast);
		int count = preparedStatement.executeUpdate();
		
		return count;
	}
	
	public ResultSet getAll() throws SQLException {
		Statement stt = Database.conn.createStatement();
		ResultSet results = stt.executeQuery(SELECT_ALL);
		
		return results;
	}
	
	public void removeData(String city) throws SQLException {
		PreparedStatement preparedStatement = Database.conn.prepareStatement(DELETE_ROW);
		preparedStatement.setString(1, city.toLowerCase());
		preparedStatement.executeUpdate();
	}
}

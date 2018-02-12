package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import util.ReadConfig;

public class DatabaseService {

	private static Connection connection;

	public static Connection getConnection() {

		try {
			if (connection == null || connection.isClosed()) {
				connection = establishConnection();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return connection;
	}

	private static Connection establishConnection() {
		Connection output = null;

		try {

			String user = ReadConfig.get("db.user");
			String pass = ReadConfig.get("db.password");
			String server = ReadConfig.get("db.server");
			Class.forName("com.mysql.jdbc.Driver");
			output = DriverManager.getConnection("jdbc:mysql://" + server + "/eis?user=" + user + "&password=" + pass);
			if (output == null) {
				System.out.println("connection null!");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public static Connection oracleConnection() {
		Connection output = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			output = DriverManager.getConnection("jdbc:oracle:thin:@10.128.57.76:1521/exaprd","eis","EIS2014");
			if (output == null) {
				System.out.println("connection null!");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}
	
	public static Connection tableauRepoConnection() {
		Connection output = null;
		try {
			Class.forName("org.postgresql.Driver");
			output = DriverManager.getConnection("jdbc:postgresql://localhost:5432/workgroup","postgres","password");
			if (output == null) {
				System.out.println("connection null!");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

}

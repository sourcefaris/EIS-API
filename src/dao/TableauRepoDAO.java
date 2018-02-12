package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import service.DatabaseService;

public class TableauRepoDAO {
	public static int getUserLicensedSize() {
		int sizeUser = 0;
		Connection conn = DatabaseService.tableauRepoConnection();

		try {

			PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT u.name, (SELECT licensing_role_name WHERE name = u.name LIMIT 1)"
					+ "FROM _users u WHERE licensing_role_name NOT IN ('Unlicensed','Guest') ORDER BY name");

			ResultSet result = prep.executeQuery();

			while (result.next()) {
				sizeUser++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return sizeUser;
	}
	
	public static int getUserUnlicensedSize() {
		int sizeUser = 0;
		Connection conn = DatabaseService.tableauRepoConnection();

		try {

			PreparedStatement prep = conn.prepareStatement("SELECT DISTINCT u.name, (SELECT licensing_role_name WHERE name = u.name LIMIT 1)"
					+ "FROM _users u WHERE licensing_role_name = 'Unlicensed' AND name NOT LIKE '%system%'  ORDER BY name");

			ResultSet result = prep.executeQuery();

			while (result.next()) {
				sizeUser++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return sizeUser;
	}
}

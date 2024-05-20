package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionMysql {


		static Connection con=null;
		public static Connection getConnection() {
			if(con ==null) {
				try {
					con=DriverManager.getConnection("jdbc:mysql://localhost:3306/biblio","root","");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return con;
		

		}
}

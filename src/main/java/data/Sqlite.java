package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Sqlite {
    public Connection connect(){
        Connection con = null;

        try {
            String url = "jdbc:sqlite:subscription.db";
//            String url = "jdbc:sqlite:src/main/resources/DB/subscription.db";
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return con;
    }

}

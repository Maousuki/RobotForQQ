import data.Sqlite;

import java.sql.*;

public class StringTest {
    public static void main(String[] args) {

        String GroupID = "123";
        int UpID = 3434;
        String name = "嘉然";
        String sql = "insert into Dynamic values (" + GroupID + "," + UpID + "," + "\"" + name + "\"" + ")";

        Connection connect = new Sqlite().connect();
        try {
            PreparedStatement pst = connect.prepareStatement(sql);
            pst.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}

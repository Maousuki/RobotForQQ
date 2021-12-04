import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import data.Sqlite;

public class test {
    public static void main(String[] args) {
        String sql = "select * from Dynamic ";
        Connection connect = new Sqlite().connect();
        try {
            Statement stm = connect.createStatement();
            ResultSet resultSet = stm.executeQuery(sql);

            while (resultSet.next()){
                System.out.println(resultSet.getInt("UpID"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}

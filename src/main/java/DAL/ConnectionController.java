package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionController {
    private static final String url =
            "jdbc:mysql://ec2-52-30-211-3.eu-west-1.compute.amazonaws.com/s185103?user=s185103&password=A6fE9rT4KIhs53G05jsqL";


    public Connection createConnection() throws SQLException {
        String dbName = ("s185103");
        String userName = ("s185103");
        String password = ("A6fE9rT4KIhs53G05jsqL");
        String hostname = ("ec2-52-30-211-3.eu-west-1.compute.amazonaws.com");
        String port = ("3306");
        String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return  DriverManager.getConnection(jdbcUrl);
    }
}

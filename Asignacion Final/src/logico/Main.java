package logico;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream("config/config.properties")) {
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String url = props.getProperty("db.url");
        String username = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Conexión exitosa!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

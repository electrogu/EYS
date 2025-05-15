package database;

import config.*;
import java.io.FileReader;
import java.sql.*;
import java.util.Properties;

public class DBConnection {

    private static Statement st = null; 
    private static Connection cn = null;
    private static ResultSet rs = null;
    int port = 3306;
    private String host = null;
    private String db = null;
    private String username = null;
    private String pword = null;
    private String url = null;

    Encryption encryption = new Encryption();
    public DBConnection() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("DBC: Bağlantı sırasında bir hata oluştu: " + ex);
        }
    }

    public boolean testConnection(String URL, String Username, String Password){
        try {
            DriverManager.getConnection(URL, Username, Password);
            return true;
        } catch (SQLException ex) {
            System.out.println("DBC: Bağlantı sırasında bir hata oluştu: " + ex);
        }
        return false;
    }
    
    public final String getURL() {
        try ( FileReader reader = new FileReader(Variables.configProperties)) {
            Properties properties = new Properties();
            properties.load(reader);
            host = properties.getProperty("host");
            if (host.equals("null")) {
                port = 3306;
                host = "localhost";
                db = "application";
                username = "root";
                pword = "root";
            } else {
                port = Integer.valueOf(encryption.decrypt(properties.getProperty("port")));
                db = encryption.decrypt(properties.getProperty("database"));
                username = encryption.decrypt(properties.getProperty("username"));
                pword = encryption.decrypt(properties.getProperty("password"));
                host = encryption.decrypt(properties.getProperty("host"));
            }
            url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useUnicode=true&characterEncoding=utf8";
            return url;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    public final String getUsername() {
        try ( FileReader reader = new FileReader(Variables.configProperties)) {
            Properties properties = new Properties();
            properties.load(reader);
            host = properties.getProperty("host");
            if (host.equals("null")) {
                username = "root";
            } else {
                username = encryption.decrypt(properties.getProperty("username"));
            }
            return username;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    public final String getPassword() {
        try ( FileReader reader = new FileReader(Variables.configProperties)) {
            Properties properties = new Properties();
            properties.load(reader);
            host = properties.getProperty("host");
            if (host.equals("null")) {
                pword = "root";
            } else {
                pword = encryption.decrypt(properties.getProperty("password"));
            }
            return pword;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
    
}

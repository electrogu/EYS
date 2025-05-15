
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.util.*;

/**
 *
 * @author NUN
 */
public class LogDatabase extends DBConnection{
    static Statement st = null; 
    static Connection cn = null;
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    LoginDatabase ldb = new LoginDatabase();
    
    public LogDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }
    
    public void addDeviceChange(String deviceID, String operation, String parameter, String admin) {
        try {
            LocalTime time = LocalTime.now();
            LocalDate date = LocalDate.now();
            st = cn.createStatement();
            parameter = parameter==null?"NULL":"'"+parameter+"'";
            String query="SELECT * FROM application.inventory WHERE deviceID='"+deviceID+"'";
            rs = st.executeQuery(query);
            String deviceName = "";
            if(rs.next()) deviceName = rs.getString("deviceName");
            query="INSERT INTO application.devicelogs (deviceName, deviceID, operation, parameter, admin, time, date) VALUES ('"+deviceName+"', '"+deviceID+"', '"+operation+"', "+parameter+", '"+admin+"', '"+time+"', '"+date+"')";
            st.executeUpdate(query);

        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public List<String[]> getDeviceLog(String deviceID) {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.devicelogs WHERE deviceID="+deviceID+" ORDER BY devicelogsID DESC";
            rs = st.executeQuery(query);
            while (rs.next()) {
                String operation = rs.getString("operation"); 
                String parameter = rs.getString("parameter");
                String admin = rs.getString("admin"); 
                String time = rs.getTime("time").toString(); 
                String date = rs.getDate("date").toString();
                values.add(new String[] {operation,parameter,admin,time,date});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public void addProfileChange(String profileID, String operation, String parameter, String admin) {
        try {
            LocalTime time = LocalTime.now();
            LocalDate date = LocalDate.now();
            st = cn.createStatement();
            parameter = parameter==null?"NULL":"'"+parameter+"'";
            String query="SELECT * FROM application.profiles WHERE profileID='"+profileID+"'";
            rs = st.executeQuery(query);
            String profileNameSurname = "";
            if(rs.next()) profileNameSurname = rs.getString("name")+" "+rs.getString("surname");
            query="INSERT INTO application.profilelogs (profileID, profileNameSurname, operation, parameter, admin, time, date) VALUES ('"+profileID+"', '"+profileNameSurname+"', '"+operation+"', "+parameter+", '"+admin+"', '"+time+"', '"+date+"')";
            st.executeUpdate(query);

        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public List<String[]> getProfileLog(String profileID) {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.profilelogs WHERE profileID="+profileID+" ORDER BY profilelogsID DESC";
            rs = st.executeQuery(query);
            while (rs.next()) {
                String operation = rs.getString("operation"); 
                String parameter = rs.getString("parameter"); 
                String admin = rs.getString("admin");
                String time = rs.getTime("time").toString(); 
                String date = rs.getDate("date").toString();
                values.add(new String[] {operation,parameter,admin,time,date});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
}

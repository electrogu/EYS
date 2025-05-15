
package database;

import java.sql.*;
import config.Variables;
import java.time.*;
import java.util.*;

/**
 *
 * @author NUN
 */
public class NotificationUtilDatabase extends DBConnection{

    static Statement st = null; 
    static Connection cn = null;
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    Variables vary = new Variables();
    boolean error = false;

    public NotificationUtilDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            error = true;
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }

    public List<String[]> getInventory() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.inventory";
            
            /*Getting the result of the query search in database and it returns each item found in database.*/
            rs = st.executeQuery(query);
            
            /*The following process adds each item stored in database into the dynamic array list initialized before*/
            while (rs.next()) {
                String device = rs.getString("deviceName");
                String warranty = rs.getDate("warranty").toString();
                Period duration = Period.between(LocalDate.now(), rs.getDate("warranty").toLocalDate());
                String remainingWarranty = String.valueOf(duration.getDays()
                        + duration.getMonths() * 30 + duration.getYears() * 12 * 30);
                values.add(new String[]{device, warranty, remainingWarranty});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
}

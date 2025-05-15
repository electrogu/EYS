
package database;

import java.awt.Color;
import java.sql.*;
import config.Pair;
import config.Variables;

/**
 *
 * @author NUN
 */
public class LoginDatabase extends DBConnection {

    static Statement st = null;
    static Connection cn = null;
    static ResultSet rs = null;
    boolean error = false;

    public LoginDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            error = true;
            System.out.println("LDB: Bağlantı sırasında bir hata oluştu: " + ex);
        }
    }

    public String[] authorization(String username, String password) {
        String[] result = new String[3];
        try {
            st = cn.createStatement();
            String query = "Select * From users where username='" + username + "' and password='" + password + "'";
            rs = st.executeQuery(query);
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("userid")); 
                String dateofcreation = rs.getDate("dateofcreation").toString();
                String perm = rs.getString("perm");
                Variables.id = id;
                result[0] = id;
                result[1] = perm;
                result[2] = dateofcreation;

            }

        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }

        return result;
    }

    public Pair<String, Color> checkConnection() {
        if (cn == null && !error) {
            return new Pair<>("Veritabanı Bağlantısı Kurulamadı", Color.LIGHT_GRAY);
        } else if (error) {
            return new Pair<>("Bağlantı Kurulurken Bir Hata Oluştu", Color.RED);
        } else if (cn != null) {
            float[] hsb = Color.RGBtoHSB(0, 135, 53, null);
            return new Pair<>("Veritabanı Bağlantısı Kuruldu", Color.getHSBColor(hsb[0], hsb[1], hsb[2]));
        }

        return null;
    }
}

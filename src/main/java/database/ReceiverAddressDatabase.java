
package database;

import config.Dialogs;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author 
 */
public class ReceiverAddressDatabase extends DBConnection {

    static Statement st = null;
    static Connection cn = null;
    static ResultSet rs = null;

    public ReceiverAddressDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }

    /*A sub-procedure to return the current email address for verification*/
    public String getAddress() {
        String receiverAddress = null;
        try {
            st = cn.createStatement();
            String query = "Select * From config ";

            rs = st.executeQuery(query);

            if (rs.next()) {
                receiverAddress = rs.getString("receiverAddress");
            }
        } catch (SQLException e) {
            /*If the program faces an error, it prints the error.*/
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return receiverAddress;
    }

    public boolean setAddress(String receiverAddress) {
        try {
            st = cn.createStatement();
            String query = "Select * from config where receiverAddress='" + receiverAddress + "'";
            rs = st.executeQuery(query);
            if (rs.next()) {
                Dialogs.infoBox("Bu mail adresi zaten kayıtlı!", "Mevcut Adres");
                return false;
            }

            query = "UPDATE application.config SET receiverAddress='" + receiverAddress + "' WHERE receiverAddress='" + getAddress() + "'";
            st.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            /*If the program faces an error, it prints the error.*/
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return false;
    }
}

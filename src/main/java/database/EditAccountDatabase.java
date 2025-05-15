
package database;

import java.sql.*;
import config.Dialogs;

/**
 *
 * @author NUN
 */
public class EditAccountDatabase extends DBConnection{

    static Statement st = null; //SQL ifadelerini veritabanına göndermek için bu interface’ten oluşturulan nesneler kullanılır.
    static Connection cn = null;//Bu interface, bütün metotları ile veritabanına irtibat kurmak için kullanılır.
    static ResultSet rs = null;
    DBConnection con = new DBConnection();

    public EditAccountDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }

    public boolean checkMainAdminPassword(String mainadminpassword) {
        try {
            st = cn.createStatement();
            
            String query = "SELECT * FROM users WHERE perm='Ana Admin' AND password='"+mainadminpassword+"'";
            rs = st.executeQuery(query);
            if(!rs.next()){
                Dialogs.infoBox("Girilen ana admin şifresi yanlış!", "Yanlış Şifre");
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
        }
        return false;
    }
    
    public void editAccount(String ID, String username, String newpassword, String permission, String mainadminpassword) {
        try {
            st = cn.createStatement();

            String query = "SELECT * FROM application.users WHERE perm='Ana Admin' AND password='" + mainadminpassword + "'";
            rs = st.executeQuery(query);
            if (!rs.next()) {
                Dialogs.infoBox("Girilen ana admin şifresi yanlış!", "Yanlış Şifre");
                return;
            }
            String text = "";
            if (!username.equals("") && !newpassword.equals("") && !permission.equals("")) {
                text = "username='" + username + "', password='" + newpassword + "', perm='" + permission + "'";
            }
            if (!username.equals("") && !newpassword.equals("") && permission.equals("")) {
                text = "username='" + username + "', password='" + newpassword + "'";
            }
            if (!username.equals("") && newpassword.equals("") && !permission.equals("")) {
                text = "username='" + username + "', permission='" + permission + "'";
            }
            if (!username.equals("") && newpassword.equals("") && permission.equals("")) {
                text = "username='" + username + "'";
            }
            if (username.equals("") && !newpassword.equals("") && !permission.equals("")) {
                text = "password='" + newpassword + "', permission='" + permission + "'";
            }
            if (username.equals("") && newpassword.equals("") && !permission.equals("")) {
                text = "permission='" + permission + "'";
            }
            if (username.equals("") && !newpassword.equals("") && permission.equals("")) {
                text = "password='" + newpassword + "'";
            }
            if (username.equals("") && newpassword.equals("") && permission.equals("")) {
                return;
            }
            String query1 = "UPDATE application.users SET " + text + " WHERE userid='" + ID + "'";
            st.executeUpdate(query1);

            Dialogs.infoBox("Hesap ismi " + username + ", şifre " + newpassword + " ve yetki " + permission + " olarak güncellendi.", "Başarılı");

        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
        }
    }

    public boolean checkAccount(String ID) {
        try {
            st = cn.createStatement();
            String query = "SELECT * FROM application.users WHERE userid='" + ID + "'";
            rs = st.executeQuery(query);
            if (!rs.next()) {
                Dialogs.errorBox("Değişiklik yapılacak kullanıcı bulunmuyor!", "Hata");
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
        }
        return false;
    }

    public boolean checkSecondMainAdmin(String ID, boolean change, boolean delete) {
        try {
            st = cn.createStatement();
            String query = "SELECT * FROM application.users WHERE perm='Ana Admin'";
            rs = st.executeQuery(query);
            int count = 0;
            boolean isMainAdmin = false;
            while (rs.next()) {
                count++;
                if (rs.getString("userid").equals(ID)) {
                    isMainAdmin = true;
                }
            }
            if (!delete) {
                if (count > 1 || !change) {
                    return true;
                } else {
                    Dialogs.errorBox("Bu kullanıcı dışında bir ana admin yok!", "Hata");
                    return false;
                }
            } else {
                if (count > 1 || !isMainAdmin) {
                    return true;
                } else {
                    Dialogs.errorBox("Bu kullanıcı dışında bir ana admin yok!", "Hata");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
        }
        return false;
    }

    public String[] getAccount(String ID) {
        try {
            st = cn.createStatement();
            String query = "SELECT * FROM application.users WHERE userid='" + ID + "'";
            rs = st.executeQuery(query);
            if (rs.next()) {
                String username = rs.getString("username");
                String perm = rs.getString("perm");
                String password = rs.getString("password");
                return new String[]{username, perm, password};
            }
        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
        }
        return null;
    }

    public void deleteAccount(String ID, String mainadminpassword) {
        try {
            st = cn.createStatement();
            String query = "SELECT * FROM application.users WHERE perm='Ana Admin' AND password='" + mainadminpassword + "'";
            rs = st.executeQuery(query);
            if (!rs.next()) {
                Dialogs.infoBox("Girilen ana admin şifresi yanlış!", "Yanlış Şifre");
                return;
            }
            String query1 = "DELETE FROM application.users WHERE userid='" + ID + "'";
            st.executeUpdate(query1);

            Dialogs.infoBox(ID + " ID'li kullanıcı hesabı silindi!", "Başarılı");
        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
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
}

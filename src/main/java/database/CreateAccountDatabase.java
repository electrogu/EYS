
package database;
import java.sql.*;
import java.time.LocalDate;
import config.Dialogs;

/**
 *
 * @author NUN
 */
public class CreateAccountDatabase extends DBConnection{
    static Statement st = null; //SQL ifadelerini veritabanına göndermek için bu interface’ten oluşturulan nesneler kullanılır.
    static Connection cn = null;//Bu interface, bütün metotları ile veritabanına irtibat kurmak için kullanılır.
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    
    public CreateAccountDatabase() {
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
    
    public void createAccount(String username, String newpassword, String permission, String mainadminpassword) {
        try {
            st = cn.createStatement();
            
            String query = "SELECT * FROM users WHERE perm='Ana Admin' AND password='"+mainadminpassword+"'";
            rs = st.executeQuery(query);
            if(!rs.next()){
                Dialogs.infoBox("Girilen ana admin şifresi yanlış!", "Yanlış Şifre");
                return;
            }
            st = cn.createStatement();
            query = "INSERT INTO application.users (username, password, perm, dateofcreation) VALUES ('"+username+"', '"+newpassword+"', '"+permission+"', '"+LocalDate.now()+"');";
            st.executeUpdate(query);
            Dialogs.infoBox("Hesap başarıyla oluşturuldu!\nİsim: "+username+", Şifre:"+newpassword+", Yetki: "+permission+"", "Başarılı!");
            
        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
        }
    }
    
    public boolean checkAccount(String username, String newpassword) {
        try {
            st = cn.createStatement();
            String query = "SELECT * FROM users WHERE username='"+username+"'";
            rs = st.executeQuery(query);
            if(rs.next()){
                Dialogs.errorBox("Bu isme sahip bir hesap zaten bulunuyor! Başka bir isim giriniz.", "Hata");
                return false;
            }else return true;
        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
        }
        return false;
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

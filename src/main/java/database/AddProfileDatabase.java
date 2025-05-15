
package database;

import config.Variables;
import config.Dialogs;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 *
 * @author NUN
 */
public class AddProfileDatabase extends DBConnection{
    static Statement st = null; //SQL ifadelerini veritabanına göndermek için bu interface’ten oluşturulan nesneler kullanılır.
    static Connection cn = null;//Bu interface, bütün metotları ile veritabanına irtibat kurmak için kullanılır.
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    LogDatabase log = new LogDatabase();
    public AddProfileDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }
    
    public void addProfile(String name, String surname, String identitynumber, String duty) {
        try {
            st = cn.createStatement();
            
            String query = "SELECT * FROM application.profiles WHERE name='"+name+"' AND surname='"+surname+"'";
            rs = st.executeQuery(query);
            if(rs.next()){
                Dialogs.infoBox("Bu isimde bir profil zaten kaydedilmiş! Başka bir isim giriniz ya da profilleri kontrol ediniz.", "Veritabanı Hatası!");
                return;
            }
                
            String query1 = "INSERT INTO application.profiles (name, surname, status, identitynumber, duty, dateofcreation) VALUES ('"+name+"', '"+surname+"', 'çalışıyor', '"+identitynumber+"', '"+duty+"', '"+LocalDate.now()+"')";
            st.executeUpdate(query1);
            
            rs = st.executeQuery("SELECT * FROM application.profiles WHERE name='"+name+"' AND surname='"+surname+"'");
            String id = "";
            if(rs.next()){
                id = rs.getString("profileID");
            }
            log.addProfileChange(id, "Profil Eklendi", null, Variables.uname);
            Dialogs.infoBox("Profil başarıyla kaydedildi!", "Başarılı");
            
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
}

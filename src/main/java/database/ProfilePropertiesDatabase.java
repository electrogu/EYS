
package database;

import config.*;
import java.sql.*;
import java.util.*;

/**
 *
 * @author NUN
 */
public class ProfilePropertiesDatabase extends DBConnection{
    static Statement st = null; 
    static Connection cn = null;
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    LoginDatabase ldb = new LoginDatabase();
    LogDatabase log = new LogDatabase();
    
    public ProfilePropertiesDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }
    
    public String[] getProfile(String ID) {
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.profiles WHERE profileID='"+ ID +"'";
            rs = st.executeQuery(query);
                // print data of each row
            while(rs.next()){
                String name = rs.getString("name"); //tablodaki sütun ismi
                String surname = rs.getString("surname"); //tablodaki sütun ismi
                String status = rs.getString("status"); //tablodaki sütun ismi
                String identitynumber = rs.getString("identitynumber"); //tablodaki sütun ismi
                String duty = rs.getString("duty"); //tablodaki sütun ismi
                String dateofcreation = rs.getDate("dateofcreation").toString();
                String[] values = {name, surname, status, identitynumber, duty, dateofcreation};
                return values;
            }
            
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return null;
    }
    
    public List<String[]> getAssignedDevices(String profileID) {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();

            String query="SELECT * FROM application.inventory WHERE assignedProfileID='"+profileID+"'";
            rs = st.executeQuery(query);
                // print data of each row
            while (rs.next()) {
                String ID = rs.getString("deviceID"); //tablodaki sütun ismi
                String name = rs.getString("deviceName"); //tablodaki sütun ismi
                values.add(new String[] {ID,name});
            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public List<String[]> getAllAvailableDevices() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();

            String query="SELECT * FROM application.inventory WHERE status='Boşta'";
            rs = st.executeQuery(query);
                // print data of each row
            while (rs.next()) {
                String ID = rs.getString("deviceID"); //tablodaki sütun ismi
                String name = rs.getString("deviceName"); //tablodaki sütun ismi
                values.add(new String[] {ID, name});
            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public void updateStatus(String profileID, String newStatus) {
        try {
            st = cn.createStatement();
            String query="UPDATE application.profiles SET status='"+newStatus+"' WHERE profileID='"+profileID+"'";
            
            st.executeUpdate(query);

            Dialogs.infoBox("Durum değiştirildi!", "Başarılı");
            log.addProfileChange(profileID, "Durum Değiştirme", newStatus, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public void removeAssignment(String deviceID, String deviceidname, String profileID, String profileName) {
        try {
            st = cn.createStatement();
            String query="UPDATE application.inventory SET assignedProfile=NULL, assignedProfileID=NULL, status='Boşta' WHERE deviceID='"+deviceID+"'";
            String profilenameid = profileID + " - " + profileName;
            
            st.executeUpdate(query);

            Dialogs.infoBox("Atama başarıyla kaldırıldı!", "Başarılı");
            log.addDeviceChange(deviceID, "Atama Kaldırma", profilenameid, Variables.uname);
            log.addProfileChange(profileID, "Atama Kaldırma", deviceidname, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public void assign(String deviceID, String deviceidname, String profileID, String profileName) {
        try {
            st = cn.createStatement();
            String query="UPDATE application.inventory SET assignedProfile='"+profileName+"', assignedProfileID='"+profileID+"', status='Kullanımda' WHERE deviceID='"+deviceID+"'";
            String profilenameid = profileID + " - " + profileName;
            
            st.executeUpdate(query);

            Dialogs.infoBox(deviceID+" ID'li cihaz bu profile atandı!", "Başarılı");
            log.addDeviceChange(deviceID, "Cihaz Atama", profilenameid, Variables.uname);
            log.addProfileChange(profileID, "Cihaz Atama", deviceidname, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public void removeAllAssignments(List<String[]> deviceIDs, String profileID, String profileName) {
        try {
            st = cn.createStatement();
            for(String[] device : deviceIDs){
                String deviceID = device[0];
                String query="UPDATE application.inventory SET assignedProfile=NULL, assignedProfileID=NULL, status='Boşta' WHERE deviceID='"+deviceID+"'";
                String profilenameid = profileID + " - " + profileName;
                st.executeUpdate(query);
                log.addDeviceChange(deviceID, "Atama Kaldırma", profilenameid, Variables.uname);
                log.addProfileChange(profileID, "Atama Kaldırma", "ID: "+deviceID, Variables.uname);
            }
            Dialogs.infoBox("Bütün atamalar başarıyla kaldırıldı!", "Başarılı");
        } catch (SQLException e) {
            System.out.println("Verileri güncellerken bir hata oluştu:" + e);
            Dialogs.errorBox(e.toString(), "Hata! Cihazların atamasını kontrol ediniz.");
        }
    }
    
    public void edit(String profileID, String newName, String newSurname, String newDuty, String newidentitynumber) {
        try {
            st = cn.createStatement();
            
            String query="UPDATE application.profiles SET name='"+newName+"', surname='"+newSurname+"', duty='"+newDuty+"', identitynumber='"+newidentitynumber+"' WHERE profileID='"+profileID+"'";
            String query1="UPDATE application.inventory SET assignedProfile='"+newName+" "+newSurname+"' WHERE assignedProfileID='"+profileID+"'";
            
            st.executeUpdate(query);
            st.executeUpdate(query1);
            
            Dialogs.infoBox("Profil ismi "+newName+", soyismi "+newSurname+", görevi "+newDuty+", telefon numarası ise "+newidentitynumber+" olarak güncellendi.", "Başarılı");
            log.addProfileChange(profileID, "Profil Düzenleme", newName+" "+newSurname+" | "+newDuty+" | "+newidentitynumber, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public void deleteProfile(String profileID, String profileName, String profileSurname) {
        try {
            st = cn.createStatement();
            
            String query="DELETE FROM application.profiles WHERE profileID='"+profileID+"'";
            List<String[]> assignedDevices = getAssignedDevices(profileID);
            for(String[] device : assignedDevices){
                String deviceID = device[0];
                String query1="UPDATE application.inventory SET assignedProfile=NULL, assignedProfileID=NULL, status='Boşta' WHERE deviceID='"+deviceID+"'";
                log.addDeviceChange(deviceID, "Profil Silme ve Atama Kaldırma", null, Variables.uname);
                st.executeUpdate(query1);
            }
            st.executeUpdate(query);
            String profileNameSurname = profileName + " " + profileSurname;
            Dialogs.infoBox(profileName+" isimli ve "+profileID+" ID'li profil silindi.", "Başarılı");
            log.addProfileChange(profileID, "Profil Silme", profileID + " - " + profileNameSurname, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
}

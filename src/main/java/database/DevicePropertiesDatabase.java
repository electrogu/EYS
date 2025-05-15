
package database;

import config.Variables;
import config.Dialogs;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import config.Pair;

/**
 *
 * @author NUN
 */
public class DevicePropertiesDatabase extends DBConnection{
    static Statement st = null;
    static Connection cn = null;
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    LoginDatabase ldb = new LoginDatabase();
    LogDatabase log = new LogDatabase();
    
    public DevicePropertiesDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }
    
    public Pair<Date, String[]> getDevice(String ID) {
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.inventory WHERE deviceID='"+ ID +"'";
            rs = st.executeQuery(query);
                // print data of each row
            while(rs.next()){
                String category = rs.getString("category");
                String device = rs.getString("deviceName"); 
                String status = rs.getString("status"); 
                String assignedProfile = rs.getString("assignedProfile");
                String assignedProfileID = rs.getString("assignedProfileID");
                String serialNum = rs.getString("serialNum");
                Date warranty = rs.getDate("warranty");
                Period duration = Period.between(LocalDate.now(), rs.getDate("warranty").toLocalDate());
                String remainingWarranty = String.valueOf(duration.getDays() + duration.getMonths()*30 + duration.getYears()*12*30);
                String[] values = {category, device, status, assignedProfile, assignedProfileID, remainingWarranty, serialNum};
                return new Pair<>(warranty, values);
            }
            
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return null;
    }
    
    public List<String[]> getDeviceProperties(String name, String categoryName) {
        List<String[]> values = new ArrayList<>();
        
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.properties WHERE deviceName='"+ name +"'";
            rs = st.executeQuery(query);
            String[] properties = new String[10];
            if(rs.next()){
                String p1 = rs.getString("p1"); 
                String p2 = rs.getString("p2"); 
                String p3 = rs.getString("p3");
                String p4 = rs.getString("p4"); 
                String p5 = rs.getString("p5"); 
                String p6 = rs.getString("p6");
                String p7 = rs.getString("p7");
                String p8 = rs.getString("p8");
                String p9 = rs.getString("p9");
                String p10 = rs.getString("p10");
                properties[0] = p1; properties[1] = p2; properties[2] = p3; properties[3] = p4; properties[4] = p5; properties[5] = p6; properties[6] = p7; properties[7] = p8;
                properties[8] = p9; properties[9] = p10;
            }
            query="SELECT * FROM application.propertynames WHERE categoryName='"+ categoryName +"'";
            rs = st.executeQuery(query);
            String[] propertyNames = new String[10];
            if(rs.next()){
                String p1Name = rs.getString("p1"); 
                String p2Name = rs.getString("p2"); 
                String p3Name = rs.getString("p3"); 
                String p4Name = rs.getString("p4"); 
                String p5Name = rs.getString("p5"); 
                String p6Name = rs.getString("p6");
                String p7Name = rs.getString("p7");
                String p8Name = rs.getString("p8");
                String p9Name = rs.getString("p9");
                String p10Name = rs.getString("p10");
                propertyNames[0] = p1Name; propertyNames[1] = p2Name; propertyNames[2] = p3Name; propertyNames[3] = p4Name; propertyNames[4] = p5Name;
                propertyNames[5] = p6Name; propertyNames[6] = p7Name; propertyNames[7] = p8Name; propertyNames[8] = p9Name; propertyNames[9] = p10Name;
                //String[] propertyNames = {p1Name, p2Name, p3Name, p4Name, p5Name, p6Name, p7Name, p8Name, p9Name, p10Name};
            }
                for(int x=0;x<10;x++){
                    values.add(new String[] {propertyNames[x], properties[x]});
                }
            return values;
                
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return null;
    }
    
    public List<String[]> getProfilesFull() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();

            String query="SELECT * FROM application.profiles";
            rs = st.executeQuery(query);
                // print data of each row
            while (rs.next()) {
                String ID = rs.getString("profileID"); //tablodaki sütun ismi
                String name = rs.getString("name") + " " + rs.getString("surname"); //tablodaki sütun ismi
                String status = rs.getString("status"); //tablodaki sütun ismi
                values.add(new String[] {ID,name,status});
            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public void removeAssignment(String deviceID, String deviceName, String profileID) {
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.inventory WHERE deviceID='"+deviceID+"'";
            String query1="UPDATE application.inventory SET assignedProfile=NULL, assignedProfileID=NULL, status='Boşta' WHERE deviceID='"+deviceID+"'";
            rs = st.executeQuery(query);
            String profilenameid = null;
            if(rs.next()){
                profilenameid = rs.getString("assignedProfileID") + " - " + rs.getString("assignedProfile");
            }
            st.executeUpdate(query1);

            Dialogs.infoBox("Atama başarıyla kaldırıldı!", "Başarılı");
            log.addDeviceChange(deviceID, "Atama Kaldırma", profilenameid, Variables.uname);
            log.addProfileChange(profileID, "Atama Kaldırma", deviceID+" - "+deviceName, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public void updateStatus(String deviceID, String status) {
        try {
            st = cn.createStatement();

            String query="UPDATE application.inventory SET status='"+status+"' WHERE deviceID='"+deviceID+"'";
            st.executeUpdate(query);

            Dialogs.infoBox("Cihaz durumu başarıyla güncellendi!", "Başarılı");
            log.addDeviceChange(deviceID, "Durum değiştirildi", status, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public String assign(String profileName, String deviceID, String devicename) {
        try {
            String[] a = profileName.split(" ");
            String first = a[0], last = a[a.length-1];
            st = cn.createStatement();
            String query = "SELECT * FROM application.profiles WHERE name LIKE '"+first+"%' and surname LIKE '%"+last+"'";
            rs = st.executeQuery(query);
            String profileID = "";
            if(rs.next()){
                profileID = rs.getString("profileID");
            }
            String query1="UPDATE application.inventory SET status='Kullanımda', assignedProfile='"+profileName+"', assignedProfileID='"+profileID+"' WHERE deviceID='"+deviceID+"'";
            st.executeUpdate(query1);
            
            Dialogs.infoBox("Cihaz başarıyla "+profileName+" profiline atandı!", "Başarılı");
            log.addDeviceChange(deviceID, "Cihaz Atama", profileID+" - "+profileName, Variables.uname);
            log.addProfileChange(profileID, "Cihaz Atama", deviceID+" - "+devicename, Variables.uname);
            return profileID;
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return null;
    }
    
    public void edit(String deviceID, String newDeviceName, LocalDate newWarrantyDate, String newCategory) {
        try {
            st = cn.createStatement();
            
            String query="UPDATE application.inventory SET deviceName='"+newDeviceName+"', warranty='"+newWarrantyDate+"', category='"+newCategory+"' WHERE deviceID='"+deviceID+"'";
            st.executeUpdate(query);
            
            Dialogs.infoBox("Cihaz ismi "+newDeviceName+", kategori "+newCategory+", garanti bitiş tarihi ise "+newWarrantyDate.toString()+" olarak güncellendi.", "Başarılı");
            log.addDeviceChange(deviceID, "Cihaz Düzenleme", newDeviceName+" | "+newWarrantyDate+" | "+newCategory, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public void deleteDevice(String deviceID, String deviceName, String profileID) {
        try {
            st = cn.createStatement();
            
            String query="DELETE FROM application.inventory WHERE deviceID='"+deviceID+"'";
            st.executeUpdate(query);
            
            Dialogs.infoBox(deviceName+" isimli ve "+deviceID+" ID'li cihaz silindi.", "Başarılı");
            log.addDeviceChange(deviceID, "Cihaz Silme", null, Variables.uname);
            if(profileID != null && !profileID.equals(""))
                log.addProfileChange(profileID, "Cihaz Silme ve Atama Kaldırma", deviceID+" - "+deviceName, Variables.uname);
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public boolean checkProfile(String profileID) {
        try {
            st = cn.createStatement();
            
            String query="SELECT * FROM application.profiles WHERE profileID='"+profileID+"'";
            rs = st.executeQuery(query);
            if(!rs.next()){
                Dialogs.infoBox("Böyle bir profil kayıtlı değil ya da silinmiş.", "Uyarı");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return true;
    }
}

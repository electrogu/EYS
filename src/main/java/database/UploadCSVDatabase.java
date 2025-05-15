
package database;

import config.*;
import java.sql.*;
import java.text.*;
import java.time.*;
import java.util.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author NUN
 */
public class UploadCSVDatabase extends DBConnection{

    static Statement st = null; 
    static Connection cn = null;
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    LogDatabase log = new LogDatabase();
    Variables vary = new Variables();
    
    
    public UploadCSVDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }

    public void addDevices(List<List<String>> data, int categoryindex, int devicenameindex, int devicestatusindex, int warrantydateindex, int serialnumindex) {

        int existingSerialNum = 0, existingDeviceName = 0, existingDeviceNameProperties = 0, registeredDevices = 0;
        boolean alreadyexists = false;
        List<String> deviceNames = new ArrayList<>();
        System.out.println(String.valueOf(categoryindex)+String.valueOf(devicenameindex)+String.valueOf(devicestatusindex)+String.valueOf(warrantydateindex)+String.valueOf(serialnumindex));
        for (List<String> row : data) {
            String deviceName = row.get(devicenameindex), category = row.get(categoryindex), warrantyDate = row.get(warrantydateindex), serialNum = row.get(serialnumindex);
            deviceName = deviceName.replace("'", "`");
            category = category.replace("'", "`");
            warrantyDate = warrantyDate.replace("'", "`");
            serialNum = serialNum.replace("'", "`");
            if (warrantyDate.contains("/")) {
                warrantyDate = warrantyDate.replace("/", "-");
                String[] date = warrantyDate.split("-");
                if (date.length > 3 || date.length < 3) {
                    Dialogs.errorBox("Garanti bitiş tarihini lütfen gg/aa/yyyy ya da yyyy-aa-gg olarak giriniz", "Hata");
                    return;
                }
                warrantyDate = "";
                int x=0;
                while(x<3){
                    if(x==2)
                        warrantyDate += date[x].length() == 1 ? "0" + date[x] : date[x];
                    else
                        warrantyDate += date[x].length() == 1 ? "0" + date[x] + "-" : date[x] + "-";
                    x++;
                }
            } else if (warrantyDate.contains(".")) {
                warrantyDate = warrantyDate.replace(".", "-");
                String[] date = warrantyDate.split("-");
                if (date.length > 3 || date.length < 3) {
                    Dialogs.errorBox("Garanti bitiş tarihini lütfen gg/aa/yyyy ya da yyyy-aa-gg olarak giriniz", "Hata");
                    return;
                }
                warrantyDate = "";
                int x=0;
                while(x<3){
                    if(x==2)
                        warrantyDate += date[x].length() == 1 ? "0" + date[x] : date[x];
                    else
                        warrantyDate += date[x].length() == 1 ? "0" + date[x] + "-" : date[x] + "-";
                    x++;
                }
            }
            DateValidator validator = new DateValidator("dd-MM-yyyy");
            DateValidator validator1 = new DateValidator("MM-dd-yyyy");
            if(validator.isValid(warrantyDate) || validator1.isValid(warrantyDate)){
                String format = validator.isValid(warrantyDate) ? "dd-MM-yyyy" : "MM-dd-yyyy";
                SimpleDateFormat format1 = new SimpleDateFormat(format);
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = format1.parse(warrantyDate);
                    warrantyDate = format2.format(date);
                } catch (ParseException ex) {
                    Logger.getLogger(UploadCSVDatabase.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            DateValidator validator4 = new DateValidator("yyyy-MM-dd");
            boolean result = validator4.isValid(warrantyDate);
            if(!result){
                Dialogs.errorBox("Garanti bitiş tarihini lütfen gg/aa/yyyy ya da yyyy-aa-gg olarak giriniz", "Hata");
                return;
            }

            try {
                st = cn.createStatement();

                String query1 = "SELECT * FROM application.inventory WHERE deviceName='" + deviceName + "'";
                rs = st.executeQuery(query1);
                if (rs.next()) {
                    existingDeviceName++;
                    alreadyexists = true;
                } else if (!deviceNames.contains(deviceName)) {
                    deviceNames.add(deviceName);
                }

                String query2 = "SELECT * FROM application.properties WHERE deviceName='" + deviceName + "'";
                rs = st.executeQuery(query2);
                if (!rs.next()) {
                    String q = "INSERT INTO application.properties (deviceName, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) VALUES ('" + deviceName + "', '', '', '', '', '', '', '', '', '', '')";
                    st.executeUpdate(q);
                } else {
                    existingDeviceNameProperties++;
                }

                String query3 = "SELECT * FROM application.inventory";
                rs = st.executeQuery(query3);
                List<String> serNums = new ArrayList<>();
                while (rs.next()) {
                    serNums.add(rs.getString("serialNum"));
                }
                if (serNums.contains(serialNum) && !serialNum.equals("")) {
                    //functions.infoBox(serialNum + " seri numarası zaten kaydedilmiş! Lütfen kontrol ediniz.", "Hata");
                    existingSerialNum++;
                }else{
                if (serialNum != null && !serialNum.equals("")) {
                    String p = "('" + category + "', '" + deviceName + "', 'boşta', '" + warrantyDate + "', '" + serialNum + "')";
                    st.executeUpdate("INSERT INTO application.inventory (category, deviceName, status, warranty, serialNum) VALUES " + p + ";");
                    registeredDevices++;
                }

                //functions.infoBox("Cihaz(lar) başarıyla kaydedildi!", "Başarılı");
                rs = st.executeQuery("SELECT * FROM application.inventory WHERE serialNum='" + serialNum + "'");
                while (rs.next()) {
                    String deviceID = String.valueOf(rs.getInt("deviceID"));
                    log.addDeviceChange(deviceID, "Cihaz CSV Dosyası İle Eklendi", null, Variables.uname);
                } 
                }
            } catch (SQLException e) {
                System.out.println("Verileri kaydetme sırasında bir hata oluştu:" + e);
                //functions.infoBox(e.toString(), "Verileri kaydetme sırasında bir hata oluştu!");
                if (!alreadyexists) {
                    String deleteproperties = "DELETE FROM application.properties WHERE deviceName='" + deviceName + "'",
                            deleteinventory = "DELETE FROM application.inventory WHERE serialNum='" + serialNum + "'",
                            checkproperties = "SELECT * FROM application.properties WHERE deviceName='" + deviceName + "'",
                            checkinventory = "SELECT * FROM application.inventory WHERE serialNum='" + serialNum + "'";
                    try {
                        st = cn.createStatement();

                        rs = st.executeQuery(checkproperties);
                        if (rs.next()) {
                            st.executeUpdate(deleteproperties);
                        }

                        rs = st.executeQuery(checkinventory);
                        if (rs.next()) {
                            st.executeUpdate(deleteinventory);
                        }
                    } catch (SQLException error) {
                        System.out.println("Kontrol sırasında bir hata oluştu:" + error);
                    }
                }
            }
        }
        if (existingSerialNum == 0 && existingDeviceName == 0 && existingDeviceNameProperties == 0 && registeredDevices != 0) {
            Dialogs.infoBox("Başarıyla kaydedilen cihaz sayısı: " + registeredDevices, "Başarılı");
        } else {
            Dialogs.infoBox("Seri numarası zaten kayıtlı olup yüklenmeyen cihaz sayısı: " + existingSerialNum + "\nÖzellikleri yüklenmeyen cihaz sayısı: " + existingDeviceNameProperties + "\nÖzellikleri güncellenmeyen cihaz sayısı: " + existingDeviceName + "\n\nBaşarıyla kaydedilen cihaz sayısı: " + registeredDevices, "Uyarı");
        }
    }

    public void addProfiles(List<List<String>> data, int profilenameindex, int profilestatusindex, int profilesurnameindex, int identitynumberindex, int dutyindex) {

        int existingProfiles = 0, registeredProfiles = 0;
        for (List<String> row : data) {
            String profileName = row.get(profilenameindex), surname = row.get(profilesurnameindex), status = row.get(profilestatusindex), identitynumber = row.get(identitynumberindex), duty = row.get(dutyindex);
            profileName = profileName.replace("'", "`");
            surname = surname.replace("'", "`");
            status = status.replace("'", "`");
            identitynumber = identitynumber.replace("'", "`");
            duty = duty.replace("'", "`");
            try {
                st = cn.createStatement();

                String query1 = "SELECT * FROM application.profiles WHERE name='" + profileName + "' AND surname='" + surname + "'";
                rs = st.executeQuery(query1);
                if (rs.next()) {
                    existingProfiles++;
                } else {
                    String query2 = "INSERT INTO application.profiles (name, surname, status, identitynumber, duty, dateofcreation) VALUES ('" + profileName + "', '" + surname + "', '" + status + "', '" + identitynumber + "', '" + duty + "', '" + LocalDate.now() + "')";
                    st.executeUpdate(query2);
                    registeredProfiles++;
                }
                rs = st.executeQuery("SELECT * FROM application.profiles WHERE name='" + profileName + "' AND surname='" + surname + "'");
                String id = "";
                if (rs.next()) {
                    id = rs.getString("profileID");
                }
                log.addProfileChange(id, "Profil CSV Dosyası İle Eklendi", null, Variables.uname);

            } catch (SQLException e) {
                System.out.println("Bir hata oluştu:" + e);
            }
        }

        if (existingProfiles == 0 && registeredProfiles != 0) {
            Dialogs.infoBox("Başarıyla kaydedilen cihaz sayısı: " + registeredProfiles, "Başarılı");
        } else {
            Dialogs.infoBox("Zaten kayıtlı olan ve tekrar kaydedilmeyen profil sayısı: " + existingProfiles + "\n\nBaşarıyla kaydedilen profil sayısı: " + registeredProfiles, "Uyarı");
        }
    }
}

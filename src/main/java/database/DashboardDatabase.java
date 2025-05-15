
package database;

import config.Dialogs;
import config.Variables;
import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.time.*;  
import java.util.*;

/**
 *
 * @author NUN
 */
public class DashboardDatabase extends DBConnection{
    static Statement st = null; 
    static Connection cn = null;
    static ResultSet rs = null;
    LogDatabase log = new LogDatabase();
    Variables vary = new Variables();
    
    public DashboardDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }
    
    public void updateAccount(String username, String newpassword, String oldpassword) {
        try {
            if(!oldpassword.equals(Variables.pword)) {
                Dialogs.infoBox("Girilen eski şifre yanlış!", "Yanlış Şifre");
                return;
            }
            st = cn.createStatement();
            
            String query = "UPDATE users SET password='"+newpassword+"', username='"+username+"'WHERE userid='" + Variables.id + "'";
            st.executeUpdate(query);
            Variables.uname = username;
            Variables.pword = newpassword;
            
            Dialogs.infoBox("Değişiklikler uygulandı!", "Başarılı");
        } catch (SQLException e) {
            System.out.println("Verileri güncelleme sırasında bir hata oluştu:" + e);
        }
    }
    
    public int createAccount(String username, String newpassword, String permission, String mainadminpassword) {
        try {
            st = cn.createStatement();
            String query = "Select * from users where perm='main admin' and password='"+mainadminpassword+"'";
            rs = st.executeQuery(query);
            if(!rs.next()){
                Dialogs.infoBox("Girilen ana admin şifresi yanlış!", "Yanlış Şifre");
                return 0;
            }
            st = cn.createStatement();
            query = "INSERT INTO application.users (username, password, perm, dateofcreation) VALUES ('"+username+"', '"+newpassword+"', '"+permission+"', '"+LocalDate.now()+"');";
            st.executeUpdate(query);
            return 1;
        } catch (SQLException e) {
            System.out.println("Verileri ekleme sırasında bir hata oluştu:" + e);
        }
        return 0;
    }
    
    /*A sub-procedure to return the current registered accounts as an array list */
    public List<String[]> getAccounts() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="Select * From users ";
            
            /*Getting the result of the query search in database
            and it returns the accounts stored in database.*/
            rs = st.executeQuery(query);
            
            /*The following process adds each account stored in
            databaseinto the dynamic array list initialized before*/
            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String date = rs.getString("dateofcreation");
                values.add(new String[] {username,password,date});
            }
        } catch (SQLException e) {
            
            /*If the program faces an error, it prints the error.*/
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public List<String[]> getInventory(String searchTerm, String categoryFilter, String deviceFilter, String statusFilter,
            String assignedProfileFilter,String warrantyFilter, String remainingWarrantyFilter) {
        List<String[]> values = new ArrayList<>();
        try { // Exception Handling
            String searchterm = searchTerm != null ? "(LOCATE('"+searchTerm+"',deviceID) > 0 OR LOCATE('"+searchTerm+"',category) > 0 OR "
                    + "LOCATE('"+searchTerm+"',deviceName) > 0 OR LOCATE('"+searchTerm+"',status) > 0 OR LOCATE('"+searchTerm+"',assignedProfile) > 0)" : "";
            String ctagory = (categoryFilter!= null) && (!categoryFilter.equals("Tümünü Seç")) ? " AND category='"+categoryFilter+"'" : "";
            String dvice = (deviceFilter!= null) && (!deviceFilter.equals("Tümünü Seç")) ? " AND deviceName='"+deviceFilter+"'" : "";
            String stats = (statusFilter!= null) && (!statusFilter.equals("Tümünü Seç"))? " AND status='"+statusFilter+"'" : "";
            String assignedPrfile = (assignedProfileFilter!= null) && (!assignedProfileFilter.equals("Tümünü Seç"))? " AND assignedProfile='"+assignedProfileFilter+"'" : "";
            String wrranty = (warrantyFilter!= null) && (!warrantyFilter.equals("Tümünü Seç"))? " AND warranty='"+warrantyFilter+" '" : "";
            String warrantyfromremaining = (remainingWarrantyFilter!=null) && 
                    (!remainingWarrantyFilter.equals("Tümünü Seç")) ? String.valueOf(LocalDate.now().plusDays(Integer.valueOf(remainingWarrantyFilter))): "";
            String warrantyfromrmainingwarranty = (remainingWarrantyFilter!= null) &&
                    (!wrranty.equals("")) && (!remainingWarrantyFilter.equals("Tümünü Seç"))? " AND warranty='"+warrantyfromremaining+"'" : "";
            /* Initialising the neccessary variables for searching and filtering the devices. */
            st = cn.createStatement(); // Using the connection created with the MYSQL databse to fetch the data according to the filters and searching.
            String query="SELECT * FROM application.inventory WHERE"+ searchterm + ctagory + dvice + stats + assignedPrfile + wrranty + warrantyfromrmainingwarranty;
            rs = st.executeQuery(query);
                // Passing each returned data to an ArrayList
            while (rs.next()) {
                String ID = rs.getString("deviceID"); 
                String category = rs.getString("category"); 
                String device = rs.getString("deviceName"); 
                String status = rs.getString("status"); 
                String assignedProfile = rs.getString("assignedProfile"); 
                String warranty = rs.getDate("warranty").toString();
                Period duration = Period.between(LocalDate.now(), rs.getDate("warranty").toLocalDate());
                String remainingWarranty = String.valueOf(duration.getDays() + duration.getMonths()*30 + duration.getYears()*12*30);
                values.add(new String[] {ID,category,device,status,assignedProfile,warranty, remainingWarranty});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    /*Another sub-procedure that returns all the items stored in database as an array list of String arrays*/
    public List<String[]> getInventoryFull() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.inventory";
            
            /*Getting the result of the query search in database and it returns each item found in database.*/
            rs = st.executeQuery(query);
            
            /*The following process adds each item stored in database into the dynamic array list initialized before*/
            while (rs.next()) {
                String ID = rs.getString("deviceID");
                String category = rs.getString("category");
                String device = rs.getString("deviceName");
                String status = rs.getString("status");
                String assignedProfile = rs.getString("assignedProfile");
                String warranty = rs.getDate("warranty").toString();
                Period duration = Period.between(LocalDate.now(), rs.getDate("warranty").toLocalDate());
                String remainingWarranty = String.valueOf(duration.getDays()
                        + duration.getMonths() * 30 + duration.getYears() * 12 * 30);
                values.add(new String[]{ID, category, device, status, assignedProfile, warranty, remainingWarranty});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public List<String[]> getProfiles(String searchTerm, String statusFilter) {
        List<String[]> values = new ArrayList<>();
        try {
            String searchterm = searchTerm != null ? "(LOCATE('"+searchTerm+"',profileID) > 0 OR LOCATE('"+searchTerm+"',name) > 0 OR "
                    + "LOCATE('"+searchTerm+"',surname) > 0 OR LOCATE('"+searchTerm+"',status) > 0 OR "
                    + "LOCATE('"+searchTerm+"',identitynumber) > 0 OR LOCATE('"+searchTerm+"',duty) > 0)" : "";
            String stats = (statusFilter!= null) && (!statusFilter.equals("Tümünü Seç"))? " AND status='"+statusFilter+"'" : "";
            
            st = cn.createStatement();
            List<String> c = new ArrayList<>();
            rs = st.executeQuery("SELECT * FROM application.inventory");
            while(rs.next()){
                c.add(rs.getString("assignedProfileID"));
            }
            
            String query="SELECT * FROM application.profiles WHERE"+ searchterm + stats;
            rs = st.executeQuery(query);
                // print data of each row
            while (rs.next()) {
                String ID = rs.getString("profileID"); //tablodaki sütun ismi
                String name = rs.getString("name") + " " + rs.getString("surname"); //tablodaki sütun ismi
                String devicenumber = String.valueOf(Collections.frequency(c, ID));
                String status = rs.getString("status"); //tablodaki sütun ismi
                values.add(new String[] {ID,name,devicenumber,status});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    /*Sub-procedure that returns all the profiles created and stored in database as an array list of String arrays*/
    public List<String[]> getProfilesFull() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            
            List<String> c = new ArrayList<>();
            rs = st.executeQuery("SELECT * FROM application.inventory");
            
            /*The following process adds each item's assignedProfileID stored in inventory database into the
            dynamic array list to calculate the total assigned devices of a profile in the next process*/
            while(rs.next()){
                c.add(rs.getString("assignedProfileID"));
            }
            
            String query="SELECT * FROM application.profiles";
            rs = st.executeQuery(query);
            
            /*The following process adds each profile's information stored in profiles database
            and the frequency of assigned devices of a profile into a dynamic array list */
            while (rs.next()) {
                String ID = rs.getString("profileID");
                String name = rs.getString("name") + " " + rs.getString("surname");
                String status = rs.getString("status");
                String devicenumber = String.valueOf(Collections.frequency(c, ID));
                values.add(new String[] {ID,name,devicenumber,status});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public List<String[]> getCategories(String searchTerm, String propertyNumber) {
        List<String[]> values = new ArrayList<>();
        try {
            String searchterm = searchTerm != null ? "(LOCATE('"+searchTerm+"',categoryID) > 0 OR LOCATE('"+searchTerm+"',name) > 0 OR "
                    + "LOCATE('"+searchTerm+"',propertyNumber) > 0)" : "";
            String propertynumber = (propertyNumber!= null) && (!propertyNumber.equals("Tümünü Seç"))? " AND propertyNumber='"+propertyNumber+"'" : "";
            
            st = cn.createStatement();
            
            List<String> categories = new ArrayList<>();
            rs = st.executeQuery("SELECT * FROM application.inventory");
            while(rs.next()){
                categories.add(rs.getString("category"));
            }
            
            String query="SELECT * FROM application.categories WHERE"+ searchterm + propertynumber;
            rs = st.executeQuery(query);
            
            while (rs.next()) {
                String ID = rs.getString("categoryID");
                String name = rs.getString("name");
                String prprtynumber = rs.getString("propertyNumber"); 
                String regdevicenum = String.valueOf(Collections.frequency(categories, name));
                values.add(new String[] {ID,name,prprtynumber,regdevicenum});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    /*Sub-procedure that returns all the categories created and stored in categories database as an array list of String arrays */
    public List<String[]> getCategoriesFull() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            /* A private ArrayList variable that cannnot be accessed out of this method. */
            List<String> categories = new ArrayList<>();
            rs = st.executeQuery("SELECT * FROM application.inventory");
            
            /* The following process adds each item's category stored in inventory database into the
            dynamic array list to calculate the total registered device of a category in the next process */
            while(rs.next()){
                categories.add(rs.getString("category"));
            }
            
            /* query variable used several times in this class, but they do not mix in favour of being private variables */
            String query="SELECT * FROM application.categories";
            rs = st.executeQuery(query);
            
            /* The following process adds each cateogry's information stored in categories database
            and the frequency of registered devices of a category into a dynamic array list */
            while (rs.next()) {
                /* The following variables are initialised as private, preventing any access from outside of this method */
                String ID = rs.getString("categoryID"); 
                String name = rs.getString("name");
                String prprtynumber = rs.getString("propertyNumber");
                String regdevicenum = String.valueOf(Collections.frequency(categories, name));
                values.add(new String[] {ID,name,prprtynumber,regdevicenum});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public List<String[]> getCategorybyName(String name) {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.categories WHERE name='"+ name +"'";
            rs = st.executeQuery(query);
                // print data of each row
            if(rs.next()) {
                String ID = rs.getString("categoryID"); //tablodaki sütun ismi
                name = rs.getString("name");//tablodaki sütun ismi
                String prprtynumber = rs.getString("propertyNumber"); //tablodaki sütun ismi
                values.add(new String[] {ID,name,prprtynumber});
            }
            
            query="SELECT * FROM application.propertynames WHERE categoryName='"+name+"'";
            rs = st.executeQuery(query);
            if(rs.next()) {
                String p1 = rs.getString("p1"); //tablodaki sütun ismi
                String p2 = rs.getString("p2"); //tablodaki sütun ismi
                String p3 = rs.getString("p3"); //tablodaki sütun ismi
                String p4 = rs.getString("p4"); //tablodaki sütun ismi
                String p5 = rs.getString("p5"); //tablodaki sütun ismi
                String p6 = rs.getString("p6"); //tablodaki sütun ismi
                String p7 = rs.getString("p7"); //tablodaki sütun ismi
                String p8 = rs.getString("p8"); //tablodaki sütun ismi
                String p9 = rs.getString("p9"); //tablodaki sütun ismi
                String p10 = rs.getString("p10"); //tablodaki sütun ismi
                values.add(new String[] {p1,p2,p3,p4,p5,p6,p7,p8,p9,p10});
            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public void addDevices(String deviceName, String category, LocalDate warrantyDate, int warranty, String warrantyType, List<String> properties, List<String> serialNums) {
        try {
            if(warrantyDate==null){
                switch(warrantyType){
                    case("Gün"):
                        warrantyDate = LocalDate.now().plusDays(warranty);
                        break;
                    case("Ay"):
                        warrantyDate = LocalDate.now().plusMonths(warranty);
                        break;
                    case("Yıl"):
                        warrantyDate = LocalDate.now().plusYears(warranty);
                        break;
                    default:
                        Dialogs.infoBox("Lütfen garanti süresi türünü seçiniz!", "Eksik Bilgi!");
                        return;
                }
            }
            
            st = cn.createStatement(); // Using the connection created with MYSQL.
            
            String query1 = "SELECT * FROM application.inventory WHERE deviceName='"+ deviceName +"'";
            rs = st.executeQuery(query1); // Creating a query to get the devices with the name passed through deviceName variable and executing the query.
            if(rs.next()){ // Controlling if there is already a device with the given name and informing the user, if not the loop exits.
                int answer = Dialogs.questionBox(null, "Bu isme sahip bir cihaz zaten kaydedilmiş! Devam etmek istiyor musunuz?", "Veritabanı Hatası!");
                if(answer == 1)
                    return;
            }
            
            /* Generating a String text in a loop for a proper format to pass it to MYSQL as a query to insert the 
            properties coming with the input deviceName, then executing the query. */
            String values = "";
            for(String next : properties){
                if(next!=null && !next.equals("")){
                    values += ", '"+next+"'";
                }else{
                    values += ", NULL";
                }
            }
            String query3 = "INSERT INTO application.properties (deviceName, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) VALUES ('"+deviceName+"'"+values+")";
            String query2 = "SELECT * FROM application.inventory";
            rs = st.executeQuery(query2);
            List<String> serNums = new ArrayList<>();
            while(rs.next()){
                serNums.add(rs.getString("serialNum"));
            }
            
            /* Getting all the serial numbers and looping through each of them
            to generate a query String text to insert the new devices */
            String p = "";
            for(String next : serialNums){
                /* If one or more of the serial numbers entered is already registered,
                the user is informed about that serial number with a diologue box. */
                if(serNums.contains(next) && !next.equals("")){ 
                    Dialogs.infoBox(next+" seri numarası zaten kaydedilmiş! Lütfen kontrol ediniz.", "Hata");
                    return;
                }
                /* If the variable 'next' is not null amd not empty, the device is added to the query. */
                if(next!=null && !next.equals("")){
                    p += "('"+category+"', '"+deviceName+"', 'boşta', '"+warrantyDate+"', '"+next+"'),";
                }
            }
            StringBuffer sb= new StringBuffer(p);
            sb.deleteCharAt(sb.length()-1); // Removing the last character of p String as it has a , at the end due to loop.
            
            st.executeUpdate(query3); // Executing the update with query of properties that was generated previously.
            // Inserting the new devices into database with the correct details.
            st.executeUpdate("INSERT INTO application.inventory (category, deviceName, status, warranty, serialNum) VALUES "+sb+";");
            Dialogs.infoBox("Cihaz(lar) başarıyla kaydedildi!", "Başarılı"); // Informing the user that the process was successful.
            rs = st.executeQuery("SELECT * FROM application.inventory WHERE deviceName='"+deviceName+"'");
            
            /* Initialising an ArrayList for the ids of the new devices and adding each id to the ArrayList. */
            List<String> deviceIDs = new ArrayList<>();
            while(rs.next()){
                deviceIDs.add(String.valueOf(rs.getInt("deviceID")));
            }
      
            /* Creating log registry for each device in the deviceIDs array list with a dedicated method. */
            deviceIDs.forEach(id -> {
                log.addDeviceChange(id, "Cihaz Eklendi", null, Variables.uname);
            });
            
        } catch (SQLException e) {
            System.out.println("Verileri kaydetme sırasında bir hata oluştu:" + e);
            Dialogs.infoBox( e.toString(), "Verileri kaydetme sırasında bir hata oluştu!");
            String deleteproperties = "DELETE FROM application.properties WHERE deviceName='"+deviceName+"'",
                    deleteinventory = "DELETE FROM application.inventory WHERE deviceName='"+deviceName+"'",
                    checkproperties = "SELECT * FROM application.properties WHERE deviceName='"+deviceName+"'",
                    checkinventory = "SELECT * FROM application.inventory WHERE deviceName='"+deviceName+"'";
            try{
                st = cn.createStatement();
                
                rs = st.executeQuery(checkproperties);
                if(rs.next()){
                    st.executeUpdate(deleteproperties);
                }
                
                rs = st.executeQuery(checkinventory);
                if(rs.next()){
                    st.executeUpdate(deleteinventory);
                }
            }catch(SQLException error){
                System.out.println("Kontrol sırasında bir hata oluştu:" + error);
            }
        }
    }
    
    public void addCategory(String categoryName, String[] properties) {
        try {
            st = cn.createStatement();
            
            String query = "SELECT * FROM application.categories WHERE name='"+categoryName+"'";
            rs = st.executeQuery(query);
            if(rs.next()){
                Dialogs.infoBox("Bu isimde bir kategori zaten kaydedilmiş! Başka bir isim giriniz ya da kategorileri kontrol ediniz.", "Veritabanı Hatası!");
                return;
            }
            int propertyNumber = 0;
            for(int pass=0; pass<=8; pass++){
                for(int x=0; x<=8; x++){
                    if((properties[x]==null || properties[x].equals("")) && (properties[x+1]!=null && !properties[x+1].equals(""))){
                        String temp = properties[x];
                        properties[x] = properties[x+1];
                        properties[x+1] = temp;
                    }
                }
            }
            
            String props = "";
            for(String x : properties){
                if(!x.equals("") && x != null){
                    propertyNumber++;
                    props += ", '"+x+"'";
                }else{
                    props += ", NULL";
                }
            }
            
            String date = String.valueOf(LocalDate.now());
            String query1 = "INSERT INTO application.categories (name, propertyNumber, dateofCreation) VALUES ('"+categoryName+"', '"+propertyNumber+"', '"+date+"')";
            String query2 = "INSERT INTO application.propertynames (categoryName, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10) VALUES ('"+categoryName+"'"+props+")";
            
            st.executeUpdate(query1);
            st.executeUpdate(query2);

            Dialogs.infoBox("Kategori başarıyla kaydedildi!", "Başarılı");
            
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
            Dialogs.infoBox( e.toString(), "Verileri kaydetme sırasında bir hata oluştu!");
            String deletepropertynames = "DELETE FROM application.propertynames WHERE categoryName='"+categoryName+"'",
                    deletecategory = "DELETE FROM application.categories WHERE name='"+categoryName+"'",
                    checkpropertynames = "SELECT * FROM application.propertynames WHERE categoryName='"+categoryName+"'",
                    checkcategory = "SELECT * FROM application.categories WHERE name='"+categoryName+"'";
            try{
                st = cn.createStatement();
                
                rs = st.executeQuery(checkpropertynames);
                if(rs.next()){
                    st.executeUpdate(deletepropertynames);
                }
                
                rs = st.executeQuery(checkcategory);
                if(rs.next()){
                    st.executeUpdate(deletecategory);
                }
            }catch(SQLException error){
                System.out.println("Kontrol sırasında bir hata oluştu:" + error);
            }
        }
    }
    
    public List<String[]> getDevicesLog() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.devicelogs ORDER BY devicelogsID DESC";
            rs = st.executeQuery(query);
            while (rs.next()) {
                String ID = rs.getString("deviceID"); //tablodaki sütun ismi
                String name = rs.getString("deviceName"); //tablodaki sütun ismi
                String operation = rs.getString("operation"); //tablodaki sütun ismi
                String parameter = rs.getString("parameter"); //tablodaki sütun ismi
                String admin = rs.getString("admin"); //tablodaki sütun ismi
                String time = rs.getTime("time").toString(); //tablodaki sütun ismi
                String date = rs.getDate("date").toString();
                values.add(new String[] {ID+" | "+name,operation,parameter,admin,time,date});
            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    public List<String[]> getProfilesLog() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.profilelogs ORDER BY profilelogsID DESC";
            rs = st.executeQuery(query);
            while (rs.next()) {
                String ID = rs.getString("profileID"); //tablodaki sütun ismi
                String namesurname = rs.getString("profileNameSurname"); //tablodaki sütun ismi
                String operation = rs.getString("operation"); //tablodaki sütun ismi
                String parameter = rs.getString("parameter"); //tablodaki sütun ismi
                String admin = rs.getString("admin"); //tablodaki sütun ismi
                String time = rs.getTime("time").toString(); //tablodaki sütun ismi
                String date = rs.getDate("date").toString();
                values.add(new String[] {namesurname,operation,parameter,admin,time,date});
            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }
    
    /** This method generates a CSV File containing the information of inventory in the target location
     * with the path passed as a parameter
     * @param path This is the path of target location that the CSV File will be generated.
     */
    public void exportInventory(String path) {
        try { // Try Catch prevents any crash due to errors.
            st = cn.createStatement();
            String query = "SELECT * FROM application.inventory";
            rs = st.executeQuery(query);
            
            File file = new File(path); // The file is created with the path of location and name of the file
            try ( FileOutputStream fos = new FileOutputStream(file);  
                    OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-9"));  
                    BufferedWriter writer = new BufferedWriter(osw)) { // The file is started to be read
                String line = "Cihaz ID,Kategori,İsim,Durum,Atanan Profil,Atanan Profil ID,Garanti Bitiş Tarihi,Seri Numarası";
                writer.append(line); // Appending first line as the titles of the columns
                writer.newLine(); // Starting to a new line
                while (rs.next()) { // Iterating until there isn't any elements left in ResultSet
                    String id = rs.getString("deviceID");
                    String category = rs.getString("category");
                    String deviceName = rs.getString("deviceName");
                    String status = rs.getString("status");
                    String assignedProfile = rs.getString("assignedProfile");
                    String assignedProfileID = rs.getString("assignedProfileID");
                    String warranty = rs.getDate("warranty").toString();
                    String serialNum = rs.getString("serialNum");
                    line = id + "," + category + "," + deviceName + "," + status + "," + assignedProfile + 
                            "," + assignedProfileID + "," + warranty + "," + serialNum;
                    writer.append(line); // Appending each row created with the values returned from database
                    writer.newLine(); // Starting to a new line
                }

            } catch (IOException e) {
                System.out.println(e); // Logging any error to console
            }
            
            // Informing the user about the success and the location of file created
            Dialogs.infoBox("Envanter tablosu CSV olarak aşağıdaki dosya yoluna kaydedildi:\n"+file.getPath(), "");

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    public void exportDevices(String path, List<String> IDs) {
        try {
            st = cn.createStatement();
            if(IDs == null) return;
            String text = " WHERE ";
            for(String ID : IDs){
                if(IDs.size() == 1){
                    text += "deviceID="+ID;
                }else if(IDs.size() > 1){
                    if(IDs.get(IDs.size()-1).equals(ID)){
                        text += "deviceID="+ID;
                    }else{
                        text += "deviceID="+ID+" OR ";
                    }
                }
            }
            String query = "SELECT * FROM application.inventory" + text;
            rs = st.executeQuery(query);
            
            File file = new File(path);
            try ( FileOutputStream fos = new FileOutputStream(file);  
                    OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-9"));  
                    BufferedWriter writer = new BufferedWriter(osw)) {
                String line = "Cihaz ID,Kategori,İsim,Durum,Atanan Profil,Atanan Profil ID,Garanti Bitiş Tarihi,Seri Numarası";
                writer.append(line);
                writer.newLine();
                while (rs.next()) {
                    String id = rs.getString("deviceID");
                    String category = rs.getString("category");
                    String deviceName = rs.getString("deviceName");
                    String status = rs.getString("status");
                    String assignedProfile = rs.getString("assignedProfile");
                    String assignedProfileID = rs.getString("assignedProfileID");
                    String warranty = rs.getDate("warranty").toString();
                    String serialNum = rs.getString("serialNum");
                    line = id + "," + category + "," + deviceName + "," + status + "," + assignedProfile + "," + assignedProfileID + "," + warranty + "," + serialNum;
                    writer.append(line);
                    writer.newLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Dialogs.infoBox(IDs.toString()+"\nID'lerine sahip cihazlar CSV olarak aşağıdaki dosya yoluna kaydedildi:\n"+file.getPath(), "");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void exportProfiles(String path) {
        try {
            st = cn.createStatement();
            String query = "SELECT * FROM application.profiles";
            rs = st.executeQuery(query);
            
            File file = new File(path);
            try ( FileOutputStream fos = new FileOutputStream(file);  
                    OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-9"));  
                    BufferedWriter writer = new BufferedWriter(osw)) {
                String line = "Profil ID,İsim,Soyisim,Durum,Telefon Numarası,Görev,Oluşturulma Tarihi";
                writer.append(line);
                writer.newLine();
                while (rs.next()) {
                    String id = rs.getString("profileID");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String status = rs.getString("status");
                    String identitynumber = rs.getString("identitynumber");
                    String duty = rs.getString("duty");
                    String dateofcreation = rs.getDate("dateofcreation").toString();
                    line = id + "," + name + "," + surname + "," + status + "," + identitynumber + "," + duty + "," + dateofcreation;
                    writer.append(line);
                    writer.newLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Dialogs.infoBox("Profil tablosu CSV olarak aşağıdaki dosya yoluna kaydedildi:\n"+file.getPath(), "");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void exportSomeProfiles(String path, List<String> IDs) {
        try {
            st = cn.createStatement();
            if(IDs == null) return;
            String text = " WHERE ";
            for(String ID : IDs){
                if(IDs.size() == 1){
                    text += "profileID="+ID;
                }else if(IDs.size() > 1){
                    if(IDs.get(IDs.size()-1).equals(ID)){
                        text += "profileID="+ID;
                    }else{
                        text += "profileID="+ID+" OR ";
                    }
                }
            }
            String query = "SELECT * FROM application.profiles" + text;
            rs = st.executeQuery(query);
            
            File file = new File(path);
            try ( FileOutputStream fos = new FileOutputStream(file);  
                    OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-9"));  
                    BufferedWriter writer = new BufferedWriter(osw)) {
                String line = "Profil ID,İsim,Soyisim,Durum,Kimlik Numarası,Görev,Oluşturulma Tarihi";
                writer.append(line);
                writer.newLine();
                while (rs.next()) {
                    String id = rs.getString("profileID");
                    String name = rs.getString("name");
                    String surname = rs.getString("surname");
                    String status = rs.getString("status");
                    String identitynumber = rs.getString("identitynumber");
                    String duty = rs.getString("duty");
                    String dateofcreation = rs.getDate("dateofcreation").toString();
                    line = id + "," + name + "," + surname + "," + status + "," + identitynumber + "," + duty + "," + dateofcreation;
                    writer.append(line);
                    writer.newLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Dialogs.infoBox(IDs.toString()+"\nID'lerine sahip profiller CSV olarak aşağıdaki dosya yoluna kaydedildi:\n"+file.getPath(), "");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void exportCategories(String path) {
        try {
            st = cn.createStatement();
            String query = "SELECT * FROM application.categories";
            rs = st.executeQuery(query);
            
            File file = new File(path);
            try ( FileOutputStream fos = new FileOutputStream(file);  
                    OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-9"));  
                    BufferedWriter writer = new BufferedWriter(osw)) {

                while (rs.next()) {
                    String id = rs.getString("categoryID");
                    String name = rs.getString("name");
                    String propertyNumber = rs.getString("propertyNumber");
                    String dateofcreation = rs.getDate("dateofcreation").toString();
                    String line = id + "," + name + "," + propertyNumber + "," + dateofcreation;
                    writer.append(line);
                    writer.newLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Dialogs.infoBox("Kategori tablosu CSV olarak aşağıdaki dosya yoluna kaydedildi:\n"+file.getPath(), "");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public void exportSomeCategories(String path, List<String> IDs) {
        try {
            st = cn.createStatement();
            if(IDs == null) return;
            String text = " WHERE ";
            for(String ID : IDs){
                if(IDs.size() == 1){
                    text += "categoryID="+ID;
                }else if(IDs.size() > 1){
                    if(IDs.get(IDs.size()-1).equals(ID)){
                        text += "categoryID="+ID;
                    }else{
                        text += "categoryID="+ID+" OR ";
                    }
                }
            }
            String query = "SELECT * FROM application.categories" + text;
            rs = st.executeQuery(query);
            
            File file = new File(path);
            try ( FileOutputStream fos = new FileOutputStream(file);  
                    OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-9"));  
                    BufferedWriter writer = new BufferedWriter(osw)) {

                while (rs.next()) {
                    String id = rs.getString("categoryID");
                    String name = rs.getString("name");
                    String propertyNumber = rs.getString("propertyNumber");
                    String dateofcreation = rs.getDate("dateofcreation").toString();
                    String line = id + "," + name + "," + propertyNumber + "," + dateofcreation;
                    writer.append(line);
                    writer.newLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Dialogs.infoBox(IDs.toString()+"\nID'lerine sahip kategoriler CSV olarak aşağıdaki dosya yoluna kaydedildi:\n"+file.getPath(), "");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
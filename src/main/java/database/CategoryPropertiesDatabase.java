
package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import config.Pair;
import config.Dialogs;

/**
 *
 * @author NUN
 */
public class CategoryPropertiesDatabase extends DBConnection{
    static Statement st = null; //SQL ifadelerini veritabanına göndermek için bu interface’ten oluşturulan nesneler kullanılır.
    static Connection cn = null;//Bu interface, bütün metotları ile veritabanına irtibat kurmak için kullanılır.
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    LoginDatabase ldb = new LoginDatabase();
    
    public CategoryPropertiesDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }
    
    public String[] getCategory(String ID) {
        try {
            st = cn.createStatement();
            
            List<String> categories = new ArrayList<>();
            rs = st.executeQuery("SELECT * FROM application.inventory");
            while(rs.next()){
                categories.add(rs.getString("category"));
            }
            
            String query="SELECT * FROM application.categories WHERE categoryID='"+ ID +"'";
            rs = st.executeQuery(query);
            while(rs.next()){
                String name = rs.getString("name"); //tablodaki sütun ismi
                String propertyNumber = rs.getString("propertyNumber"); //tablodaki sütun ismi
                String dateofcreation = rs.getDate("dateofcreation").toString(); //tablodaki sütun ismi
                String regdevicenum = String.valueOf(Collections.frequency(categories, name));
                String[] values = {name, propertyNumber, dateofcreation, regdevicenum};
                return values;
            }
            
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return null;
    }
    
    public Pair<List<String>, String[]> getCategoryDetails(String categoryName) {
        List<String> regDevices = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query="SELECT * FROM application.inventory WHERE category='"+ categoryName +"'";
            rs = st.executeQuery(query);
            while(rs.next()){
                String deviceName = rs.getString("deviceName");
                if(!regDevices.contains(deviceName)){
                    regDevices.add(deviceName);
                }
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
                
            return new Pair<>(regDevices, propertyNames);
                
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return null;
    }
    
    public void edit(String categoryID, String currentName, String newName) {
        try {
            st = cn.createStatement();
            
            String query="UPDATE application.categories SET name='"+newName+"' WHERE categoryID='"+categoryID+"'";
            String query1="UPDATE application.inventory SET category='"+newName+"' WHERE category='"+currentName+"'";
            String query2="UPDATE application.propertynames SET categoryName='"+newName+"' WHERE categoryName='"+currentName+"'";
            st.executeUpdate(query);
            st.executeUpdate(query1);
            st.executeUpdate(query2);
            
            Dialogs.infoBox("Cihaz ismi "+newName+" olarak güncellendi.", "Başarılı");
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
    
    public String[] getAllCategories(String currentCategoryName) {
        List<String> categoryNames = new ArrayList<>();
        try {
            st = cn.createStatement();
            
            String query="SELECT * FROM application.categories";
            rs = st.executeQuery(query);
            while(rs.next()){
                String name = rs.getString("name");
                categoryNames.add(name);
            }
            String[] names = new String[categoryNames.size()];
            for(int x = 0; x<categoryNames.size();x++){
                if(!categoryNames.get(x).equals(currentCategoryName))
                    names[x] = categoryNames.get(x);
            }
            return names;
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return null;
    }
    
    public void deleteAndReplaceCategory(String ID, String currentCategoryName, String newCategoryName) {
        try {
            st = cn.createStatement();
            
            String query="DELETE FROM application.categories WHERE categoryID='"+ID+"'";
            String query1="DELETE FROM application.propertynames WHERE categoryName='"+currentCategoryName+"'";
            String query2="UPDATE application.inventory SET category='"+newCategoryName+"' WHERE category='"+currentCategoryName+"'";
            st.executeUpdate(query);
            st.executeUpdate(query1);
            st.executeUpdate(query2);
            Dialogs.infoBox("Bu kategori silindi ve bu kategoriye kayıtlı olan bütün cihazlar "+newCategoryName+" kategorisine kaydedildi.", "Başarılı");
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
    }
}

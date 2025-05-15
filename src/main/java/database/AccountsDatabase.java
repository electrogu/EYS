
package database;

import config.Dialogs;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.nio.charset.*;

/**
 *
 * @author NUN
 */
public class AccountsDatabase extends DBConnection{

    static Statement st = null; //SQL ifadelerini veritabanına göndermek için bu interface’ten oluşturulan nesneler kullanılır.
    static Connection cn = null;//Bu interface, bütün metotları ile veritabanına irtibat kurmak için kullanılır.
    static ResultSet rs = null;
    DBConnection con = new DBConnection();
    
    public AccountsDatabase() {
        try {
            cn = DriverManager.getConnection(getURL(), getUsername(), getPassword());
        } catch (SQLException ex) {
            System.out.println("Bağlantı sırasında bir hata oluştu:" + ex);
        }
    }

    public List<String[]> getAccounts() {
        List<String[]> values = new ArrayList<>();
        try {
            st = cn.createStatement();
            String query = "Select * From users ";
            rs = st.executeQuery(query);
            // print data of each row
            while (rs.next()) {
                String ID = rs.getString("userid"); //tablodaki sütun ismi
                String username = rs.getString("username"); //tablodaki sütun ismi
                String password = rs.getString("password"); //tablodaki sütun ismi
                String permission = rs.getString("perm"); //tablodaki sütun ismi
                String date = rs.getString("dateofcreation"); //tablodaki sütun ismi
                values.add(new String[]{ID, username, password, permission, date});

            }
        } catch (SQLException e) {
            System.out.println("Verileri okuma sırasında bir hata oluştu:" + e);
        }
        return values;
    }

    public void exportAccounts(String path, boolean showpassword) {
        try {
            st = cn.createStatement();
            String query = "SELECT * FROM application.users";
            rs = st.executeQuery(query);

            File file = new File(path);
            try ( FileOutputStream fos = new FileOutputStream(file);  OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-9"));  BufferedWriter writer = new BufferedWriter(osw)) {

                while (rs.next()) {
                    String id = rs.getString("userid");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String perm = rs.getString("perm");
                    if(showpassword == false) perm = "****";
                    String dateofcreation = rs.getDate("dateofcreation").toString();
                    String line = id + "," + username + "," + password + "," + perm + "," + dateofcreation;
                    writer.append(line);
                    writer.newLine();
                }

            } catch (IOException e) {
                System.out.println(e);
            }

            Dialogs.infoBox("Hesaplar tablosu CSV olarak aşağıdaki dosya yoluna kaydedildi:\n" + file.getPath(), "");

        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void exportSomeAccounts(String path, List<String> IDs, boolean showpassword) {
        try {
            File file = new File(path);
            List<String> arr = new ArrayList<>();
            try ( FileOutputStream fos = new FileOutputStream(file);  OutputStreamWriter osw = new OutputStreamWriter(fos, Charset.forName("ISO-8859-9"));  BufferedWriter writer = new BufferedWriter(osw)) {
            st = cn.createStatement();
            if (IDs == null) {
                return;
            }
            String text = " WHERE ";
            for (String ID : IDs) {
                if (IDs.size() == 1) {
                    text += "userid=" + ID;
                } else if (IDs.size() > 1) {
                    if (IDs.get(IDs.size() - 1).equals(ID)) {
                        text += "userid=" + ID;
                    } else {
                        text += "userid=" + ID + " OR ";
                    }
                }
            }
            
            String query = "SELECT * FROM application.users" + text;
            rs = st.executeQuery(query);

                while (rs.next()) {
                    String id = rs.getString("userid");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String perm = rs.getString("perm");
                    if(showpassword == false) perm = "****";
                    String dateofcreation = rs.getDate("dateofcreation").toString();
                    String line = id + "," + username + "," + password + "," + perm + "," + dateofcreation;
                    writer.append(line);
                    writer.newLine();
                    arr.add(username);
            }
            
                

            } catch (IOException e) {
                System.out.println(e);
            }

            Dialogs.infoBox(arr.toString() + "\nisimlerine sahip hesaplar CSV olarak aşağıdaki dosya yoluna kaydedildi:\n" + file.getPath(), "");

        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

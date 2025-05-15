
package config;

import com.formdev.flatlaf.*;
import java.io.FileReader;
import java.util.Properties;
import javax.swing.*;
import views.*;

/**
 *
 * @author arif
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String appTheme = "";
        try ( FileReader reader = new FileReader(Variables.configProperties)) {
            Properties properties = new Properties();
            properties.load(reader);
            appTheme = properties.getProperty("appTheme");
        } catch (Exception e) {
            System.out.println(e);
        }
        
        try {
            UIManager.setLookAndFeel(appTheme.equals("light") ? new FlatLightLaf() : appTheme.equals("dark") ? new FlatDarkLaf() : new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println(e);
        }
        (new LoginScreen()).setVisible(true);
    }

}

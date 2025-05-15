
package config;

import java.io.*;
import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * A utility class that reads/saves SMTP settings from/to a properties file.
 * @author 
 *
 */
public class ConfigUtility {
    private File configFile = new File("smtp.properties");
    private Properties configProps;
     
    public Properties loadProperties() throws IOException {
        Dotenv dotenv = Dotenv.load();

        Properties defaultProps = new Properties();
        // sets default properties
        defaultProps.setProperty("mail.smtp.host", dotenv.get("DEFAULT_SMTP_HOST"));
        defaultProps.setProperty("mail.smtp.port", dotenv.get("DEFAULT_SMTP_PORT"));
        defaultProps.setProperty("mail.user", dotenv.get("DEFAULT_SMTP_USER"));
        defaultProps.setProperty("mail.password", dotenv.get("DEFAULT_SMTP_PASSWORD"));
        defaultProps.setProperty("mail.smtp.starttls.enable", "true");
        defaultProps.setProperty("mail.smtp.auth", "true");
         
        configProps = new Properties(defaultProps);
         
        // loads properties from file
        if (configFile.exists()) {
            InputStream inputStream = new FileInputStream(configFile);
            configProps.load(inputStream);
            inputStream.close();
        }
         
        return configProps;
    }
     
    public void saveProperties(String host, String port, String user, String pass) throws IOException {
        configProps.setProperty("mail.smtp.host", host);
        configProps.setProperty("mail.smtp.port", port);
        configProps.setProperty("mail.user", user);
        configProps.setProperty("mail.password", pass);
        configProps.setProperty("mail.smtp.starttls.enable", "true");
        configProps.setProperty("mail.smtp.auth", "true");
         
        OutputStream outputStream = new FileOutputStream(configFile);
        configProps.store(outputStream, "host setttings");
        outputStream.close();
    }  
}

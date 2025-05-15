
package config;

import java.awt.*;

/**
 *
 * @author NUN
 */
public class Variables {
    /* Global variables that is used throughout the program. */
    public static String id = null; // ID of the current userx
    public static String uname = null; // Username of the current user
    public static String pword = null; // Password of the current user
    public static String perm = null; // Permission of the current user
    public static String dateofcreation = null; // Creation date of the current user
    public static Image icon = Toolkit.getDefaultToolkit().createImage(
    Variables.class.getClassLoader().getResource("icon.png"));
    public static String configProperties = "src/main/resources/config.properties"; // this is gonna be updated with 'resources' rules
}

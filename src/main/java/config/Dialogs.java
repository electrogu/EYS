
package config;

import java.awt.Component;
import javax.swing.*;

/**
 *
 * @author NUN
 */
public class Dialogs {
    
    /** This method shows a message dialog with the properties passed through parameters.
     * @param infoMessage This is the message that will be showed in the dialog.
     * @param titleBar This is the title that will be showed in the title bar.
    */
    public static void infoBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
    
    /** This method shows an error dialog with the properties passed through parameters.
     * @param infoMessage This is the error message that will be showed in the dialog.
     * @param titleBar This is the title that will be showed in the title bar.
    */
    public static void errorBox(String infoMessage, String titleBar)
    {
        JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.ERROR_MESSAGE);
    }
    
    /** This method returns the answer of the user to the custom option dialog with the properties passed through parameters.
     * The option dialog is shown in the frame passed through parameters.
     * @param frame This is the frame that the dialog will be shown.
     * @param infoMessage This is the message that will be showed in the dialog.
     * @param titleBar This is the title that will be showed in the title bar.
     * @return 0 or 1 accordingly to the user's choice in the option dialog.
    */
    public static int questionBox(Component frame, String infoMessage, String titleBar)
    {
        Object[] options = {"Evet", "Hayır"}; // These are the button names will be shown to user
        int choice = JOptionPane.showOptionDialog(frame, infoMessage, titleBar, 0, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        return choice;
    }
    
    /** This method shows an option dialog with a customisable combo box and properties passed through parameters 
     * and returns the index & the value of the item selected by user.
     * @param choices This is the array of the values that will be shown in the combo box.
     * @param titleBar This is the title that will be showed in the title bar.
     * @param frame This is the frame that the dialog will be shown.
     * @return the index and the value of selected item in combo box as a Pair.
    */
    public static Pair<Integer, Object> comboBox(Component frame, String[] choices, String titleBar)
    {
        Object[] options = {"Seç"}; // This is the ok button name.
        JComboBox<String> combo = new JComboBox<>(choices); // The values are used as the elements of a new combo box created
        int choice = JOptionPane.showOptionDialog(frame, combo, titleBar, 0, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return new Pair<>(choice, combo.getSelectedItem());
    }
}

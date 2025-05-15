
package config;

import database.*;
import java.util.*;

/**
 *
 * @author NUN
 */
public class NotificationUtility {

    NotificationUtilDatabase util = new NotificationUtilDatabase();

    public void showRemainingWarrantiesUnder60() {
        try {
            List<String[]> inventory = util.getInventory(), devices = new ArrayList<>();
            List<String> name = new ArrayList<>(), warranty = new ArrayList<>();
                        
            for (int i = 0; i < inventory.size(); i++) {
                String[] deviceCredentials = inventory.get(i);
                int remainingWarranty = Integer.parseInt(deviceCredentials[2]);
                if (remainingWarranty == 60 || remainingWarranty == 45 || remainingWarranty == 30 || remainingWarranty == 15 || remainingWarranty == 10 || remainingWarranty == 5 || remainingWarranty == 3 || remainingWarranty == 2 || remainingWarranty == 1 || remainingWarranty == 0) {
                    if(!name.contains(deviceCredentials[0]) || !warranty.contains(deviceCredentials[1])){
                        name.add(deviceCredentials[0]);
                        warranty.add(deviceCredentials[1]);
                        devices.add(deviceCredentials);
                    }
                }
            }
            
            if(devices == null) return;
            
            String warrantyWarningText = "Cihaz | Garanti Bitiş Tarihi | Kalan Garanti Süresi\n------------------------------------\n";
                
            for(int i = 0; i < devices.size(); i++){
                warrantyWarningText += "(" + (i+1) + ") " + devices.get(i)[0] + " | " + devices.get(i)[1] + " | " + devices.get(i)[2] + " Gün\n";
            }
            
            Dialogs.infoBox(warrantyWarningText, "Garanti Uyarısı");
        } catch (NumberFormatException err) {
            System.out.println(err);
        }
    }
}

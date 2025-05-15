
package views;

import config.Dialogs;
import config.Variables;
import com.formdev.flatlaf.*;
import javax.swing.UIManager;
import database.*;
import config.*;
import java.awt.*;
import config.Pair;

public class LoginScreen extends javax.swing.JFrame {

    public LoginScreen() {
        initComponents();
        Image icon = Variables.icon;
        this.setIconImage(icon);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        nickname = new javax.swing.JTextField();
        loginButton = new javax.swing.JButton();
        password = new javax.swing.JPasswordField();
        passwordshowhide = new javax.swing.JRadioButton();
        connectionText = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        settings = new javax.swing.JMenu();
        databaseSettings = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("EYS");
        setResizable(false);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Kullanıcı Adı");

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Şifre");

        nickname.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        nickname.setToolTipText("");
        nickname.setActionCommand("<Not Set>");
        nickname.setName(""); // NOI18N
        nickname.setOpaque(false);
        nickname.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nicknameActionPerformed(evt);
            }
        });

        loginButton.setText("Giriş");
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        password.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        passwordshowhide.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordshowhideActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(loginButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nickname)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(passwordshowhide)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(nickname, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(passwordshowhide)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(password, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(loginButton))
        );

        nickname.getAccessibleContext().setAccessibleName("");

        connectionText.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        connectionText.setText("Çevrimdışı - Veritabanına Bağlanılmadı");

        settings.setText("Ayarlar");

        databaseSettings.setText("Veritabanı Konfigürasyonu");
        databaseSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                databaseSettingsActionPerformed(evt);
            }
        });
        settings.add(databaseSettings);

        jMenuBar1.add(settings);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(connectionText)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(71, 71, 71)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(93, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(connectionText, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel2))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void nicknameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nicknameActionPerformed
    }//GEN-LAST:event_nicknameActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        Dashboard screens = new Dashboard();
        LoginDatabase ldb = new LoginDatabase();
        NotificationUtility notification = new NotificationUtility();

        /*Initializing Variables*/
        String uname = nickname.getText();
        String pword = password.getText();
        String ID = null;
        String perm = null;
        String dateofcreation = null;

        Pair<String, Color> connectionInfo = ldb.checkConnection();
        if (!connectionInfo.getKey().equals("Veritabanı Bağlantısı Kuruldu")) {
            if ("admin".equals(uname) && "admin".equals(pword)) {
                ID = "offline";
                pword = "admin";
                uname = "admin";
                perm = "Ana Admin";
                dateofcreation = "null";
            } else {
                /*if password and username are wrong then show a Dialogs of error to the user*/
                Dialogs.infoBox("The username and password are not correct!", "Error!");
            }
        } else {
            /*Assigning the result of authorization method from LoginDatabase class into authorization array*/
            String[] authorization = ldb.authorization(uname, pword);
            ID = authorization[0];
            perm = authorization[1];
            dateofcreation = authorization[2];
        }

        /*Controling the ID returned from authorization*/
        if (ID != null) {
            /*if the ID is not null then show the Dashboard class and set global Variables in Variables class*/
            screens.setVisible(true);
            this.setVisible(false);
            Variables.id = ID;
            Variables.pword = pword;
            Variables.uname = uname;
            Variables.perm = perm;
            Variables.dateofcreation = dateofcreation;
            if (connectionInfo != null) {
                notification.showRemainingWarrantiesUnder60();
            }
        } else {
            /*if ID is null then show a Dialogs of error to the user*/
            Dialogs.infoBox("Girilen kullanıcı adı ve/veya şifre sistemde bulunamadı!", "Kullanıcı bulunamadı!");
        }
    }//GEN-LAST:event_loginButtonActionPerformed

    private void passwordshowhideActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordshowhideActionPerformed
        /*Show the password entered if radio button is selected but hide if not*/
        if (passwordshowhide.isSelected()) {
            password.setEchoChar((char) 0);
        } else {
            password.setEchoChar('\u2022');
        }
    }//GEN-LAST:event_passwordshowhideActionPerformed

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        LoginDatabase ldb = new LoginDatabase();
        /*Control if there is a successful database connection and set text accordingly*/
        Pair<String, Color> connectionInfo = ldb.checkConnection();
        if (connectionInfo == null) {
            return;
        }
        connectionText.setText(connectionInfo.getKey());
        connectionText.setForeground(connectionInfo.getValue());
    }//GEN-LAST:event_formComponentShown

    private void databaseSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_databaseSettingsActionPerformed
        DatabaseProperties dbprops = new DatabaseProperties(this);
        dbprops.setVisible(true);
    }//GEN-LAST:event_databaseSettingsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {

        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                (new LoginScreen()).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel connectionText;
    private javax.swing.JMenuItem databaseSettings;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton loginButton;
    private javax.swing.JTextField nickname;
    private javax.swing.JPasswordField password;
    private javax.swing.JRadioButton passwordshowhide;
    private javax.swing.JMenu settings;
    // End of variables declaration//GEN-END:variables
}

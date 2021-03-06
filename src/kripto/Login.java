/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author miroslav.mandic
 */
public class Login extends javax.swing.JDialog {

    /**
     * Creates new form Login
     */
    public Login(java.awt.Frame parent, boolean modal) {
        super(parent, modal);        
        initComponents();
        txtPrivateKeyPath.setEditable(false);
    }
    

    /**
     * This method is called from within the constructor to  the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnGetPrivateKey = new javax.swing.JButton();
        txtPrivateKeyPath = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jToggleButton1.setText("LOGIN");
        jToggleButton1.setActionCommand("btnLogin");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        txtUsername.setName("txtUsername"); // NOI18N

        txtPassword.setName("txtPassword"); // NOI18N

        jLabel1.setText("Username");

        jLabel2.setText("Password");

        btnGetPrivateKey.setText("Get private key");
        btnGetPrivateKey.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetPrivateKeyActionPerformed(evt);
            }
        });

        txtPrivateKeyPath.setName("txtPrivateKeyPath"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(208, Short.MAX_VALUE)
                .addComponent(jToggleButton1)
                .addGap(196, 196, 196))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(btnGetPrivateKey)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPrivateKeyPath))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPassword)
                            .addComponent(txtUsername))))
                .addGap(32, 32, 32))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGetPrivateKey)
                    .addComponent(txtPrivateKeyPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addComponent(jToggleButton1)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
//        new Poruka("Uspjesno ste se ulogovali", "Info", "");
//                        this.dispose();
//                        MainForm mainForm = new MainForm();
//                        mainForm.setLocationRelativeTo(null);
//                        mainForm.setVisible(true);
    
        boolean uslov = true;

        if ("".equals(txtPrivateKeyPath.getText())) {
            new Poruka("Morate ucitati privatni kljuc", "Info", "Info");
        } else {
            for (User u : Main.KORISNICI) {
                if (u.login(txtUsername.getText(), new String(this.txtPassword.getPassword()))) {
                    
                    

                    Main.KORISNIK = u;
                    if(u.getSertifikat().isRevoked())
                        new Poruka("Sertifikat ovog korisnika je povucen iz upotrebe!", "Info", "Info");
                    else if(!u.getSertifikat().isValidOnDate())
                        new Poruka("Sertifikat ovog korisnika je istekao!", "Info", "Info");
                    else if (!u.keyMatch(txtPrivateKeyPath.getText())) {
                        new Poruka("Privatni kljuc ne odgovara vasem sertifikatu!", "ERROR", "ERROR");
                    } else {
                        //new Poruka("Uspjesno ste se ulogovali", "Info", "Info");
                        this.dispose();
                        MainForm mainForm = new MainForm();
                        mainForm.setLocationRelativeTo(null);
                        mainForm.setVisible(true);
                    }

                    uslov = false;
                    break;
                }
            }
            if (uslov) {
                new Poruka("Korisnicko ime ili pasword nisu ispravni", "Info", "Info");
            }
        }
//
//
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void btnGetPrivateKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetPrivateKeyActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("*.der","der"));
        fileChooser.setCurrentDirectory(new File ("src//privatniKljucevi"));
        StringBuilder sb = new StringBuilder();
        try {
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File privatniKljuc = fileChooser.getSelectedFile();
                txtPrivateKeyPath.setText(privatniKljuc.getAbsoluteFile().toString());

            } else {
                sb.append("No file was selected");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 // TODO add your handling code here:
    }//GEN-LAST:event_btnGetPrivateKeyActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGetPrivateKey;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtPrivateKeyPath;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}

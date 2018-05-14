/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

import java.util.HashMap;

/**
 *
 * @author miroslav.mandic
 */
public class Main {
    
    //public File PRIVATE_KEY_FILE;
    public static User KORISNIK;
    public static User [] KORISNICI;
    public static HashMap <String,Message> NIZ_PORUKA;
    
    
    public static void main (String [] args){
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        public void run() {
            Helper.serijalizujPoruke();
        }
    }, "Shutdown-thread"));
        
        KORISNICI = new User().readUsers();
        NIZ_PORUKA = new HashMap<String,Message>();
        Helper.ucitajSerijalizovanePoruke();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Login dialog = new Login(new javax.swing.JFrame(), true);
                dialog.setLocationRelativeTo(null);
                
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
        //Steganografija.main(args);
        
    }
}

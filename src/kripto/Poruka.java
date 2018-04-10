/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

import javax.swing.JOptionPane;

/**
 *
 * @author miroslav.mandic
 */
public class Poruka {
    
    //error
    //info
    //bilo sta = warrning
       
    public Poruka(String message,String titleBar,String vrsta){
        int a =0;
        if ("error".equalsIgnoreCase(vrsta)) a = JOptionPane.ERROR_MESSAGE;
        else if ("info".equalsIgnoreCase(vrsta)) a = JOptionPane.INFORMATION_MESSAGE;
        else a = JOptionPane.WARNING_MESSAGE;
        JOptionPane.showMessageDialog(null, message,titleBar,a);
    }
    
}

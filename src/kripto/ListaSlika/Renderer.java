/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto.ListaSlika;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Miso
 */
public class Renderer extends DefaultListCellRenderer implements ListCellRenderer<Object>
{
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        //ASSIGN TO VALUE THAT IS PASSED
        Slika is=(Slika) value;

        //setText(is.getName());
        setIcon(is.getSlika());

        //SET BACKGROUND AND FOREGROUND COLORS TO CUSTOM LIST ROW
        if(isSelected)
        {
            setBackground(list.getSelectionBackground());
         setForeground(list.getSelectionForeground());
        }else
        {

            setBackground(list.getBackground());
         setForeground(list.getForeground());
        }

        setEnabled(true);
        setFont(list.getFont());

         return this;
}
    
}

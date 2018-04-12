/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto.ListaSlika;

import javax.swing.Icon;

/**
 *
 * @author Miso
 */
public class Slika {
    
    private String path;
    private Icon slika;
    
    public Slika (){}
    
    public Slika (String path, Icon icon){
        this.path = path;
        this.slika = icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Icon getSlika() {
        return slika;
    }

    public void setSlika(Icon slika) {
        this.slika = slika;
    }
    
    

    
    
}

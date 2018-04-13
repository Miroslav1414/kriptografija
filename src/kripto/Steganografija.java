
package kripto;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author miroslav.mandic
 */
public class Steganografija {
    
    BufferedImage image;
    
    public Steganografija(){}
    
    public Steganografija(File slika){
        try {
            image = ImageIO.read(slika);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    //karakter u niz od osam bajtova koji sadrzi samo po jedan bit
    public byte[] convertCharToBit(char ch){
        byte rez [] = new byte[8];
        int brojac = 8;
            while (ch > 0)
            {
                rez[--brojac] = (byte)(ch % 2);
                ch /= 2;
            }
        return rez;
    }
    
    //string u bitove - odnosno niz bajtova po bit
    public byte[] nizCharovaUnizBita(String tekst){
        byte[] tekstUBajtovima = new byte[tekst.length() * 8];
        int brojac = 0;
        for (char ch : tekst.toCharArray()) {
            System.arraycopy(convertCharToBit(ch), 0, tekstUBajtovima, brojac, 8);
            brojac += 8;
        }
        return tekstUBajtovima;
    }
    
   
    public void upisiBiteUSliku(byte [] tekst){
        //duzina teksta koji se upisuje, odnosno niza  mora biti djeljiva sa 3
        int brojac = 0;
        for(int i=0;i<image.getWidth() ;i++){
            for(int j=0 ;j<image.getHeight();j++){
                if(brojac == tekst.length) break;
                int pixel = image.getRGB(i, j);               
                
                int alpha = (pixel >> 24)& 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                red = (tekst[brojac] == 1) ? (byte) (red | 1) : (byte) (red & ~1);
                green = (tekst[brojac+1] == 1) ? (byte) (green | 1) : (byte) (green & ~1);
                blue = (tekst[brojac+2] == 1) ? (byte) (blue | 1) : (byte) (blue & ~1);
                
                //ovde je greska
                int value = alpha | red << 16 | green << 8 | blue;
                image.setRGB(i, j, value);
                brojac +=3;
            }
        }
        try {
            ImageIO.write(image, "png", new File("C:\\Users\\miroslav.mandic\\Desktop\\45.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void kodovanje(String tekst,String primalac){
        byte [] tekstBiti = nizCharovaUnizBita(tekst);
        byte [] primalacBiti = nizCharovaUnizBita(primalac);
        int minimalnaVelicinaSLike = tekstBiti.length + primalacBiti.length + 512;
        
    }
    
    public static void read(BufferedImage im){
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 8;j++){
        int pixel = im.getRGB(i, j);
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;
                System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
            }
        }
    }
    
    public static void main(String [] args){
        try {
      // get the BufferedImage, using the ImageIO class
      read(ImageIO.read(new File("C:\\Users\\miroslav.mandic\\Desktop\\456.png")));
      Steganografija a  = new Steganografija(new File("C:\\Users\\miroslav.mandic\\Desktop\\456.png"));
      a.upisiBiteUSliku(a.nizCharovaUnizBita("asd"));
      read(ImageIO.read(new File("C:\\Users\\miroslav.mandic\\Desktop\\45.png")));
      
      
      
      
      
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
    }
    
}

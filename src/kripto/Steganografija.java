
package kripto;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.imageio.ImageIO;
import kripto.ListaSlika.Slika;

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
    
   //tekst predstavlja niz bajtova gdje svaki bajt sardzi samo jedan bit teksta koji treba da se upise
    public void upisiBiteUSliku(byte [] tekst,String putanja){
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

                red = (tekst[brojac] == 1) ?  (red | 1) : (red & ~1);
                green = (tekst[brojac+1] == 1) ?  (green | 1) : (green & ~1);
                blue = (tekst[brojac+2] == 1) ?  (blue | 1) :  (blue & ~1);
                
                int value = 0xFF000000+(red << 16) + (green << 8) + (blue);
                image.setRGB(i, j, value);
                brojac +=3;
            }
        }
        try {
            //ImageIO.write(image, "png", new File(putanja));
            ImageIO.write(image, "png", new File("C:\\Users\\miroslav.mandic\\Desktop\\45.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void kodovanje(String tekst,String primalac){
        try{
        PrivateKey  privateKey = Main.KORISNIK.getPrivateKey();
        Cipher sifrat = Cipher.getInstance("RSA");
        sifrat.init(Cipher.ENCRYPT_MODE, privateKey);

        byte[] kriptovanTekst = sifrat.doFinal(tekst.getBytes("UTF-8"));
        
        //9*8 za smjestanje usenrame primaoca
        byte [] tekstZaUpis = new byte[9*8 + kriptovanTekst.length];
        tekstZaUpis = Arrays.copyOfRange(primalac.getBytes("utf-8"), 0, 9*8);
        tekstZaUpis = Arrays.copyOfRange(tekstZaUpis, 9*8, tekstZaUpis.length);
        
        Sertifikat sert = new Sertifikat(Helper.SERTIFIKATI + primalac + ".der");
        PublicKey publicKey = sert.getPublicKey();
        sifrat.init(Cipher.ENCRYPT_MODE, publicKey);
                
        byte [] kriptovanTekstZaUpis = sifrat.doFinal(tekstZaUpis);
        if(kriptovanTekstZaUpis.length > (2^21)){
            new Poruka("Vasa poruka ne moze da sadrzi toliko slova","ERROR","ERROR");
        }
        else
        {
            int minimalnaVelicinaSLike = 8 * 3 + kriptovanTekstZaUpis.length * 8;
            if ((minimalnaVelicinaSLike % 3) == 1) {
                minimalnaVelicinaSLike += 2;
            } else if ((minimalnaVelicinaSLike % 3) == 2) {
                minimalnaVelicinaSLike++;
            }
            if (minimalnaVelicinaSLike > ((image.getHeight() + image.getWidth()) * 3))
                new Poruka("Slika nije dovoljne velicine da bi se moglgla skladistiti data poruka u nju."
                        + " Ili izaberite vecu sliku ili smanjite kolicinu teksta u poruci", "Error", "Error");
            else{
                
                byte [] nizZaUpis = nizCharovaUnizBita(new String(kriptovanTekstZaUpis,"utf-8"));
                byte [] duzinaPoruke = nizCharovaUnizBita(String.valueOf(kriptovanTekstZaUpis.length));
                nizZaUpis = Arrays.copyOfRange(duzinaPoruke,0, 24);
                nizZaUpis = Arrays.copyOfRange(kriptovanTekstZaUpis,24,nizZaUpis.length);
                
                upisiBiteUSliku(nizZaUpis,"");
                
            }
        }
        
        }
        catch(Exception  e){e.printStackTrace();}
        
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
      a.upisiBiteUSliku(a.nizCharovaUnizBita("asd"),"");
      read(ImageIO.read(new File("C:\\Users\\miroslav.mandic\\Desktop\\45.png")));

      
      
      
      
      
    } catch (IOException e) {
      e.printStackTrace();
    }
    }
    
}


package kripto;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
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
    
    //proslijedjuje se slika u koju ce se upisati podaci
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
        char cc = ch;
        try{
        int brojac = 8;
            while (ch > 0)
            {
                rez[--brojac] = (byte)(ch % 2);
                ch /= 2;
            }
        }
        catch(Exception e){
//            System.out.println((int)cc);
//            for(byte a: rez)
//            System.out.print(a);
//            System.out.println("==================");
            e.printStackTrace();
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
    
    //niz bajtova u niz bajtova po 1 bit
    public byte[] nizCharovaUnizBita(byte[] tekst){
        byte[] tekstUBajtovima = new byte[tekst.length * 8];
        int brojac = 0;
        int temp = 0;
        for (byte ch : tekst) {
            
            if ( (int)ch <0) 
            {
                temp = ch;
                System.arraycopy(convertCharToBit((char)(temp+256)), 0, tekstUBajtovima, brojac, 8);
            }
            else
                System.arraycopy(convertCharToBit((char)ch), 0, tekstUBajtovima, brojac, 8);
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
            ImageIO.write(image, "png", new File(putanja));
            //ImageIO.write(image, "png", new File("C:\\Users\\miroslav.mandic\\Desktop\\45.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public byte[] obrniNiz(byte [] niz){
        byte [] rez = new byte[niz.length];
        int brojac = 0;
        for(int i =niz.length -1  ; i>=0 ; i--)
            rez[brojac++]= niz[i];
        return rez;
    }
    
    public void kodovanje(String tekst,String primalac){
        try{
        PrivateKey  privateKey = Main.KORISNIK.getPrivateKey();

        //potpisivanje
        Signature potpis = Signature.getInstance("SHA256withRSA");
        potpis.initSign(privateKey);
        potpis.update(Helper.stringToByte(tekst));

        byte[] kriptovanTekst = potpis.sign();
        //System.out.println(Helper.byteToString(kriptovanTekst));

        //return Base64.getEncoder().encodeToString(signature);AAA
        
        //9*8 za smjestanje usenrame primaoca
        byte [] tekstZaUpis = new byte[9*8 + kriptovanTekst.length];
        System.arraycopy(Helper.stringToByte(primalac),0,tekstZaUpis,0,9*8);
        //tekstZaUpis = Arrays.copyOfRange(Helper.stringToByte(primalac), 0, 9*8);
        System.arraycopy(kriptovanTekst,0,tekstZaUpis,9*8,tekstZaUpis.length);
        //tekstZaUpis = Arrays.copyOfRange(kriptovanTekst, 9*8, tekstZaUpis.length);
        
        Sertifikat sert = new Sertifikat(Helper.SERTIFIKATI + primalac + ".der");
        PublicKey publicKey = sert.getPublicKey();
        
        Cipher sifrat = Cipher.getInstance("RSA");
        sifrat.init(Cipher.ENCRYPT_MODE, publicKey);
        System.out.println(Helper.byteToString(tekstZaUpis));
                
        byte [] kriptovanTekstZaUpis = sifrat.doFinal(tekstZaUpis);
        if(kriptovanTekstZaUpis.length > (2097152)){
            //System.out.println(Helper.byteToString(kriptovanTekstZaUpis));
            new Poruka("Vasa poruka ne moze da sadrzi toliko slova. Maksimalna duzina teksta je 2097152 slova." ,"ERROR","ERROR");
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
                new Poruka("Slika nije dovoljne velicine da bi se moglg skladistiti data poruka u nju."
                        + " Ili izaberite vecu sliku ili smanjite kolicinu teksta u poruci", "Error", "Error");
            else{
                byte [] nizZaUpis = new byte[minimalnaVelicinaSLike];
                byte [] duzinaPoruke = nizCharovaUnizBita(String.valueOf(kriptovanTekstZaUpis.length));
                nizZaUpis = Arrays.copyOfRange(obrniNiz(duzinaPoruke),0, 24);
                for(byte b : obrniNiz(duzinaPoruke))
                    System.out.print(Byte.toString(b));
                //nizZaUpis = Arrays.copyOfRange(nizCharovaUnizBita(Helper.byteToString(kriptovanTekstZaUpis)),24,nizZaUpis.length);
                nizZaUpis = Arrays.copyOfRange(nizCharovaUnizBita(kriptovanTekstZaUpis),24,nizZaUpis.length);
                
                boolean jedinstvenoIme  = true;
                String imeFajla;
                do{
                     imeFajla= Helper.getRandomString(20);
                    File  [] sveSlikeKriptovane = new File("src//slike_kriptovane").listFiles();
                    for(File a : sveSlikeKriptovane)
                        if(a.getName().equals(imeFajla))
                            jedinstvenoIme = false;
                }
                while(!jedinstvenoIme);
                upisiBiteUSliku(nizZaUpis,"src//slike_kriptovane//" + imeFajla + ".png");
                
            }
        }
        
        }
        catch(Exception  e){e.printStackTrace();}
        
    }
    
    public int getDuzinaPoruke(BufferedImage im){
        int rez = 0,brojac = 0;
        for(int i = 0;i<im.getWidth();i++){
            for (int j = 0; j<im.getHeight(); j++){
                
            }
        }
        return rez;
    }
    
    public byte [] citajPixele(int pixel){
        byte [] rez = new byte[3];
        
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;

        rez[0] = (byte)( red%2);
        rez[1] = (byte)( green%2);
        rez[2] = (byte)( blue%2);
        //System.out.println(rez[0] + " " +rez[1] + " " +rez[2] + "---" + red +" " + green + " " + blue);
        return rez;
    }
    
    public void dekodovanje (String putanjaDoslike) {
        int prvih8Bita = 0,i=0,j=0;
        try{
            BufferedImage slikaDekripcija =(ImageIO.read(new File(putanjaDoslike)));
            
            byte [] duzinaPoruke= new byte[24];
            int pozicija = 0;
            for(;i<slikaDekripcija.getWidth();i++)
            {
                for(; j<slikaDekripcija.getHeight();j++){
                    if (prvih8Bita ==8) break;
                    System.arraycopy(citajPixele(slikaDekripcija.getRGB(i, j)),0,duzinaPoruke, pozicija,3);
                    pozicija +=3;
                    prvih8Bita ++;
                }
            }
            
            for(byte b : obrniNiz(duzinaPoruke))
                    System.out.print(Byte.toString(b));
            
            
            
            
        }
        catch(Exception e){e.printStackTrace();
                System.out.println(prvih8Bita);}
        
        
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
//      read(ImageIO.read(new File("C:\\Users\\miroslav.mandic\\Desktop\\456.png")));
//      Steganografija a  = new Steganografija(new File("C:\\Users\\miroslav.mandic\\Desktop\\456.png"));
//      a.upisiBiteUSliku(a.nizCharovaUnizBita("asd"),"");
//      read(ImageIO.read(new File("C:\\Users\\miroslav.mandic\\Desktop\\45.png")));
      
      Steganografija asd  = new Steganografija();
      asd.dekodovanje("src//slike_kriptovane//RAlOTiRlpnyzw6VJJ0PA.png");
//      byte a = (byte)(201%2);
//      byte b = (byte)(200%2);
//      
//      System.out.println(Byte.toString(a)  + "  " + Byte.toString(b));

      
      
      
      
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    }
    
}

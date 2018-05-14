
package kripto;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;
import java.util.Base64;
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
            e.printStackTrace();
        }
            
            
        return rez;
    }
    
//    public String convertBitToString(byte [] niz){
//        String rez = "";
//        for(i=0; i<niz.length ; i+=8)
//            rez += (char)
//        
//        
//        
//        return rez;
//    }
    
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
    
    
    
    public void kodovanje(String tekst,String primalac){
        try{
        //System.out.println(tekst);
        PrivateKey  privateKey = Main.KORISNIK.getPrivateKey();

        //potpisivanje
        Signature potpis = Signature.getInstance("SHA256withRSA");
        potpis.initSign(privateKey);
        potpis.update(Helper.stringToByte(tekst));
        
        byte[] tekstUByte = Helper.stringToByte(tekst);
        byte[] potpisTeksta = potpis.sign();
        byte[] kriptovanTekst = new byte[tekstUByte.length + 256];
        System.out.println("duzina potpisa" + potpisTeksta.length);
        
        //tekst + potpis
        System.arraycopy(tekstUByte, 0, kriptovanTekst, 0, tekstUByte.length);
        System.arraycopy(potpisTeksta, 0, kriptovanTekst, tekstUByte.length, 256);
        byte [] niz64 = kriptovanTekst;       
        
        //broj slova posiljaoca + posiljalac + tekst sa potpisom
        byte [] tekstZaUpis = new byte[1 + Main.KORISNIK.getUsername().length() + niz64.length];
        byte [] brojSlovaPrimaoca = new byte[1];
        brojSlovaPrimaoca = Helper.stringToByte(String.valueOf(Main.KORISNIK.getUsername().length()));
        System.arraycopy(brojSlovaPrimaoca,0,tekstZaUpis,0,1);
        System.arraycopy(Helper.stringToByte(Main.KORISNIK.getUsername()),0,tekstZaUpis,1,Main.KORISNIK.getUsername().length());
        System.arraycopy(niz64,0,tekstZaUpis,(Main.KORISNIK.getUsername().length()+1),niz64.length);

        
        //kreirane des lozinke, kriptovanje niza sa 3DES i kriptovanje des kljuca sa javnim kljucem primaoca
        Sertifikat sert = new Sertifikat(Helper.SERTIFIKATI + primalac + ".der");
        PublicKey publicKey = sert.getPublicKey();
        String desLozinka = Helper.getRandomString(10);
        byte [] desKriptovanTekst = new TripleDES().encrypt(Helper.byteToString(tekstZaUpis), desLozinka);
        Cipher sifrat = Cipher.getInstance("RSA");
        sifrat.init(Cipher.ENCRYPT_MODE, publicKey);
        byte [] lozinka = sifrat.doFinal(Helper.stringToByte(desLozinka));
        
        //duzina lozinke + lozinka + sifrat
        byte duzinaLozinke =0 ;
        switch ( lozinka.length){
            case 64 : duzinaLozinke = 1; break;
            case 128: duzinaLozinke = 2; break;
            case 256: duzinaLozinke = 3; break;
            case 512: duzinaLozinke = 4; break;
            case 1024: duzinaLozinke = 5; break;
            case 2048: duzinaLozinke = 6; break;
        }
        byte [] desLozinkaISifrat = new byte[1 + lozinka.length  + desKriptovanTekst.length];
        desLozinkaISifrat[0]= duzinaLozinke;
        System.arraycopy(lozinka,0,desLozinkaISifrat,1,lozinka.length);
        System.arraycopy(desKriptovanTekst,0,desLozinkaISifrat,lozinka.length + 1 ,desKriptovanTekst.length);
        byte [] kriptovanTekstZaUpis = desLozinkaISifrat;
        
        
        //provjera za duzinu upisanog teksta
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
            if (minimalnaVelicinaSLike > ((image.getHeight() * image.getWidth()) * 3)){
                new Poruka("Slika nije dovoljne velicine da bi se moglg skladistiti data poruka u nju."
                        + " Ili izaberite vecu sliku ili smanjite kolicinu teksta u poruci", "Error", "Error");
                System.out.println("minimalna velicina slike je : " + minimalnaVelicinaSLike + "! U sliku moze stati: " + ((image.getHeight() * image.getWidth()) * 3));
            }
            else{
                //duzina poruke (24 prva bita) + prethodni niz bajtova
                byte [] nizZaUpis = new byte[minimalnaVelicinaSLike];
                byte [] duzinaPoruke = Helper.intToByte(kriptovanTekstZaUpis.length);
                System.arraycopy(duzinaPoruke,0,nizZaUpis,0,24);
                //pretvaranje u niz bita
                byte [] temp = nizCharovaUnizBita(kriptovanTekstZaUpis);
                System.arraycopy(temp,0,nizZaUpis,24,temp.length);
                
                //upis u sliku                
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
                
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(Files.readAllBytes(Paths.get("src//slike_kriptovane//" + imeFajla + ".png")));
                
                Main.NIZ_PORUKA.put(imeFajla,new Message("src//slike_kriptovane//" + imeFajla + ".png", hash, primalac));
                
                
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
        return rez;
    }
    
    public String dekodovanje (String putanjaDoslike) {
        int prvih8Bita = 0,i=0,j=0;
        String rez = "";
        try{
            BufferedImage slikaDekripcija =(ImageIO.read(new File(putanjaDoslike)));
            
            byte [] duzinaPoruke= new byte[24];
            int pozicija = 0;
            
            //cita duzinu upisanih podataka
            petlja_1:
            for(;i<slikaDekripcija.getWidth();i++)
            {
                if (i>0) j=0;
                for(; j<slikaDekripcija.getHeight() ;j++){
                    if (prvih8Bita >=8) break petlja_1;
                    System.arraycopy(citajPixele(slikaDekripcija.getRGB(i, j)),0,duzinaPoruke, pozicija,3);
                    pozicija +=3;
                    prvih8Bita ++;
                }
            }
            int duzinaPorukeInt = Helper.nizBitaUInt(duzinaPoruke)*8;
            int duzinaPorukeIntPocetna = duzinaPorukeInt;
            
            //cita ostale bite
            if (duzinaPorukeInt%3 == 1) duzinaPorukeInt+=2;
            else if (duzinaPorukeInt%3 == 2) duzinaPorukeInt++;
            
            
            pozicija = 0;
            
            byte[] kriptovanTekst = new byte[duzinaPorukeInt];
            
            petlja_2:
            for(;i<slikaDekripcija.getWidth();i++)
            {
                if (i>0) j=0;
                for(; j<slikaDekripcija.getHeight();j++){
                    if (pozicija >= duzinaPorukeInt) break petlja_2;
                    System.arraycopy(citajPixele(slikaDekripcija.getRGB(i, j)),0,kriptovanTekst, pozicija,3);
                    pozicija +=3;
                }
            }
            
//            System.out.println("ucitani tekst duzina:" + kriptovanTekst);
//            for (byte a : kriptovanTekst)
//                System.out.print(a);
            //brise visak            
            kriptovanTekst = Arrays.copyOf(kriptovanTekst, duzinaPorukeIntPocetna);
            
            //pretvara bite u bajte
            byte [] nizBajtova= new byte [duzinaPorukeIntPocetna/8];
            pozicija = 0;
        
            for (int k = 0; k<duzinaPorukeIntPocetna;k+=8){
                nizBajtova[pozicija++] = Helper.nizButaUByte(Arrays.copyOfRange(kriptovanTekst, k, k+8));
            }
            
            //duzina des kljuca se trazi koji je kriptovan javnim kljucem primaoca
                        
            int duzinaLozinkeBit = nizBajtova[0];
            int duzinaLozinke = 0;
            switch (duzinaLozinkeBit){
            case 1 : duzinaLozinke = 64; break;
            case 2: duzinaLozinke = 128; break;
            case 3: duzinaLozinke = 256; break;
            case 4: duzinaLozinke = 512; break;
            case 5: duzinaLozinke = 1024; break;
            case 6: duzinaLozinke = 2048; break;
        }
            byte [] lozinkaZaDes = new byte[duzinaLozinke];
            for (int k =1; k<= duzinaLozinke; k++)
                lozinkaZaDes[k-1] = nizBajtova[k];
            byte [] desKriptovanTekst = Arrays.copyOfRange(nizBajtova, (1+ duzinaLozinke) , nizBajtova.length);
            Cipher sifrat = Cipher.getInstance("RSA");
            PrivateKey privatekey = Main.KORISNIK.getPrivateKey();
            sifrat.init(Cipher.DECRYPT_MODE, privatekey);
            
            byte [] lozinkaZaDesString = sifrat.doFinal(lozinkaZaDes);
            
            byte [] dekriptovanTekst = Helper.stringToByte(new TripleDES().decrypt(desKriptovanTekst, Helper.byteToString(lozinkaZaDesString)));
            
            
            int brojSlovaPrimaoca = dekriptovanTekst[0] - 48;
            String posiljaoc = "";
            for (int k=1 ; k<=brojSlovaPrimaoca; k++)
                posiljaoc += (char)dekriptovanTekst[k];
            
            
            byte [] niz64 = new byte[ dekriptovanTekst.length - 1 - brojSlovaPrimaoca];
            System.arraycopy(dekriptovanTekst, 1+brojSlovaPrimaoca, niz64, 0, niz64.length);
            
            byte[] niz = niz64;
            Sertifikat sert = new Sertifikat(Helper.SERTIFIKATI + posiljaoc + ".der");
            
            
            
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(sert.getPublicKey());
        
        byte [] tekst = new byte[niz.length-256];
        System.arraycopy(niz, 0, tekst, 0, niz.length-256);
        byte [] potpisTeksta = Arrays.copyOfRange(niz, niz.length-256, niz.length);
        
        
        publicSignature.update(tekst);
//        System.out.println(publicSignature.verify(potpisTeksta));
//        System.out.println(Helper.byteToString(tekst));
        if (publicSignature.verify(potpisTeksta))
            rez = Helper.byteToString(tekst);
        else rez = "Poruka je neovlasteno promjenjena.";
  
        }
        catch(Exception e){e.printStackTrace();
                }
        return rez;
        
        
    }
//    
//    public static void read(BufferedImage im){
//        for (int i = 0; i < 1; i++) {
//            for (int j = 0; j < 8;j++){
//        int pixel = im.getRGB(i, j);
//                int alpha = (pixel >> 24) & 0xff;
//                int red = (pixel >> 16) & 0xff;
//                int green = (pixel >> 8) & 0xff;
//                int blue = (pixel) & 0xff;
//                System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
//            }
//        }
//    }
    
//    public static void main(String [] args){
//        try {
//      Steganografija asd  = new Steganografija();
//      asd.dekodovanje("src//slike_kriptovane//rUiVP80mVg2PFT7kmUWs.png");
// 
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
//    }
    
}

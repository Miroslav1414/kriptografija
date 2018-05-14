/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import static java.lang.Math.pow;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import javax.crypto.Cipher;

/**
 *
 * @author miroslav.mandic
 */
public class Helper {
    
    public static final String USER_PATH = "src//root//user.usx";
    public static final String HASH_PASS_PATH = "src//root//hp.hp";
    public static final String HASH_PASS_MSG_PATH = "src//root//msg.hp";
    public static final String SERTIFIKATI = "src//sertifikati//";
    public static final String PORUKE_SERIJALIZOVANE = "src//root//msg.ser";
    
    public Helper(){}
    
    public  static void serijalizujPoruke(){
        try{
            if (Main.NIZ_PORUKA.isEmpty()) {
                new File(PORUKE_SERIJALIZOVANE).delete();
            } else {
                byte[] bajtovi = null;

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(bos);
                try {
                    out.writeObject(Main.NIZ_PORUKA);
                    out.flush();
                    bajtovi = bos.toByteArray();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        bos.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                TripleDES tdes = new TripleDES();
                String passForDes = Helper.getRandomString(20);
                byte[] kriptovanePoruke = tdes.encrypt(byteToString(bajtovi), passForDes);
                Files.write(Paths.get(PORUKE_SERIJALIZOVANE), kriptovanePoruke);

                //hash fajla i kriptovanje kljuca i hasha javnim kljucem CA
                byte[] kriptovanePoruke2 = Files.readAllBytes(Paths.get(PORUKE_SERIJALIZOVANE));
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(kriptovanePoruke2);

                Sertifikat ca = new Sertifikat();
                PublicKey publicKey = ca.CA_CERT.getPublicKey();
                Cipher sifrat = Cipher.getInstance("RSA");
                sifrat.init(Cipher.ENCRYPT_MODE, publicKey);

                byte[] hashCrypted = sifrat.doFinal(hash);
                byte[] lozinkaCrypted = sifrat.doFinal(passForDes.getBytes("utf-8"));

                //upis u fajl 
                byte[] upisUFajl = new byte[hashCrypted.length + lozinkaCrypted.length];
                System.arraycopy(hashCrypted, 0, upisUFajl, 0, hashCrypted.length);
                System.arraycopy(lozinkaCrypted, 0, upisUFajl, hashCrypted.length, lozinkaCrypted.length);

                Files.write(Paths.get(Helper.HASH_PASS_MSG_PATH), upisUFajl);

            }
        }
        catch(Exception e){e.printStackTrace();}
    }
    public  static void ucitajSerijalizovanePoruke(){
               
        try {
            if (!new File(PORUKE_SERIJALIZOVANE).exists() && new File("src//slike_kriptovane").listFiles().length != 0)
                new Poruka("Doslo je do greske prilikom ucitavanja poruka. "
                        + "\nIli je Neko je izbrisao fajl gdje se nalaze podaci o porukama ili je dodao fajlove gdje se kriptovane poruke smjestaju!", "ERROR", "ERROR");
            if (new File(PORUKE_SERIJALIZOVANE).exists()) {
                Sertifikat ca = new Sertifikat();
                PrivateKey privateKey = ca.PRIVATE_KEY;
                Cipher sifrat = Cipher.getInstance("RSA");
                sifrat.init(Cipher.DECRYPT_MODE, privateKey);

                byte[] hashIPass = Files.readAllBytes(Paths.get(Helper.HASH_PASS_MSG_PATH));
                byte[] hashCrypted = Arrays.copyOfRange(hashIPass, 0, 256);
                byte[] passCrypted = Arrays.copyOfRange(hashIPass, 256, hashIPass.length);

                byte[] hash1 = sifrat.doFinal(hashCrypted);
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hash2 = md.digest(Files.readAllBytes(Paths.get(PORUKE_SERIJALIZOVANE)));

                if (!(Arrays.equals(hash1, hash2))) {
                    new Poruka("Poruke ne mogu biti validno ucitane! Neko je izvrsio neovlastenu izmjenu!", "ERROR", "ERROR");
                    System.exit(1);
                }

                String pass = new String(sifrat.doFinal(passCrypted), "UTF-8");

                String poruke = new TripleDES().decrypt(Files.readAllBytes(Paths.get(PORUKE_SERIJALIZOVANE)), pass);

                byte[] porukeByte = stringToByte(poruke);

                ByteArrayInputStream bis = new ByteArrayInputStream(porukeByte);
                ObjectInputStream in = null;
                try {
                    in = new ObjectInputStream(bis);
                    Main.NIZ_PORUKA = (HashMap<String, Message>) in.readObject();

                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }

        }  catch (Exception ex) {
            new Poruka("Poruke nisu dobro ucitane!", "ERROR", "ERROR");
            ex.printStackTrace();
        }
        
    }
    
    
    public static String getRandomString(int duzina){
        char [] nizSlova = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        String rez = "";
        Random rand = new Random();
        for(int i =0;i<duzina;i++){
            rez += nizSlova[rand.nextInt(nizSlova.length -1)];
        }
        return rez;
    }
    
    public static byte[] stringToByte (String str){
        byte[] rez = new byte[str.toCharArray().length];
        int brojac = 0;
        for (char c : str.toCharArray()){
            rez[brojac++] = (byte)c;
        }
        return rez;
    }
    
    public static String byteToString(byte [] niz ){
        String rez = "";
        for(byte a : niz)
            rez += (char)(a);
        return rez;
    }
    
    public static byte nizButaUByte(byte [] niz ){
        byte rez = (byte) (niz[0]* 128  + niz[1] * 64 + niz[2] * 32 + niz[3] *16 + niz[4]*8 + niz[5] * 4 + niz[6] * 2 + niz[7]);
        return rez;
    }
    
    
    public static int nizBitaUInt(byte [] niz){
        int rez = 0;
        byte [] temp = obrniNiz(niz);
        int stepen = temp.length-1;
                for(byte a : temp){
                    rez += pow(2,stepen)* a;
                    stepen --;
                }        
        return rez;
    }
    
    public byte [] nizBitaUNizBajta(byte [] niz ){
        byte [] rez = new byte [niz.length /8];
        return rez;
    }
    
    public static byte[] obrniNiz(byte [] niz){
        byte [] rez = new byte[niz.length];
        int brojac = 0;
        for(int i =niz.length -1  ; i>=0 ; i--)
            rez[brojac++]= niz[i];
        return rez;}

      public static byte[] intToByte(int a){
        byte[] rez = new byte[24];
        int brojac = 0;
        while (a != 0){
            rez[brojac++] = (byte)(a%2);
            a/=2;
        }        
            
        return rez;
    }
}

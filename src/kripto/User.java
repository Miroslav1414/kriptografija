/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

/**
 *
 * @author miroslav.mandic
 */
public class User {

    private String cert_path = "src//sertifikati//";

    private String username;
    private String password;
    private Sertifikat sertifikat;
    
    public String getUsername(){return username;}

    public User() {
    }

    public User(String name, String password) {
        this.username = name;
        this.password = password;
        this.sertifikat = new Sertifikat(cert_path + name + ".der");
    }
    
    public PrivateKey getPrivateKey(){
        return sertifikat.getPrivateKey();
    }
    
    public PublicKey getPublicKey(){
        return sertifikat.getPublicKey();
    }

    public void writeUsers() {
        try {
            String korisniciText = "Admin:Admin#Miso:Miso#";
            //kriptovanje korisnika i upis u fajl
            TripleDES tdes = new TripleDES();
            String passForDes = Helper.getRandomString(20);
            byte[] kriptovaniKorisnici = tdes.encrypt(korisniciText, passForDes);
            Files.write(Paths.get(Helper.USER_PATH), kriptovaniKorisnici);

            //hash fajla i kriptovanje kljuca i hasha javnim kljucem CA
            byte[] usersToByte = Files.readAllBytes(Paths.get((Helper.USER_PATH)));
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(usersToByte);

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

            Files.write(Paths.get(Helper.HASH_PASS_PATH), upisUFajl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public User[] readUsers() {
        User[] rez = null;
        try {
            Sertifikat ca = new Sertifikat();
            PrivateKey privateKey = ca.PRIVATE_KEY;
            Cipher sifrat = Cipher.getInstance("RSA");
            sifrat.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] hashIPass = Files.readAllBytes(Paths.get(Helper.HASH_PASS_PATH));
            byte[] hashCrypted = Arrays.copyOfRange(hashIPass, 0, 256);
            byte[] passCrypted = Arrays.copyOfRange(hashIPass, 256, hashIPass.length);

            byte[] hash1 = sifrat.doFinal(hashCrypted);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash2 = md.digest(Files.readAllBytes(Paths.get((Helper.USER_PATH))));

            if (!(Arrays.equals(hash1, hash2))) {
                //poruka da je neko mjenjao korisnike
                new Poruka("Korisnici ne mogu biti validno ucitani! Neko je izvrsio neovlastenu izmjenu", "ERROR", "ERROR");
                //treba ugasiti program a ne null!!! (kasnije odraditi)
                return null;
            }

            String pass = new String(sifrat.doFinal(passCrypted), "UTF-8");
            String korisnici = new TripleDES().decrypt(Files.readAllBytes(Paths.get(Helper.USER_PATH)), pass);

            String[] nizKorisnika = korisnici.split("#");
            rez = new User[nizKorisnika.length];

            int i = 0;
            for (String korisnik : nizKorisnika) {
                rez[i++] = new User(korisnik.split(":")[0], korisnik.split(":")[1]);
            }

        } 
        catch (IllegalBlockSizeException ibe){
            ibe.printStackTrace();
            new Poruka("Javni i privatni kljuc CA se ne poklapaju!", "ERROR", "ERROR");
        }
        catch (Exception ex) {
            new Poruka("Korisnici nisu dobro ucitani", "ERROR", "ERROR");
            ex.printStackTrace();
        }

        return rez;
    }

    public boolean login(String user, String pass) {
        if (username.equals(user) && password.equals(pass)) {
            return true;
        }
        return false;
    }
    
    public boolean keyMatch(String pathToPrivateKey){
        try {
            PublicKey publicKey = sertifikat.getPublicKey();            
            Cipher sifrat = Cipher.getInstance("RSA");
            sifrat.init(Cipher.ENCRYPT_MODE, publicKey);
            
            byte [] sifrat1 = sifrat.doFinal("miso".getBytes("utf-8"));
            
            PrivateKey privateKey = sertifikat.getPrivateKey(pathToPrivateKey);
            sifrat.init(Cipher.DECRYPT_MODE, privateKey);
            
            byte [] sifrat2= sifrat.doFinal(sifrat1);
            if(new String(sifrat2,"UTF-8").equals("miso"))
                return true;

        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

//    public static void main(String[] args) {
//        new User().writeUsers();
//        //new User().readUsers();
//    }

}

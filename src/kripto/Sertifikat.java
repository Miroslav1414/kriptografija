package kripto;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 *
 * @author miroslav.mandic
 */
public class Sertifikat {

    private final static String CRL_PUTANJA = "src//root//crl.der";
    private final static String ROOTCA_PUTANJA = "src//root//rootca.der";
    private final static String ROOTCA_PUTANJA_PRIVATNOG_KLJUCA = "src//root//root_private_key.der";
    //private final static String ROOTCA_PUTANJA_PRIVATNOG_KLJUCA = "src//privatniKljucevi//admin_private_key.der";    
    public static X509Certificate CA_CERT = null;
    public static X509CRL CRL = null;
    public static CertificateFactory FACTORY = null;
    public static PrivateKey PRIVATE_KEY = null;

    private X509Certificate sertifikat = null;
    private PrivateKey privateKey = null;

    static {
        try {
            FACTORY = CertificateFactory.getInstance("x.509");
            CA_CERT = getCertFromPath(ROOTCA_PUTANJA);
            PRIVATE_KEY  = getPrivateKey(ROOTCA_PUTANJA_PRIVATNOG_KLJUCA);
            CRL = getCRL(CRL_PUTANJA);
            
        } catch (Exception e) {
            e.printStackTrace();
            new Poruka("Greska prilikom ucitavanja CA sertifikata ili CRL!", "ERROR", "ERROR");
        }
    }
    
    public Sertifikat(){}

    //konstruktor
    //public Sertifikat(String pathToCert, String pathToPrivateKey) {
    public Sertifikat(String pathToCert) {
        try {
            sertifikat = getCertFromPath(pathToCert);
            //privateKey = getPrivateKey(pathToPrivateKey);

        } catch (Exception e) {
            e.printStackTrace();
            //ispisati poruku jer certifikat ne postoji
        }
    }

    //ucitava  certifikat sa zadate putanje ako je putanja ispravna
    public static X509Certificate getCertFromPath(String path) {
        X509Certificate rez = null;
        try {
            rez = (X509Certificate) FACTORY.generateCertificate(new FileInputStream(path));
        } catch (FileNotFoundException | CertificateException e) {
            e.printStackTrace();
            //ispistai poruku ako se desila greska jer CA_root ne postoji
        }
        return rez;
    }

    //javni kljuc iz certifikata
    public PublicKey getPublicKey() {
        return sertifikat.getPublicKey();
    }
    
    //javni kljuc CA certifikata
//    public PublicKey getCAPublicKey(){
//        return CA_CERT.getPublicKey();
//    }

    //Ucitavanje privatnog  kljuca sa putanje
    public static PrivateKey getPrivateKey(String path) {
        PrivateKey privateKey = null;
        File fajl = new File(path);
        byte[] privateKeyBytes = new byte[(int) fajl.length()];

        //read bytes for PKCS
        try {
            DataInputStream dis = new DataInputStream(new FileInputStream(fajl));
            dis.read(privateKeyBytes);
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //read private key by KeyFactory
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = (PrivateKey) keyFactory.generatePrivate(privateKeySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    //ucitava crl sa zadate putanje ako je putanja validna
    private static X509CRL getCRL(String crlPath) {
        X509CRL crl = null;
        try {
            crl = (X509CRL) FACTORY.generateCRL(new FileInputStream(crlPath));
        } catch (Exception e) {
            e.printStackTrace();
            //znaci da nema crl liste i da trena ispistai porkuku i ugasiti program
        }
        return crl;
    }

    // da li se nalazi u CRL listi tru ako jeste, false ako nije 
    public boolean isRevoked() {
        X509CRLEntry revokedCertificate = CRL.getRevokedCertificate(sertifikat.getSerialNumber());
        return (revokedCertificate != null);
    }

    //da li je validan datum
    public boolean isValidOnDate() {
        try {
            sertifikat.checkValidity();
            return true;
        } catch (CertificateExpiredException e) {
            System.out.println("Istekao certifikat");
            e.printStackTrace();
        } catch (CertificateNotYetValidException e) {
            System.out.println("Certifikat nije jos validan");
            e.printStackTrace();
        }
        return false;
    }

    //fa li je CRL lista od ogovarajuceg CA //valjda je svaka lista potpisana prilikom kreiranja
    private boolean verifyCrlSign() {
        if (CRL != null) {
            try {
                CRL.verify(CA_CERT.getPublicKey());
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    //da li je privatni kljuc kljuc bas tog sertifikata
    public boolean isMatch(){
        privateKey.
    }
    
    

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

import java.util.Random;

/**
 *
 * @author miroslav.mandic
 */
public class Helper {
    
    public static final String USER_PATH = "src//root//user.usx";
    public static final String HASH_PASS_PATH = "src//root//hp.hp";
    public static final String SERTIFIKATI = "src//sertifikati//";
    
    public Helper(){}
    
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
    
    public static void main (String args []){
        byte a = (byte) 72;
        byte [] aa = {(byte)-255};
        System.out.println((char)255);
        System.out.println(Byte.toString(a) + " " + aa[0]);
        while(a != (byte)0)
        {
            System.out.print(a%2);
            a/=2;
        }
        System.out.println(byteToString(aa));
        
    }
}

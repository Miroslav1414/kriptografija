/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

import static java.lang.Math.pow;
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
    
    public static byte[] obrniNiz(byte [] niz){
        byte [] rez = new byte[niz.length];
        int brojac = 0;
        for(int i =niz.length -1  ; i>=0 ; i--)
            rez[brojac++]= niz[i];
        return rez;}
    
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

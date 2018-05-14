/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

import java.io.Serializable;

/**
 *
 * @author miroslav.mandic
 */
public class Message implements Serializable{
    private String pictureName;
    private byte [] pictureHash;
    private String receiver;
    private boolean procitana;
    
    public Message(){}
    public Message(String path,byte [] hash , String user){
        pictureName = path;
        receiver = user;
        pictureHash = hash;
        procitana = false;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public byte[] getPictureHash() {
        return pictureHash;
    }

    public void setPictureHash(byte[] pictureHash) {
        this.pictureHash = pictureHash;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isProcitana() {
        return procitana;
    }

    public void setProcitana(boolean procitana) {
        this.procitana = procitana;
    }
    
    
}

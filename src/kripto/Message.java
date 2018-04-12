/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kripto;

/**
 *
 * @author miroslav.mandic
 */
public class Message {
    private String pathToPicture;
    private String username;
    
    public Message(){}
    public Message(String path, String user){
        pathToPicture = path;
        username = user;
    }
}

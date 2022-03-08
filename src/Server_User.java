/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author Acer
 */
public class Server_User extends User{

    public Server_User(String name, ImageIcon image) {
        super(name, image);
    }

    
    
    @Override
    public String typeConnect() {
        return "Server";
    }

}

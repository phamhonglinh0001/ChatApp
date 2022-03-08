
import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Acer
 */
public class Client_User extends User {

    public Client_User(String name, ImageIcon image) {
        super(name, image);
    }
    
    @Override
    public String typeConnect() {
        return "Client";
    }

}

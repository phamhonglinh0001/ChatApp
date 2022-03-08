/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Acer
 */
public class Test {
    
    public static void main(String[] args){
//        User l=new Server_User("Long Tran", "avatar1.png");
//        Chat chat=new Chat(l);
//        chat.hienThi();
        
        User ld=new Client_User("Takagi", "takagi.png");
        Chat chata=new Chat(ld);
        chata.hienThi();
    }
}

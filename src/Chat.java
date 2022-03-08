



import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Chat implements ActionListener{
    
    static JPanel pnlMain;
    JTextField txtMessage;
    JButton btnSent;
    static JPanel pnlKhungChat;
    static JFrame frmMain = new JFrame("Chat App");
    static JPanel  p2;
    static Box vertical = Box.createVerticalBox();
    File[] fileToSend = new File[1];
    static Socket socket;
    static TextMessage msocket;
    static FileMessage fm;
    static ImageMessage im;
    Boolean typing;
    
    protected static User user;
    
    
    Chat(User user){
        this.user=user;
        frmMain.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        pnlMain = new JPanel();
        pnlMain.setLayout(null);
        pnlMain.setBackground(Configuration.maunen);
        pnlMain.setBounds(0, 0, 450, 70);
        frmMain.add(pnlMain);
       ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("chatting/application/icons/3.png"));
       Image i2 = i1.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
       ImageIcon i3 = new ImageIcon(i2);
       JLabel l1 = new JLabel(i3);
       l1.setBounds(5, 17, 30, 30);
       pnlMain.add(l1);
       
       l1.addMouseListener(new MouseAdapter(){
           public void mouseClicked(MouseEvent ae){
               msocket.close();
               System.exit(0);
           }
       });
       
       ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("chatting/application/icons/"+user.imageUser));
       Image i5 = i4.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT);
       ImageIcon i6 = new ImageIcon(i5);
       JLabel l2 = new JLabel(i6);
       l2.setBounds(40, 5, 60, 60);
       pnlMain.add(l2);
       
       ImageIcon i7 = new ImageIcon(ClassLoader.getSystemResource("chatting/application/icons/file.jpg"));
       Image i8 = i7.getImage().getScaledInstance(30, 30, Image.SCALE_DEFAULT);
       ImageIcon i9 = new ImageIcon(i8);
       JLabel lblFile = new JLabel(i9);
       lblFile.setBounds(290, 20, 30, 30);
       
       lblFile.addMouseListener(new MouseAdapter(){
           public void mouseClicked(MouseEvent ae){
               // Create a file chooser to open the dialog to choose a file.
                JFileChooser jFileChooser = new JFileChooser();
                // Set the title of the dialog.
                jFileChooser.setDialogTitle("Choose a file to send.");
                // Show the dialog and if a file is chosen from the file chooser execute the following statements.
                if (jFileChooser.showOpenDialog(null)  == JFileChooser.APPROVE_OPTION) {
                    // Get the selected file.
                    fileToSend[0] = jFileChooser.getSelectedFile();
                    int question=JOptionPane.showConfirmDialog(frmMain, "Do you want to send "+fileToSend[0].getName()+" ?","Send file",JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(question==JOptionPane.YES_OPTION){
                        try {
                        // Change the text of the java swing label to have the file name.
                        //txtMessage.setText(fileToSend[0].getName());
                        fm.send(fileToSend);
                        } catch (IOException ex) {
                            
                        }
                        fileToSend=new File[1];
                    }
                    
                }
           }
       });
       pnlMain.add(lblFile);
       ImageIcon i11 = new ImageIcon(ClassLoader.getSystemResource("chatting/application/icons/image.png"));
       Image i12 = i11.getImage().getScaledInstance(35, 30, Image.SCALE_DEFAULT);
       ImageIcon i13 = new ImageIcon(i12);
       JLabel lblImage = new JLabel(i13);
       lblImage.setBounds(350, 20, 35, 30);
       pnlMain.add(lblImage);
        lblImage.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                // Create a file chooser to open the dialog to choose a file.
                JFileChooser jFileChooser = new JFileChooser();
                // Set the title of the dialog.
                jFileChooser.setDialogTitle("Choose a image to send.");
                // Show the dialog and if a file is chosen from the file chooser execute the following statements.
                if (jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    String dir = jFileChooser.getCurrentDirectory().toString();
                    String fileName = jFileChooser.getSelectedFile().getName();
                    ImageIcon img = new ImageIcon(dir + "\\" + fileName); //Thu muc icon
                    int question = JOptionPane.showConfirmDialog(frmMain, "Do you want to send " + fileName + " ?", "Send image", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (question == JOptionPane.YES_OPTION) {
                        try {
                            im.send(img);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
       
       
       ImageIcon i14 = new ImageIcon(ClassLoader.getSystemResource("chatting/application/icons/3icon.png"));
       Image i15 = i14.getImage().getScaledInstance(13, 25, Image.SCALE_DEFAULT);
       ImageIcon i16 = new ImageIcon(i15);
       JLabel l7 = new JLabel(i16);
       l7.setBounds(410, 20, 13, 25);
       pnlMain.add(l7);
       
       
       JLabel l3 = new JLabel(user.nameUser);
       l3.setFont(new Font("SAN_SERIF", Font.BOLD, 18));
       l3.setForeground(Color.WHITE);
       l3.setBounds(110, 15, 100, 18);
       pnlMain.add(l3);   
       
       
       JLabel l4 = new JLabel("Active Now");
       l4.setFont(new Font("SAN_SERIF", Font.PLAIN, 14));
       l4.setForeground(Color.WHITE);
       l4.setBounds(110, 35, 100, 20);
       pnlMain.add(l4);  
       
       Timer t = new Timer(1, new ActionListener(){
           public void actionPerformed(ActionEvent ae){
               if(!typing){
                   l4.setText("Active Now");
               }
           }
       });
       
       t.setInitialDelay(1000);
       
       
       pnlKhungChat = new JPanel();
       pnlKhungChat.setBounds(5, 75, 440, 570);
       pnlKhungChat.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
       frmMain.add(pnlKhungChat);
       
       JScrollPane sp=new JScrollPane(pnlKhungChat);
       sp.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {  
        public void adjustmentValueChanged(AdjustmentEvent e) {  
            e.getAdjustable().setValue(e.getAdjustable().getMaximum());  
        }
    });
    
       sp.setBounds(5,75,440,570);
       sp.setBorder(BorderFactory.createEmptyBorder());
       frmMain.add(sp);
       
       txtMessage = new JTextField();
       txtMessage.setBounds(5, 655, 310, 40);
       txtMessage.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
       frmMain.add(txtMessage);
       
       txtMessage.addKeyListener(new KeyAdapter(){
           public void keyPressed(KeyEvent ke){
               l4.setText("typing...");
               
               t.stop();
               
               typing = true;
           }
           
           public void keyReleased(KeyEvent ke){
               typing = false;
               
               if(!t.isRunning()){
                   t.start();
               }
           }
       });
       
       btnSent = new JButton("Send");
       btnSent.setBounds(320, 655, 123, 40);
       btnSent.setBackground(Configuration.maubtnSend);
       btnSent.setForeground(Color.WHITE);
       btnSent.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
       btnSent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }

        private void btnSendActionPerformed(ActionEvent evt) {
            try{
                msocket.send(txtMessage.getText());
                txtMessage.setText("");
                
            }catch(Exception e){
               System.out.println(e);
            }
        }
     });
       frmMain.add(btnSent);
       frmMain.getRootPane().setDefaultButton(btnSent);
        
       frmMain.getContentPane().setBackground(Color.WHITE);
       frmMain.setLayout(null);
       frmMain.setSize(470, 750);
       frmMain.setLocation(400, 200); 
       //frmMain.setUndecorated(true);//bo khung di chuyen
       frmMain.setVisible(true);
        
    }
    
    public void actionPerformed(ActionEvent ae){
        
    }
    public void hienThi(){
        frmMain.setVisible(true);
        try {
            msocket = new TextMessage(user.typeConnect(), pnlKhungChat, frmMain, vertical);
            fm = new FileMessage(user.typeConnect(), pnlKhungChat, frmMain, vertical);
            im = new ImageMessage(user.typeConnect(), pnlKhungChat, frmMain, vertical);
            JOptionPane.showConfirmDialog(frmMain,"Success","Connected",JOptionPane.YES_OPTION,JOptionPane.INFORMATION_MESSAGE);
            msocket.receive();
            fm.receive();
            im.receive();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
     

 
}

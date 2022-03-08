/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//import static chatting.application.Server.p2;
//import static chatting.application.Server.vertical;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
//import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Acer
 */
public class TextMessage {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream reader;
    private static JPanel pnlKhungChat;
    private static Box vertical;
    private static JFrame frmMain;
    private User user;
    private int idsend = 0, idreceive = 0;

    public TextMessage(User user, JPanel pnlKhungChat, JFrame frmMain, Box vertical) throws IOException {
        this.pnlKhungChat = pnlKhungChat;
        this.frmMain = frmMain;
        this.vertical = vertical;
        this.user = user;
        if (user.typeConnect().equals("Server")) {
            int port = Configuration.portTxt;
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
        } else {//Client
            socket = new Socket(Configuration.IpServer, Configuration.portTxt);
        }
        out = new DataOutputStream(socket.getOutputStream());
        reader = new DataInputStream(socket.getInputStream());
        //receive();
    }

    public void receive() {
        Thread th = new Thread() {
            public void run() {
                while (true) {
                    try {
                        String line = reader.readUTF();
                        if (idreceive == 0) {
                            Friend.name = line;
                        } else if (line != null) {
                            taokhung(line, "left");
                            frmMain.validate();
                        }
                        idreceive++;
                    } catch (Exception ex) {
//                      ex.printStackTrace();
                    }
                }
            }
        };
        th.start();
    }

    public void send(String msg) throws IOException {
        if (msg.trim().equals("")) {
            return;
        }
        out.writeUTF(msg);
        if (idsend != 0) {
            taokhung(msg, "right");
            frmMain.validate();
        }
        idsend++;
    }

    public void close() {
        try {
            out.close();
            reader.close();
            socket.close();
        } catch (Exception ex) {

        }
    }

    private void taokhung(String mess, String huong) {
        JPanel p2 = formatLabel(mess, huong);
        JPanel pnlMess = new JPanel(new BorderLayout());
        JPanel pnlUser = new JPanel(new BorderLayout());
        pnlKhungChat.setLayout(new BorderLayout());

        if (huong.equals("right")) {//Ben gui
            Image i12 = user.imageUser.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
            ImageIcon i13 = new ImageIcon(i12);
            JLabel lblAvatar = new JLabel(i13);
            pnlUser.add(lblAvatar, BorderLayout.NORTH);
            JLabel lblName = new JLabel(user.nameUser);
            lblName.setFont(new Font("serif", Font.BOLD, 15));
            lblName.setForeground(Color.BLUE);
            pnlUser.add(lblName, BorderLayout.SOUTH);
            pnlMess.add(p2, BorderLayout.WEST);
            pnlMess.add(pnlUser, BorderLayout.EAST);

            JPanel right = new JPanel(new BorderLayout());
            right.add(pnlMess, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

        } else {
            Image i12 = Friend.img.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
            ImageIcon i13 = new ImageIcon(i12);
            JLabel lblAvatar = new JLabel(i13);
            pnlUser.add(lblAvatar, BorderLayout.NORTH);
            JLabel lblName = new JLabel(Friend.name);
            lblName.setFont(new Font("serif", Font.BOLD, 15));
            lblName.setForeground(Color.BLUE);
            pnlUser.add(lblName, BorderLayout.SOUTH);
            pnlMess.add(p2, BorderLayout.EAST);
            pnlMess.add(pnlUser, BorderLayout.WEST);

            JPanel left = new JPanel(new BorderLayout());
            left.add(pnlMess, BorderLayout.LINE_START);
            vertical.add(left);
            vertical.add(Box.createVerticalStrut(15));

        }
        pnlKhungChat.add(vertical, BorderLayout.PAGE_START);
    }

    private JPanel formatLabel(String out, String huong) {
        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));

        JLabel l1 = new JLabel("<html><p style = \"width : 120px\">" + out + "</p></html>");
        l1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        if (huong.equals("left")) {
            l1.setBackground(Configuration.mauMessageLeft);
        } else {
            l1.setBackground(Configuration.mauMessageRight);
        }
        l1.setOpaque(true);
        l1.setBorder(new EmptyBorder(15, 15, 15, 50));

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel l2 = new JLabel();
        l2.setText(sdf.format(cal.getTime()));

        p3.add(l1);
        p3.add(l2);
        return p3;
    }

}

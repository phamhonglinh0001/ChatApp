/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Acer
 */
public class ImageMessage {

    private Socket socket;
    private static JPanel pnlKhungChat;
    private static Box vertical;
    private static JFrame frmMain;
    private User user;
    private int idsend = 0, idreceive = 0;

    public ImageMessage(User user, JPanel pnlKhungChat, JFrame frmMain, Box vertical) throws IOException {
        this.pnlKhungChat = pnlKhungChat;
        this.frmMain = frmMain;
        this.vertical = vertical;
        this.user = user;
        if (user.typeConnect().equals("Server")) {
            int port = Configuration.portImage;
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
        } else {//Client
            socket = new Socket(Configuration.IpServer, Configuration.portImage);
        }

        //receive();
    }

    public void receive() {
        Thread th = new Thread() {
            public void run() {
                while (true) {
                    try {
                        InputStream inputStream = socket.getInputStream();
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);
                        if (idreceive == 0) {
                            Friend.img = new ImageIcon(bufferedImage);
                        } else {
                            taokhung(new ImageIcon(bufferedImage), "left");
                            frmMain.validate();
                        }
                        idreceive++;
                    } catch (Exception ex) {

                    }
                }
            }
        };
        th.start();
    }

    public void send(ImageIcon imageIcon) throws IOException {
        try {

            OutputStream outputStream = socket.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);

            Image image = imageIcon.getImage();

            BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = bufferedImage.createGraphics();
            graphics.drawImage(image, 0, 0, null);

            graphics.dispose();

            ImageIO.write(bufferedImage, "png", bufferedOutputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (idsend != 0) {
            taokhung(imageIcon, "right");
            frmMain.validate();
        }
        idsend++;
    }

    public void close() {
        try {
            socket.close();
        } catch (Exception ex) {
        }
    }

    private void taokhung(ImageIcon img, String huong) {
        JPanel p2 = formatLabel(img, huong);
        JPanel pnlMess = new JPanel(new BorderLayout());
        JPanel pnlUser = new JPanel(new BorderLayout());
        pnlKhungChat.setLayout(new BorderLayout());

        if (huong.equals("right")) {//Ben gui
            Image i12 = user.imageUser.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
            ImageIcon i13 = new ImageIcon(i12);
            JLabel lblAvatar = new JLabel(i13);
            pnlUser.add(lblAvatar, BorderLayout.CENTER);
            JLabel lblName = new JLabel(user.nameUser);
            lblName.setFont(new Font("serif", Font.BOLD, 15));
            lblName.setForeground(Color.BLUE);
            pnlUser.add(lblName, BorderLayout.SOUTH);
            pnlMess.add(p2, BorderLayout.WEST);
            pnlMess.add(pnlUser, BorderLayout.EAST);

            JPanel right = new JPanel(new BorderLayout());
            right.add(pnlMess, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(25));

        } else {
            Image i12 = Friend.img.getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT);
            ImageIcon i13 = new ImageIcon(i12);
            JLabel lblAvatar = new JLabel(i13);
            pnlUser.add(lblAvatar, BorderLayout.CENTER);
            JLabel lblName = new JLabel(Friend.name);
            lblName.setFont(new Font("serif", Font.BOLD, 15));
            lblName.setForeground(Color.BLUE);
            pnlUser.add(lblName, BorderLayout.SOUTH);
            pnlMess.add(p2, BorderLayout.EAST);
            pnlMess.add(pnlUser, BorderLayout.WEST);

            JPanel left = new JPanel(new BorderLayout());
            left.add(pnlMess, BorderLayout.LINE_START);
            vertical.add(left);
            vertical.add(Box.createVerticalStrut(25));

        }
        pnlKhungChat.add(vertical, BorderLayout.PAGE_START);
    }

    private JPanel formatLabel(ImageIcon img, String huong) {
        JPanel p3 = new JPanel();
        p3.setLayout(new BoxLayout(p3, BoxLayout.Y_AXIS));

        ImageIcon imageIcon = new ImageIcon(fitimage(img.getImage(), Configuration.widthOfImage, img.getImage().getHeight(null) * Configuration.widthOfImage / img.getImage().getWidth(null)));

        JLabel l1 = new JLabel(imageIcon);
        l1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        if (huong.equals("left")) {
            l1.setBackground(Configuration.mauMessageLeft);
        } else {
            l1.setBackground(Configuration.mauMessageRight);
        }
        l1.setOpaque(true);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel l2 = new JLabel();
        l2.setText(sdf.format(cal.getTime()));

        p3.add(l1);
        p3.add(l2);
        return p3;
    }

    private static Image fitimage(Image img, int w, int h) {
        BufferedImage resizedimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedimage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0, w, h, null);
        g2.dispose();
        return resizedimage;
    }
}

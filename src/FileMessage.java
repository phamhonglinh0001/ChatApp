/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Acer
 */
public class FileMessage {

    private Socket socket;
    private static JPanel pnlKhungChat;
    private static Box vertical;
    private static JFrame frmMain;
    private User user;
    // Array list to hold information about the files received.
    static ArrayList<MyFile> myFiles = new ArrayList<>();
    // Used to track the file (jpanel that has the file name in it on a label).
    private int fileId = 0;

    public FileMessage(User user, JPanel pnlKhungChat, JFrame frmMain, Box vertical) throws IOException {
        this.pnlKhungChat = pnlKhungChat;
        this.frmMain = frmMain;
        this.vertical = vertical;
        this.user=user;
        if (user.typeConnect().equals("Server")) {
            int port = Configuration.portFile;
            ServerSocket serverSocket = new ServerSocket(port);
            socket = serverSocket.accept();
        } else {//Client
            socket = new Socket(Configuration.IpServer, Configuration.portFile);
        }

    }

    public void receive() {
        Thread th = new Thread() {
            public void run() {
                while (true) {
                    try {

                        // Stream to receive data from the client through the socket.
                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

                        // Read the size of the file name so know when to stop reading.
                        int fileNameLength = dataInputStream.readInt();
                        // If the file exists
                        if (fileNameLength > 0) {
                            // Byte array to hold name of file.
                            byte[] fileNameBytes = new byte[fileNameLength];
                            // Read from the input stream into the byte array.
                            dataInputStream.readFully(fileNameBytes, 0, fileNameBytes.length);
                            // Create the file name from the byte array.
                            String fileName = new String(fileNameBytes);
                            // Read how much data to expect for the actual content of the file.
                            int fileContentLength = dataInputStream.readInt();
                            // If the file exists.
                            if (fileContentLength > 0) {
                                // Array to hold the file data.
                                byte[] fileContentBytes = new byte[fileContentLength];
                                // Read from the input stream into the fileContentBytes array.
                                dataInputStream.readFully(fileContentBytes, 0, fileContentBytes.length);
                                // Add panel message
                                taokhung(fileName, "left");
                                frmMain.validate();
                                // Add the new file to the array list which holds all our data.
                                myFiles.add(new MyFile(fileId, fileName, fileContentBytes, getFileExtension(fileName)));
                                // Increment the fileId for the next file to be received.
                                fileId++;
                            }
                        }

                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
            }
        };
        th.start();
    }

    public void send(File[] fileToSend) throws IOException {
        try {
            if (fileToSend == null) {
                return;
            }
            // Create an input stream into the file you want to send.
            FileInputStream fileInputStream = new FileInputStream(fileToSend[0].getAbsolutePath());
            // Create an output stream to write to write to the server over the socket connection.
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            // Get the name of the file you want to send and store it in filename.
            String fileName = fileToSend[0].getName();
            // Convert the name of the file into an array of bytes to be sent to the server.
            byte[] fileNameBytes = fileName.getBytes();
            // Create a byte array the size of the file so don't send too little or too much data to the server.
            byte[] fileBytes = new byte[(int) fileToSend[0].length()];
            // Put the contents of the file into the array of bytes to be sent so these bytes can be sent to the server.
            fileInputStream.read(fileBytes);
            // Send the length of the name of the file so server knows when to stop reading.
            dataOutputStream.writeInt(fileNameBytes.length);
            // Send the file name.
            dataOutputStream.write(fileNameBytes);
            // Send the length of the byte array so the server knows when to stop reading.
            dataOutputStream.writeInt(fileBytes.length);
            // Send the actual file.
            dataOutputStream.write(fileBytes);
            taokhung(fileToSend[0].getName(), "right");
            frmMain.validate();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void close() {
        try {
            socket.close();
        } catch (Exception ex) {

        }
    }

    /**
     * @param fileName
     * @return The extension type of the file.
     */
    public static String getFileExtension(String fileName) {
        // Get the file type by using the last occurence of . (for example aboutMe.txt returns txt).
        // Will have issues with files like myFile.tar.gz.
        int i = fileName.lastIndexOf('.');
        // If there is an extension.
        if (i > 0) {
            // Set the extension to the extension of the filename.
            return fileName.substring(i + 1);
        } else {
            return "No extension found.";
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
            p2.setName(fileId+"");
            p2.addMouseListener(getMyMouseListener());
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

        JLabel l1 = new JLabel("<html><p style = \"width : 150px\">" + out + "</p></html>");
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

    /**
     * When the jpanel is clicked a popup shows to say whether the user wants to
     * download the selected document.
     *
     * @return A mouselistener that is used by the jpanel.
     */
    public static MouseListener getMyMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Get the source of the click which is the JPanel.
                JPanel jPanel = (JPanel) e.getSource();
                // Get the ID of the file.
                int fileId = Integer.parseInt(jPanel.getName());
                // Loop through the file storage and see which file is the selected one.
                for (MyFile myFile : myFiles) {
                    if (myFile.getId() == fileId) {
                        JFrame jfPreview = createFrame(myFile.getName(), myFile.getData(), myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }

    public static JFrame createFrame(String fileName, byte[] fileData, String fileExtension) {

        // Frame to hold everything.
        JFrame jFrame = new JFrame("Chat App's File Downloader");
        // Set the size of the frame.
        jFrame.setSize(600, 400);
        jFrame.setLocation(700, 400);

        // Panel to hold everything.
        JPanel jPanel = new JPanel();
        // Make the layout a box layout with child elements stacked on top of each other.
        jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
        jPanel.setBackground(Configuration.maunen);

        // Title above panel.
        JLabel jlTitle = new JLabel("Chat App's File Downloader");
        // Center the label title horizontally.
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Change the font family, size, and style.
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        // Add spacing on the top and bottom of the element.
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setForeground(Color.white);

        // Label to prompt the user if they are sure they want to download the file.
        JLabel jlPrompt = new JLabel("Are you sure you want to download " + fileName + "?");
        // Change the font style, size, and family of the label.
        jlPrompt.setFont(new Font("Arial", Font.BOLD, 20));
        // Add spacing on the top and bottom of the label.
        jlPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
        // Center the label horizontally.
        jlPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);
        jlPrompt.setForeground(Color.white);

        // Create the yes for accepting the download.
        JButton jbYes = new JButton("Yes");
        jbYes.setPreferredSize(new Dimension(150, 75));
        // Set the font for the button.
        jbYes.setFont(new Font("Arial", Font.BOLD, 20));

        // No button for rejecting the download.
        JButton jbNo = new JButton("No");
        // Change the size of the button must be preferred because if not the layout will ignore it.
        jbNo.setPreferredSize(new Dimension(150, 75));
        // Set the font for the button.
        jbNo.setFont(new Font("Arial", Font.BOLD, 20));

        // Label to hold the content of the file whether it be text of images.
        JLabel jlFileContent = new JLabel();
        jlFileContent.setFont(new Font("Arial", Font.BOLD, 18));
        jlFileContent.setForeground(Color.white);
        // Align the label horizontally.
        jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Panel to hold the yes and no buttons and make the next to each other left and right.
        JPanel jpButtons = new JPanel();
        // Add spacing around the panel.
        jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
        // Add the yes and no buttons.
        jpButtons.add(jbYes);
        jpButtons.add(jbNo);
        jpButtons.setBackground(Color.green);

        // If the file is a text file then display the text.
        if (fileExtension.equalsIgnoreCase("txt")) {
            // Wrap it with <html> so that new lines are made.
            jlFileContent.setText("<html>" + new String(fileData) + "</html>");
            // If the file is not a text file then make it an image.
        } else {
            jlFileContent.setIcon(new ImageIcon(fileData));
        }

        // Yes so download file.
        jbYes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Choose location to save
                JFileChooser c = new JFileChooser();
                c.setSelectedFile(new File("D:\\" + fileName));
                int rVal = c.showSaveDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    String dir = c.getCurrentDirectory().toString();

                    // Create the file with its name.
                    File fileToDownload = new File(dir + "\\" + fileName);
                    try {
                        // Create a stream to write data to the file.
                        FileOutputStream fileOutputStream = new FileOutputStream(fileToDownload);
                        // Write the actual file data to the file.
                        fileOutputStream.write(fileData);
                        // Close the stream.
                        fileOutputStream.close();
                        // Get rid of the jFrame. after the user clicked yes.
                        jFrame.dispose();
                        JOptionPane.showConfirmDialog(frmMain, "Save successful!", "Save file", JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // No so close window.
        jbNo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // User clicked no so don't download the file but close the jframe.
                jFrame.dispose();
            }
        });

        // Add everything to the panel before adding to the frame.
        jPanel.add(jlTitle);
        jPanel.add(jlPrompt);
        jPanel.add(jlFileContent);
        jPanel.add(jpButtons);

        // Add panel to the frame.
        jFrame.add(jPanel);

        // Return the jFrame so it can be passed the right data and then shown.
        return jFrame;

    }
}

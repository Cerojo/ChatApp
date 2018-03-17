/**
 * Created by cecil on 10 Mar 2018.
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class UserLogic extends Thread{
    private JButton sendButton;
    private JButton attachButton;
    private JButton imageButton;

    private JTextField message;

    private UserInterface UI;

    private Socket socket = null;
    private UserLogic[] threads;
    private BufferedReader inputStream = null;
    private PrintStream printStream = null;
    private BufferedImage img = null;
    private String username = "";
    private HashMap<String,ArrayList<String>> chats=new HashMap<String, ArrayList<String>>();
    private String groupChat = "     Group Chat     ";
    private DefaultListCellRenderer cellRenderer;
    private DefaultListCellRenderer unrenderer;

    UserLogic(Socket socket, UserLogic[] threads) {
        this.socket = socket;
        this.threads = threads;
        UI = new UserInterface();
        unrenderer = new DefaultListCellRenderer();
        unrenderer.setHorizontalAlignment(JLabel.CENTER);
        cellRenderer = new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(((String)value).equals(username)){
                    setBackground(Color.GREEN);
                }
                return this;
            }
        };
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
    }

    public void run() {
        try{
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printStream = new PrintStream(socket.getOutputStream());

            //Get the username
            while (username.equals("")) {
                username = JOptionPane.showInputDialog(null, "Please enter your username", "Username", JOptionPane.PLAIN_MESSAGE);
            }
            startUI();
            enteredChat();
            inputStream.close();
            printStream.close();
            socket.close();

        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void enteredChat() {
        for (UserLogic t : threads) {
            if (t != null && t != this) {
                t.UI.getProperContactsList().addElement(username);
                t.UI.getProperList().addElement(username + " has joined!");
            }
            if(t != null && !t.username.equalsIgnoreCase(username)){
                this.UI.getProperContactsList().addElement(t.username);
            }
        }
    }

    private void startUI(){
        UI.setupUI();
        UI.displayApp();
        sendButtonEvent();
        imageButtonEvent();
        attachButtonEvent();
        userMessageEvent();
        contactsListEvent();
    }

    private void sendButtonEvent(){
        sendButton = UI.getSendButton();
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!UI.getMessage().getText().equalsIgnoreCase("")){
                    for (UserLogic t : threads) {
                        if(((String)UI.getContacts().getSelectedValue()).equals(groupChat)) {
                            groupChatSend(t);
                        }
                        else {
                            individualSend(t);
                        }
                    }
                    UI.getMessage().setText("");
                }
            }
        });
    }

    private void individualSend(UserLogic t) {
        //This writes on others screen
        if(t != UserLogic.this && t != null && ((String)UI.getContacts().getSelectedValue()).equals(t.username)){
            if(((String)t.UI.getContacts().getSelectedValue()).equals(username)) {
                t.UI.getProperList().addElement(username + "> " + UI.getMessage().getText());
                t.UI.getContacts().setCellRenderer(unrenderer);
            }
            else{
                t.UI.getContacts().setCellRenderer(cellRenderer);
            }
            if(t.chats.containsKey(username)){
                ArrayList<String> arrayList = t.chats.get(username);
                arrayList.add(username + "> " + UI.getMessage().getText());
                t.chats.put(username, arrayList);
            }
            else {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(username + "> " + UI.getMessage().getText());
                t.chats.put(username, arrayList);
            }
        }
        //This writes on my screen
        if(t == UserLogic.this && t!=null){
            UI.getProperList().addElement("Me> " + UI.getMessage().getText());
            String currentChat = (String) UI.getContacts().getSelectedValue();
            if(chats.containsKey(currentChat)){
                ArrayList<String> arrayList = chats.get(currentChat);
                arrayList.add("Me> " + UI.getMessage().getText());
                chats.put(currentChat, arrayList);
            }
            else {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("Me> " + UI.getMessage().getText());
                chats.put(currentChat, arrayList);
            }
        }
    }

    private void groupChatSend(UserLogic t) {
        if(((String) UI.getContacts().getSelectedValue()).equals( groupChat)){
            if (t != UserLogic.this && t != null) {
                if(((String)t.UI.getContacts().getSelectedValue()).equals(groupChat)) {
                    t.UI.getProperList().addElement(username + "> " + UI.getMessage().getText());
                    t.UI.getContacts().setCellRenderer(unrenderer);
                }
                else{
                    t.UI.getContacts().setCellRenderer(cellRenderer);
                }
                if(t.chats.containsKey(groupChat)){
                    ArrayList<String> arrayList = t.chats.get(groupChat);
                    arrayList.add(username + "> " + UI.getMessage().getText());
                    t.chats.put(groupChat, arrayList);
                }
                else {
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add(username + "> " + UI.getMessage().getText());
                    t.chats.put(groupChat, arrayList);
                }
            }

            if(t == UserLogic.this && t!=null){
                UI.getProperList().addElement("Me> " + UI.getMessage().getText());
                if(chats.containsKey(groupChat)){
                    ArrayList<String> arrayList = chats.get(groupChat);
                    arrayList.add("Me> " + UI.getMessage().getText());
                    chats.put(groupChat, arrayList);
                }
                else {
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add("Me> " + UI.getMessage().getText());
                    chats.put(groupChat, arrayList);
                }
            }
        }
    }

    private void attachButtonEvent(){
        attachButton = UI.getAttachmentButton();
        attachButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();

                int option = chooser.showOpenDialog(UI.getUIFrame());
                if(option == JFileChooser.APPROVE_OPTION) {
                    try
                    {
                        for (UserLogic t : threads) {
                            String extension = (chooser.getSelectedFile().getName());
                            String format = extension.substring(extension.indexOf(".")+1,extension.length());
                            if(!(format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg"))) {
                                if (t != UserLogic.this && t != null) {
                                    t.img = ImageIO.read(chooser.getSelectedFile());
                                    int jOption = JOptionPane.showConfirmDialog(t.UI.getUIFrame(), "Would you like to receive this file?", username + " sent a file", JOptionPane.NO_OPTION);
                                    if (jOption == JOptionPane.OK_OPTION) {
                                        t.UI.getProperList().addElement(("File Received!"));
                                        Desktop.getDesktop().open(chooser.getSelectedFile());
                                    } else {
                                        t.UI.getProperList().addElement(("File Denied!"));
                                    }
                                }
                                else if (t == UserLogic.this) {
                                    UserLogic.this.UI.getProperList().addElement(("File Sent!"));
                                }
                            }
                            else{
                                if(t == UserLogic.this) {
                                    JOptionPane.showMessageDialog(UI, "Wrong format, try image button");
                                }
                            }
                        }
                    }
                    catch (IOException ignored) {}
                }
            }
        });
    }

    private void imageButtonEvent(){
        imageButton = UI.getImageButton();
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                p("Attachment");
                JFileChooser chooser = new JFileChooser();

                int option = chooser.showOpenDialog(UI.getUIFrame());
                if(option == JFileChooser.APPROVE_OPTION) {
                    try
                    {
                        for (UserLogic t : threads) {
                            String extension = (chooser.getSelectedFile().getName());
                            String format = extension.substring(extension.indexOf(".")+1,extension.length());
                            if(format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) {
                                if (t != UserLogic.this && t != null) {
                                    t.img = ImageIO.read(chooser.getSelectedFile());
                                    int jOption = JOptionPane.showConfirmDialog(t.UI.getUIFrame(), "Would you like to receive the image?", t.username + " sent an image", JOptionPane.NO_OPTION);
                                    if (jOption == JOptionPane.OK_OPTION) {
                                        t.UI.getProperList().addElement(("Image Received!"));
                                        Desktop.getDesktop().open(chooser.getSelectedFile());
                                    } else {
                                        t.UI.getProperList().addElement(("Image Denied!"));
                                    }
                                }
                                else if (t == UserLogic.this) {
                                    UserLogic.this.UI.getProperList().addElement(("Image Sent!"));
                                }
                            }
                            else{
                                if(t == UserLogic.this) {
                                    JOptionPane.showMessageDialog(UI, "Wrong format, try attach button");
                                }
                            }
                            //else if()
                        }
                    }
                    catch (IOException ignored) {}
                }
            }
        });
    }

    private void userMessageEvent(){
        message = UI.getMessage();
        message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if(!UI.getMessage().getText().equalsIgnoreCase("")){
                        for (UserLogic t : threads) {
                            if(((String)UI.getContacts().getSelectedValue()).equals(groupChat)) {
                                groupChatSend(t);
                            }
                            else {
                                individualSend(t);
                            }
                        }
                        UI.getMessage().setText("");
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    private void contactsListEvent(){
        UI.getContacts().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                UI.getProperList().clear();
                UI.getContacts().setCellRenderer(unrenderer);
                UI.getUserName().setText((String) UI.getContacts().getSelectedValue());
                String currentChat = (String)UI.getContacts().getSelectedValue();
                if(chats.containsKey(currentChat)){
                    for(String s: chats.get(currentChat)){
                        UI.getProperList().addElement(s);
                    }
                }
            }
        });
    }

    private static void p(String string){
        System.out.println(string);
    }
}

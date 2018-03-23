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

//This class handles the JAVA swing logic. i.e This is the actual client operations

public class UserLogic extends Thread{
    //Private variables
    private JButton sendButton;
    private JButton attachButton;
    private JButton imageButton;

    private JTextField message;

    private UserInterface UI;

    private Socket socket = null;
    private UserLogic[] threads;
    private BufferedImage img = null;
    private String username = "";
    private HashMap<String,ArrayList<String>> chats=new HashMap<String, ArrayList<String>>();
    private String groupChat = "     Group Chat     ";
    private DefaultListCellRenderer setNotification;
    private DefaultListCellRenderer clearNotification;

    //Constructor
    UserLogic(Socket socket, UserLogic[] threads) {
        this.socket = socket;
        this.threads = threads;
        UI = new UserInterface(); //Initialise User Interface
        clearNotification = new DefaultListCellRenderer(); //
        clearNotification.setHorizontalAlignment(JLabel.CENTER);
        setNotification = new DefaultListCellRenderer(){
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if(((String)value).equals(username) && !isSelected){
                    setBackground(Color.GREEN);
                }
                return this;
            }
        };
        setNotification.setHorizontalAlignment(JLabel.CENTER);
    }

    public void run() { //Run User thread
        try{
            //Get the username
            while (username.equals("")) { //Wait until username is provided
                username = JOptionPane.showInputDialog(null, "Please enter your username", "Username", JOptionPane.PLAIN_MESSAGE);
            }
            startUI(); //Call UI events
            enteredChat(); //Call user enters the chat room
            socket.close(); //Close socket

        }catch(Exception e){
            System.out.println(e);
        }
    }

    private void enteredChat() { //Enter chatroom
        for (UserLogic t : threads) { //Go through clients
            if (t != null && t != this) { //If the this not the current user thread (This Client)
                t.UI.getProperContactsList().addElement(username); //Add new client to the list of clients
                t.UI.getProperList().addElement(username + " has joined!"); //Write message to the chatroom
            }
            if(t != null && !t.username.equalsIgnoreCase(username)){ //If this is the current user. (This Client)
                this.UI.getProperContactsList().addElement(t.username); //Add unique client to the list
            }
        }
    }

    private void startUI(){ //Events handling
        UI.setupUI();
        UI.displayApp();
        sendButtonEvent(); //Sent Button Event
        imageButtonEvent(); //Image Button Event
        attachButtonEvent(); //Attachment Button Event
        userMessageEvent(); //Message textField Event
        contactsListEvent(); //Users list Event
    }

    private void sendButtonEvent(){
        sendButton = UI.getSendButton(); //get sendbutton object
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //Event listener
                if(!UI.getMessage().getText().equalsIgnoreCase("")){ //If the message is blank, send.
                    for (UserLogic t : threads) { //Go through clients
                        if(((String)UI.getContacts().getSelectedValue()).equals(groupChat)) { //Check if current screen is group chat
                            groupChatSend(t); //Go to group chat method
                        }
                        else { //Check if it is individual user
                            individualSend(t); //Go to individuals method
                        }
                    }
                    UI.getMessage().setText(""); //Clear text field
                }
            }
        });
    }

    private void individualSend(UserLogic t) {
        //This writes on others screen
        if(t != UserLogic.this && t != null && ((String)UI.getContacts().getSelectedValue()).equals(t.username)){
            if(((String)t.UI.getContacts().getSelectedValue()).equals(username)) { //If both users are directly communicating
                t.UI.getProperList().addElement(username + "> " + UI.getMessage().getText()); //Send message
                t.UI.getContacts().setCellRenderer(clearNotification); //Remove notification
            }
            else{ //If the user are not directly communicating
                t.UI.getContacts().setCellRenderer(setNotification); //Set notification
            }
            if(t.chats.containsKey(username)){ //Store chats in other users database (When database exists)
                ArrayList<String> arrayList = t.chats.get(username); //Get data
                arrayList.add(username + "> " + UI.getMessage().getText()); //Add new message to the database
                t.chats.put(username, arrayList); //Store data
            }
            else { //When database doesn't exist
                ArrayList<String> arrayList = new ArrayList<>(); //create new database
                arrayList.add(username + "> " + UI.getMessage().getText()); //Add new message to the database
                t.chats.put(username, arrayList); //Store data
            }
        }
        //This writes on my screen
        if(t == UserLogic.this && t!=null){ //On the current user side
            UI.getProperList().addElement("Me> " + UI.getMessage().getText()); //Write message to screen
            String currentChat = (String) UI.getContacts().getSelectedValue(); //Get selected user
            if(chats.containsKey(currentChat)){ //check if database exists
                ArrayList<String> arrayList = chats.get(currentChat);
                arrayList.add("Me> " + UI.getMessage().getText());
                chats.put(currentChat, arrayList);
            }
            else { //When database doesn't exist
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("Me> " + UI.getMessage().getText());
                chats.put(currentChat, arrayList);
            }
        }
    }

    private void groupChatSend(UserLogic t) { //When the current chat is the group chat
        if(((String) UI.getContacts().getSelectedValue()).equals( groupChat)){
            if (t != UserLogic.this && t != null) { //Sending to other users chat
                if(((String)t.UI.getContacts().getSelectedValue()).equals(groupChat)) {
                    t.UI.getProperList().addElement(username + "> " + UI.getMessage().getText());
                    t.UI.getContacts().setCellRenderer(clearNotification);
                }
                else{
                    t.UI.getContacts().setCellRenderer(setNotification);
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

            if(t == UserLogic.this && t!=null){ //Send to my screen
                UI.getProperList().addElement("Me> " + UI.getMessage().getText());
                if(chats.containsKey(groupChat)){ //Database group chat exists
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

    private void attachButtonEvent(){ //Attachment event
        attachButton = UI.getAttachmentButton();
        attachButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(); //JFileChooser

                int option = chooser.showOpenDialog(UI.getUIFrame()); //Open file chooser
                if(option == JFileChooser.APPROVE_OPTION) { //When file is chosen
                    try
                    {
                        for (UserLogic t : threads) { //Go through users
                            String extension = (chooser.getSelectedFile().getName()); //Extract filename
                            String format = extension.substring(extension.indexOf(".")+1,extension.length()); //Pull format/extension
                            if(!(format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg"))) { //Other files
                                if (t != UserLogic.this && t != null) { //This is to send to the other users
                                    t.img = ImageIO.read(chooser.getSelectedFile()); //Create image file
                                    int jOption = JOptionPane.showConfirmDialog(t.UI.getUIFrame(), "Would you like to receive this file?", username + " sent a file", JOptionPane.NO_OPTION);
                                    if (jOption == JOptionPane.OK_OPTION) { //Check if the user wants to receive an image
                                        t.UI.getProperList().addElement(("File Received!")); //Write message
                                        Desktop.getDesktop().open(chooser.getSelectedFile()); //Open image
                                    } else {
                                        t.UI.getProperList().addElement(("File Denied!")); //Cancel image
                                    }
                                }
                                else if (t == UserLogic.this) { //Write on the current user
                                    UserLogic.this.UI.getProperList().addElement(("File Sent!"));
                                }
                            }
                            else{
                                if(t == UserLogic.this) { //Write on the current user
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

    private void imageButtonEvent(){ //Image button event
        imageButton = UI.getImageButton(); //get button object
        imageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(); //File choose

                int option = chooser.showOpenDialog(UI.getUIFrame()); //Open file chooser
                if(option == JFileChooser.APPROVE_OPTION) { //File choosen
                    try
                    {
                        for (UserLogic t : threads) { //Go through all users
                            String extension = (chooser.getSelectedFile().getName()); //Get filename
                            String format = extension.substring(extension.indexOf(".")+1,extension.length()); //Get extension
                            if(format.equalsIgnoreCase("png") || format.equalsIgnoreCase("jpg") || format.equalsIgnoreCase("jpeg")) { //Image format
                                if (t != UserLogic.this && t != null) { //Other users
                                    t.img = ImageIO.read(chooser.getSelectedFile()); //Create image object
                                    int jOption = JOptionPane.showConfirmDialog(t.UI.getUIFrame(), "Would you like to receive the image?", t.username + " sent an image", JOptionPane.NO_OPTION);
                                    if (jOption == JOptionPane.OK_OPTION) { //Check if image wants to be received
                                        t.UI.getProperList().addElement(("Image Received!"));
                                        Desktop.getDesktop().open(chooser.getSelectedFile()); //Open image
                                    } else {
                                        t.UI.getProperList().addElement(("Image Denied!")); //Cancel
                                    }
                                }
                                else if (t == UserLogic.this) {
                                    UserLogic.this.UI.getProperList().addElement(("Image Sent!")); //Write to current user
                                }
                            }
                            else{
                                if(t == UserLogic.this) { //Write to current user
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

    private void userMessageEvent(){ //Text message Textfield event
        message = UI.getMessage(); //Get object
        message.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){ //Check if enter button is pressed
                    if(!UI.getMessage().getText().equalsIgnoreCase("")){ //If the message is not empty
                        for (UserLogic t : threads) { //Go through users
                            if(((String)UI.getContacts().getSelectedValue()).equals(groupChat)) { //If the chat is a group chat
                                groupChatSend(t);
                            }
                            else { //Current User
                                individualSend(t);
                            }
                        }
                        UI.getMessage().setText(""); //Clear message
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    private void contactsListEvent(){ //Selected contacts list event
        UI.getContacts().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                UI.getProperList().clear(); //Clear screen
                UI.getContacts().setCellRenderer(clearNotification); //Clear notification
                UI.getUserName().setText((String) UI.getContacts().getSelectedValue()); //Set selected username
                String currentChat = (String)UI.getContacts().getSelectedValue();
                if(chats.containsKey(currentChat)){ //If database exists
                    for(String s: chats.get(currentChat)){ //Load messages to screen
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

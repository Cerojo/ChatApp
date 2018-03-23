/**
 * Created by cecil on 10 Mar 2018.
 */
import java.awt.*;
import javax.swing.*;
//This class handles the GUI interface

public class UserInterface extends  JFrame{
    //Private variables
    private JPanel mainPanel;
    private JPanel chatsPanel;
    private JPanel chatPanel;
    private JPanel chatBottomArea;
    private JPanel contactsActionPanel;

    private JLabel appName;
    private JLabel userName;
    private JLabel chatsLabel;

    private JButton sendButton;
    private JButton attachmentButton;

    private JButton imageButton;

    private JTextField message;

    private JList messages;
    private DefaultListModel<String> properMessagesList;
    private JList contacts;
    private DefaultListModel<String> properContactsList;

    private JScrollPane messagesScroll;
    private JScrollPane contactsScroll;
    private String groupChat = "     Group Chat     ";

    UserInterface(){ //Constructor
        //JPanels
        mainPanel = new JPanel(new BorderLayout()); //Main Panel
        chatsPanel = new JPanel(new BorderLayout()); //Messages Panel
        chatPanel = new JPanel(new BorderLayout()); //Chat Panel
        chatPanel.setBackground(Color.CYAN);
        chatBottomArea = new JPanel(new FlowLayout()); //Sender Panel
        chatBottomArea.setBackground(Color.CYAN);
        contactsActionPanel = new JPanel();
        contactsActionPanel.setLayout(new BoxLayout(contactsActionPanel, BoxLayout.Y_AXIS));

        //JLabels
        appName = new JLabel("Chat App"); //App name
        appName.setHorizontalAlignment(SwingConstants.CENTER);
        userName = new JLabel("Group Chat"); //Group chat
        userName.setHorizontalAlignment(SwingConstants.CENTER);
        chatsLabel = new JLabel("Contacts"); //Contacts
        chatsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //Buttons
        sendButton = new JButton();
        ImageIcon icon = new ImageIcon("Icons/send.png"); //Get Icon
        Image img = icon.getImage() ;
        Image newimg = img.getScaledInstance( 20, 15,  java.awt.Image.SCALE_SMOOTH ) ; //Set icon size
        icon = new ImageIcon( newimg );
        sendButton.setIcon(icon); //Set Icon
        sendButton.setBackground(Color.GREEN);

        attachmentButton = new JButton();
        icon = new ImageIcon("Icons/attach.png"); //Get Icon
        img = icon.getImage() ;
        newimg = img.getScaledInstance( 20, 15,  java.awt.Image.SCALE_SMOOTH ) ; //Set icon size
        icon = new ImageIcon( newimg );
        attachmentButton.setIcon(icon); //Set Icon
        attachmentButton.setBackground(Color.CYAN);

        imageButton = new JButton();
        icon = new ImageIcon("Icons/image.png"); //GEt Icon
        img = icon.getImage() ;
        newimg = img.getScaledInstance( 20, 15,  java.awt.Image.SCALE_SMOOTH ) ; //Set icon size
        icon = new ImageIcon( newimg );
        imageButton.setIcon(icon); //set icon
        imageButton.setBackground(Color.CYAN);

        //JTextFields
        message = new JTextField("", 25); //Message area
        message.setFocusable(true);
        //JList
        properMessagesList = new DefaultListModel<>(); //Messages
        messages = new JList<>(properMessagesList);
        messages.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        messages.setSelectionModel(new DisabledItemSelectionModel());

        properContactsList = new DefaultListModel<>(); //Contacts
        properContactsList.addElement(groupChat);
        contacts = new JList<>(properContactsList);
        contacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        DefaultListCellRenderer cellRenderer = new DefaultListCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        contacts.setCellRenderer(cellRenderer);
        contacts.setSelectedIndex(0);

        //JScrollPane
        messagesScroll = new JScrollPane(messages, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        contactsScroll = new JScrollPane(contacts, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //Methods
        //mainPanel.add(appName, BorderLayout.NORTH);
        mainPanel.add(chatPanelMethod(), BorderLayout.CENTER);
        mainPanel.add(chatsPanelMethod(), BorderLayout.WEST);
    }

    void setupUI(){
        this.setSize(600, 400); //Frame size
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Dimension dimension = toolkit.getScreenSize();

        int xPos = (dimension.width/2) - (this.getWidth()/2);
        int yPos = (dimension.height/2) - (this.getHeight()/2);

        this.setLocation(xPos, yPos); //Frame location

        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Exit condition

        this.setTitle("Chat App");

        this.add(mainPanel);
    }

    void displayApp(){
        this.setVisible(true);
    } //Set the frame visible

    private JPanel chatPanelMethod(){ //Add objects to the frame
        chatPanel.add(userName, BorderLayout.NORTH);
        chatPanel.add(messagesScroll, BorderLayout.CENTER);
        chatBottomArea.add(message);
        chatBottomArea.add(attachmentButton);
        chatBottomArea.add(imageButton);
        chatBottomArea.add(sendButton);
        chatPanel.add(chatBottomArea, BorderLayout.SOUTH);
        return chatPanel;
    }

    private JPanel chatsPanelMethod(){
        chatsPanel.add(chatsLabel, BorderLayout.NORTH);
        chatsPanel.add(contactsScroll, BorderLayout.CENTER);
        chatsPanel.setBackground(Color.CYAN);
        return chatsPanel;
    }

    Frame getUIFrame(){
        return this;
    }

    class DisabledItemSelectionModel extends DefaultListSelectionModel {

        @Override
        public void setSelectionInterval(int index0, int index1) {
            super.setSelectionInterval(-1, -1);
        }
    }

    private void jFrameSetup() {
        this.setSize(600, 400);
        Toolkit toolkit = Toolkit.getDefaultToolkit();

        Dimension dimension = toolkit.getScreenSize();

        int xPos = (dimension.width/2) - (this.getWidth()/2);
        int yPos = (dimension.height/2) - (this.getHeight()/2);

        this.setLocation(xPos, yPos);

        this.setResizable(false);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setTitle("Chat App");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JLabel getAppName() {
        return appName;
    }

    public void setAppName(JLabel appName) {
        this.appName = appName;
    }

    public JLabel getUserName() {
        return userName;
    }

    public void setUserName(JLabel userName) {
        this.userName = userName;
    }

    public JButton getSendButton() {
        return sendButton;
    }

    public void setSendButton(JButton sendButton) {
        this.sendButton = sendButton;
    }

    public JButton getAttachmentButton() {
        return attachmentButton;
    }

    public void setAttachmentButton(JButton attachmentButton) {
        this.attachmentButton = attachmentButton;
    }

    public JTextField getMessage() {
        return message;
    }

    public void setMessage(JTextField message) {
        this.message = message;
    }

    public JPanel getChatsPanel() {
        return chatsPanel;
    }

    public void setChatsPanel(JPanel chatsPanel) {
        this.chatsPanel = chatsPanel;
    }

    public JList getMessages() {
        return messages;
    }

    public void setMessages(JList messages) {
        this.messages = messages;
    }

    public JPanel getChatPanel() {
        return chatPanel;
    }

    public void setChatPanel(JPanel chatPanel) {
        this.chatPanel = chatPanel;
    }

    public JPanel getChatBottomArea() {
        return chatBottomArea;
    }

    public void setChatBottomArea(JPanel chatBottomArea) {
        this.chatBottomArea = chatBottomArea;
    }

    public DefaultListModel getProperList() {
        return properMessagesList;
    }

    public void setProperList(DefaultListModel properList) {
        this.properMessagesList = properList;
    }

    public JScrollPane getMessagesScroll() {
        return messagesScroll;
    }

    public void setMessagesScroll(JScrollPane messagesScroll) {
        this.messagesScroll = messagesScroll;
    }

    public JScrollPane getContactsScroll() {
        return contactsScroll;
    }

    public void setContactsScroll(JScrollPane chatsScroll) {
        this.contactsScroll = chatsScroll;
    }

    public JLabel getChatsLabel() {
        return chatsLabel;
    }

    public void setChatsLabel(JLabel chatsLabel) {
        this.chatsLabel = chatsLabel;
    }

    public DefaultListModel<String> getProperContactsList() {
        return properContactsList;
    }

    public void setProperContactsList(DefaultListModel<String> properContactsList) {
        this.properContactsList = properContactsList;
    }

    public JList getContacts() {
        return contacts;
    }

    public JButton getImageButton() {
        return imageButton;
    }

    public void setImageButton(JButton imageButton) {
        this.imageButton = imageButton;
    }

    public void setContacts(JList contacts) {
        this.contacts = contacts;
    }
}

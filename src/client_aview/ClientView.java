package client_aview;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import client_business.Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

/**
 */
public class ClientView extends Thread {

    private JFrame frame;
    private JList userList;
    private JTextArea textArea;
    private JTextArea txt_msg;
    private JTextField txt_port;
    private JTextField txt_hostIP;
    private JTextField txt_name;
    private JButton btn_start;
    private JButton btn_stop;
    private JButton btn_send;
    private JButton btn_sendFile;
    private JPanel northPanel;
    private JPanel eastPanel;
    private JPanel southPanel;
    private JPanel panel;
    private JScrollPane rightScroll;
    private JScrollPane leftScroll;
    private JScrollPane msgScroll;
    private JSplitPane centerSplit;
    private JSplitPane rightSplit;

    private DefaultListModel listModel;

    private String currentUser;

    private String serverIP, serverPORT;

    private String chatUser;

    private boolean isGroup;

    private Client client;

    // private List<User> onlineUsers;

    private static String[] DEFAULT_FONT  = new String[] {
        "Table.font"
        , "TableHeader.font"
        , "CheckBox.font"
        , "Tree.font"
        , "Viewport.font"
        , "ProgressBar.font"
        , "RadioButtonMenuItem.font"
        , "ToolBar.font"
        , "ColorChooser.font"
        , "ToggleButton.font"
        , "Panel.font"
        , "TextArea.font"
        , "Menu.font"
        , "TableHeader.font"
        , "OptionPane.font"
        , "MenuBar.font"
        , "Button.font"
        , "Label.font"
        , "PasswordField.font"
        , "ScrollPane.font"
        , "MenuItem.font"
        , "ToolTip.font"
        , "List.font"
        , "EditorPane.font"
        , "Table.font"
        , "TabbedPane.font"
        , "RadioButton.font"
        , "CheckBoxMenuItem.font"
        , "TextPane.font"
        , "PopupMenu.font"
        , "TitledBorder.font"
        , "ComboBox.font"
    };

    public void run() {
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // new ClientView();
        initialGUI();
        addListeners();
    }

    public void setClientThread(Client client) {
        this.client = client;
    }

    // private User findUser(String username) {
    //     for (int i = 0; i < onlineUsers.size(); i++) {
    //         if (onlineUsers.get(i).getUsername().equals(username)) {
    //             return onlineUsers.get(i);
    //         }
    //     }
    //     return null;
    // }

    private void addListeners() {
        // 连接按钮点击
        btn_start.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                String port = txt_port.getText();
                String ip = txt_hostIP.getText();
                String user = txt_name.getText();

                if (ip.isEmpty() || port.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "IP地址和端口不能为�???",
                                                  "", JOptionPane.WARNING_MESSAGE);
                } else if (user.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "用户名不能为�???",
                                                  "", JOptionPane.WARNING_MESSAGE);
                } else {
                    serverIP = ip;
                    serverPORT = port;
                    currentUser = user;
                    listModel.addElement("GroupChat");
                    
                    client.connect(serverIP, Integer.parseInt(port), user);
                    isGroup = true;
                    chatUser = "GroupChat";
                    userList.setSelectedIndex(0);
                }
            }
        });

        // 断开按钮点击
        btn_stop.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
            	client.sendMessage("[OFFLINE]");
                client.disconnect();
                frame.dispose();
                System.exit(0);
            }
        });

        // 发�?�消�???
        btn_send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub

                String message = txt_msg.getText();
                if (!message.isEmpty() && !isGroup) {
                	System.out.println("p2p chat");
                    client.sendMessage("P2P[#]" + message + "[#]" + chatUser);
                    receiveMessage(currentUser, message);
                    txt_msg.setText("");
                } else if (!message.isEmpty() && isGroup) {
                	System.out.println("group chat");
                    client.sendMessage("GROUP[#]" + message);
                    txt_msg.setText("");
                    receiveMessage(currentUser, message);
                } else {
                    JOptionPane.showMessageDialog(frame, "消息不能为空",
                                                  "", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // 发�?�文�???
        btn_sendFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
                JFileChooser fd = new JFileChooser();
                fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fd.showOpenDialog(null);
                File f = fd.getSelectedFile();
                if (f != null) {
                	try {
                		String filename = f.getAbsolutePath();
                        if (isGroup) {
                          client.sendFile("GROUP[#]" + filename, filename);
                        } else {
                          client.sendFile("P2P[#]" + filename + "[#]" + chatUser, filename);
                        }
					} catch (Exception e2) {
						// TODO: handle exception
					}
                }
            }
        });

        // 切换窗口，点击左侧�?�择不同窗口对象
        userList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                // TODO Auto-generated method stub
                String content = (String) userList.getSelectedValue();
                int i = userList.getSelectedIndex();
                if (content != null && content.contains("(New Message)")) {
                    chatUser = content.substring(0, content.indexOf('('));
                    listModel.add(i, chatUser);
                    listModel.remove(i + 1);
                } else {
                    chatUser = content;
                }
                if (chatUser.contains("GroupChat")) {
 					isGroup =true;
 				}
                 else {
 					isGroup = false;
 				}
                textArea.setText("");
                System.out.println("OK");
                List<String> chatRecords = client.getChatRecords(chatUser);
                System.out.println("cr size in cv : " + chatRecords.size());
                for (int j = 0; j < chatRecords.size(); j++) {
                	StringTokenizer stringTokenizer = new StringTokenizer(chatRecords.get(j), "[#]");
                    receiveMessage(stringTokenizer.nextToken(), stringTokenizer.nextToken());
                }
            }
        });
    }

    private void initialGUI() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        UIManager.put("RootPane.setupButtonVisible", false);

        // 调整默认字体
        for (int i = 0; i < DEFAULT_FONT.length; i++)
            UIManager.put(DEFAULT_FONT[i], new Font("Microsoft YaHei UI", Font.PLAIN, 15));

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setForeground(Color.gray);

        txt_msg = new JTextArea();
        txt_port = new JTextField("8080");
        txt_hostIP = new JTextField();
        txt_name = new JTextField("");
        btn_start = new JButton("连接");
        btn_stop = new JButton("�???�???");
        btn_send = new JButton("发�??");
        btn_sendFile = new JButton("发�?�文�???");

        listModel = new DefaultListModel();
        userList = new JList(listModel);

        northPanel = new JPanel();

        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        northPanel.setLayout(gridBagLayout);

        constraints.insets = new Insets(0, 5, 0, 5);
        constraints.fill = GridBagConstraints.BOTH;

        JLabel label;

        constraints.weightx = 1.0;
        label = new JLabel("端口");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_port, constraints);
        northPanel.add(txt_port);

        constraints.weightx = 1.0;
        label = new JLabel("服务器IP");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_hostIP, constraints);
        northPanel.add(txt_hostIP);

        constraints.weightx = 1.0;
        label = new JLabel("姓名");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_name, constraints);
        northPanel.add(txt_name);
        gridBagLayout.setConstraints(btn_start, constraints);
        northPanel.add(btn_start);
        gridBagLayout.setConstraints(btn_stop, constraints);
        northPanel.add(btn_stop);

        northPanel.setBorder(new TitledBorder("连接信息"));

        rightScroll = new JScrollPane(textArea);
        rightScroll.setBorder(new TitledBorder("聊天消息"));

        leftScroll = new JScrollPane(userList);
        leftScroll.setBorder(new TitledBorder("在线用户"));

        msgScroll = new JScrollPane(txt_msg);

        southPanel = new JPanel(new BorderLayout());
        southPanel.add(msgScroll, "Center");

        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        btn_send.setMargin(new Insets(5, 20, 5, 20));
        btn_sendFile.setMargin(new Insets(5, 20, 5, 20));
        panel.add(btn_sendFile);
        panel.add(btn_send);

        southPanel.add(panel, "South");
        southPanel.setBorder(new TitledBorder("发�??"));

        rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightScroll, southPanel);
        rightSplit.setDividerLocation(350);

        eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(rightSplit, "Center");

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll,
                                     eastPanel);
        centerSplit.setDividerLocation(250);

        frame = new JFrame("有聊");
        frame.setSize(1024, 768);

        frame.setIconImage(toolkit.createImage(ClientView.class.getResource("chat.png")));

        frame.setLayout(new BorderLayout());
        frame.add(northPanel, "North");
        frame.add(centerSplit, "Center");

        int screenWidth = toolkit.getScreenSize().width;
        int screenHeight = toolkit.getScreenSize().height;

        frame.setLocation((screenWidth - frame.getWidth()) / 2,
                          (screenHeight - frame.getHeight()) / 2);

        frame.setVisible(true);
    }
    
    //把信息message显示到用户user的文字域�???
    public void receiveMessage(String user, String message) {
        textArea.append(user + " :\r\n");
        textArea.append("        ");
        textArea.append(message);
        textArea.append("\r\n\r\n");
    }
    //更新窗口，如果要更新的窗口就是当前窗口，那么直接添加文字，如果不是，就显示一条新消息提醒
    public void updateGUI(String command, String message, String sender) {
        if (command.equals("GROUP")) {
            if (chatUser.equals("GroupChat")) {
                receiveMessage(sender, message);
            } else {
                String name = (String)listModel.elementAt(0);
                listModel.remove(0);
                listModel.add(0, name + "(New Message)");
            }
            return;
        }

        if (command.equals("P2P")) {
            if (chatUser.equals(sender)) {
                receiveMessage(sender, message);
            } else {
                for (int i = 0; i < listModel.size(); i++) {
                    String name = (String)listModel.elementAt(i);
                    if (name.contains(sender)) {
                        listModel.remove(i);
                        listModel.add(i, name + "(New Message)");
                        return;
                    }
                }
            }
            return;
        }

        if (command.equals("ONLINE")) {
            listModel.addElement(message);
        }

        if (command.equals("OFFLINE")) {
            for (int i = 0; i < listModel.size(); i++) {
                String name = (String)listModel.elementAt(i);
                if (name.contains(message)) {
                    listModel.remove(i);
                    return;
                }
            }
        }

        if (command.equals("FILE")) {
            JOptionPane.showMessageDialog(frame, message, "系统消息", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}

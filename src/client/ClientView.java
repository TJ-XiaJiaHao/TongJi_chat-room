<<<<<<< HEAD
package client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.StringTokenizer;


public class ClientView extends Thread {
	public class normal {
		public static final String emptyIpPort = "IP地址和端口不能为空";
		public static final String emptyUser = "用户名不能为空";
		public static final String emptyMessage = "消息不能为空";
		public static final String port = "8080";
		public static final String connect = "连接";
		public static final String exit = "退出";
		public static final String send = "发送";
		public static final String sendFile = "发送文件";
		public static final String ip = "端口";
		public static final String hostIP = "服务器IP";
		public static final String userName = "姓名";
		public static final String connectInfo = "连接信息";
		public static final String messageContext = "聊天消息";
		public static final String onlineUser = "在线用户";
		public static final String title = 	"有聊";
		public static final String picUrl = "chat.png";
		public static final String systemMessage = "系统消息";
	}
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
                    JOptionPane.showMessageDialog(frame, normal.emptyIpPort, //"IP地址和端口不能为空",
                                                  "", JOptionPane.WARNING_MESSAGE);
                } else if (user.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, normal.emptyUser, //"用户名不能为空",
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
        
        // 发送消息
        btn_send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String message = txt_msg.getText();
                if (!message.isEmpty() && !isGroup) {
                    client.sendMessage("P2P[#]" + message + "[#]" + chatUser);
                    receiveMessage(currentUser, message);
                } else if (!message.isEmpty() && isGroup) {
                    client.sendMessage("GROUP[#]" + message);
                    receiveMessage(currentUser, message);
                } else {
                    JOptionPane.showMessageDialog(frame, normal.emptyMessage, //"消息不能为空",
                                                  "", JOptionPane.WARNING_MESSAGE);
                }
                txt_msg.setText("");
			}
		});
        
//        // 发送消息
//        btn_send.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                // TODO Auto-generated method stub
//
//                String message = txt_msg.getText();
//                if (!message.isEmpty() && !isGroup) {
//                    client.sendMessage("P2P[#]" + message + "[#]" + chatUser);
//                    receiveMessage(currentUser, message);
//                    txt_msg.setText("");
//                } else if (!message.isEmpty() && isGroup) {
//                    client.sendMessage("GROUP[#]" + message);
//                    txt_msg.setText("");
//                    receiveMessage(currentUser, message);
//                } else {
//                    JOptionPane.showMessageDialog(frame, "消息不能为空",
//                                                  "", JOptionPane.WARNING_MESSAGE);
//                }
//            }
//        });

        // 发送文件
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

        // 切换窗口
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
                List<String> chatRecords = client.getChatRecords(chatUser);
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
        txt_port = new JTextField(normal.port);//"8080");
        txt_hostIP = new JTextField();
        txt_name = new JTextField("");
        btn_start = new JButton(normal.connect); //"连接");
        btn_stop = new JButton(normal.exit); //"退出");
        btn_send = new JButton(normal.send); //"发送");
        btn_sendFile = new JButton(normal.sendFile); //"发送文件");

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
        label = new JLabel(normal.ip); //"端口");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_port, constraints);
        northPanel.add(txt_port);

        constraints.weightx = 1.0;
        label = new JLabel(normal.hostIP); //"服务器IP");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_hostIP, constraints);
        northPanel.add(txt_hostIP);

        constraints.weightx = 1.0;
        label = new JLabel(normal.userName); //"姓名");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_name, constraints);
        northPanel.add(txt_name);
        gridBagLayout.setConstraints(btn_start, constraints);
        northPanel.add(btn_start);
        gridBagLayout.setConstraints(btn_stop, constraints);
        northPanel.add(btn_stop);

        northPanel.setBorder(new TitledBorder(normal.connectInfo)); //"连接信息"));

        rightScroll = new JScrollPane(textArea);
        rightScroll.setBorder(new TitledBorder(normal.messageContext)); //"聊天消息"));

        leftScroll = new JScrollPane(userList);
        leftScroll.setBorder(new TitledBorder(normal.onlineUser)); //"在线用户"));

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
        southPanel.setBorder(new TitledBorder(normal.send)); //"发送"));

        rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightScroll, southPanel);
        rightSplit.setDividerLocation(350);

        eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(rightSplit, "Center");

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll,
                                     eastPanel);
        centerSplit.setDividerLocation(250);

        frame = new JFrame(normal.title); //"有聊");
        frame.setSize(1024, 768);

        frame.setIconImage(toolkit.createImage(ClientView.class.getResource(normal.picUrl))); //"chat.png")));

        frame.setLayout(new BorderLayout());
        frame.add(northPanel, "North");
        frame.add(centerSplit, "Center");

        int screenWidth = toolkit.getScreenSize().width;
        int screenHeight = toolkit.getScreenSize().height;

        frame.setLocation((screenWidth - frame.getWidth()) / 2,
                          (screenHeight - frame.getHeight()) / 2);

        frame.setVisible(true);
    }

    public void receiveMessage(String user, String message) {
        textArea.append(user + " :\r\n");
        textArea.append("        ");
        textArea.append(message);
        textArea.append("\r\n\r\n");
    }
    
    public interface updateGUI {
    	public void updateGUI(String command, String message, String sender);
    }
    
    public class updateForGroup implements updateGUI {
    	public updateForGroup() {
			// TODO Auto-generated constructor stub
		}
    	@Override
    	public void updateGUI(String command, String message, String sender) {
    		// TODO Auto-generated method stub
    		if (chatUser.equals("GroupChat")) {
                receiveMessage(sender, message);
            } else {
                String name = (String)listModel.elementAt(0);
                listModel.remove(0);
                listModel.add(0, name + "(New Message)");
            }
            return;
    	}
    }
    public class updateForOFFINE implements updateGUI {
    	@Override
    	public void updateGUI(String command, String message, String sender) {
    		// TODO Auto-generated method stub
    		for (int i = 0; i < listModel.size(); i++) {
                String name = (String)listModel.elementAt(i);
                if (name.contains(message)) {
                    listModel.remove(i);
                    return;
                }
            }
    	}
    }
    public class updateForFile implements updateGUI {

		@Override
		public void updateGUI(String command, String message, String sender) {
			// TODO Auto-generated method stub
			JOptionPane.showMessageDialog(frame, message, normal.systemMessage, JOptionPane.INFORMATION_MESSAGE); //"系统消息",
		}
    	
    }
    public class updateForP2P implements updateGUI {
    	@Override
    	public void updateGUI(String command, String message, String sender) {
    		// TODO Auto-generated method stub
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
    	}
    }
    public class updateForONLINE implements updateGUI {
    	@Override
    	public void updateGUI(String command, String message, String sender) {
    		// TODO Auto-generated method stub
    		listModel.addElement(message);
    	}
    }
    
    
//    
//    public void updateGUI(String command, String message, String sender) {
//        if (command.equals("GROUP")) {
//            if (chatUser.equals("GroupChat")) {
//                receiveMessage(sender, message);
//            } else {
//                String name = (String)listModel.elementAt(0);
//                listModel.remove(0);
//                listModel.add(0, name + "(New Message)");
//            }
//            return;
//        }
//
//        if (command.equals("P2P")) {
//            if (chatUser.equals(sender)) {
//                receiveMessage(sender, message);
//            } else {
//                for (int i = 0; i < listModel.size(); i++) {
//                    String name = (String)listModel.elementAt(i);
//                    if (name.contains(sender)) {
//                        listModel.remove(i);
//                        listModel.add(i, name + "(New Message)");
//                        return;
//                    }
//                }
//            }
//            return;
//        }
//
//        if (command.equals("ONLINE")) {
//            listModel.addElement(message);
//        }
//
//        if (command.equals("OFFLINE")) {
//            for (int i = 0; i < listModel.size(); i++) {
//                String name = (String)listModel.elementAt(i);
//                if (name.contains(message)) {
//                    listModel.remove(i);
//                    return;
//                }
//            }
//        }
//
//        if (command.equals("FILE")) {
//            JOptionPane.showMessageDialog(frame, message, "系统消息", JOptionPane.INFORMATION_MESSAGE);
//        }
//    }
}
=======
package client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.StringTokenizer;


public class ClientView extends Thread {
	public class normal {
		public static final String emptyIpPort = "IP地址和端口不能为空";
		public static final String emptyUser = "用户名不能为空";
		public static final String emptyMessage = "消息不能为空";
		public static final String port = "8080";
		public static final String connect = "连接";
		public static final String exit = "退出";
		public static final String send = "发送";
		public static final String sendFile = "发送文件";
		public static final String ip = "端口";
		public static final String hostIP = "服务器IP";
		public static final String userName = "姓名";
		public static final String connectInfo = "连接信息";
		public static final String messageContext = "聊天消息";
		public static final String onlineUser = "在线用户";
		public static final String title = 	"有聊";
		public static final String picUrl = "chat.png";
		public static final String systemMessage = "系统消息";
	}
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
                    JOptionPane.showMessageDialog(frame, normal.emptyIpPort, //"IP地址和端口不能为空",
                                                  "", JOptionPane.WARNING_MESSAGE);
                } else if (user.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, normal.emptyUser, //"用户名不能为空",
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
        
        // 发送消息
        btn_send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String message = txt_msg.getText();
                if (!message.isEmpty() && !isGroup) {
                    client.sendMessage("P2P[#]" + message + "[#]" + chatUser);
                    receiveMessage(currentUser, message);
                } else if (!message.isEmpty() && isGroup) {
                    client.sendMessage("GROUP[#]" + message);
                    receiveMessage(currentUser, message);
                } else {
                    JOptionPane.showMessageDialog(frame, normal.emptyMessage, //"消息不能为空",
                                                  "", JOptionPane.WARNING_MESSAGE);
                }
                txt_msg.setText("");
			}
		});
        
//        // 发送消息
//        btn_send.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                // TODO Auto-generated method stub
//
//                String message = txt_msg.getText();
//                if (!message.isEmpty() && !isGroup) {
//                    client.sendMessage("P2P[#]" + message + "[#]" + chatUser);
//                    receiveMessage(currentUser, message);
//                    txt_msg.setText("");
//                } else if (!message.isEmpty() && isGroup) {
//                    client.sendMessage("GROUP[#]" + message);
//                    txt_msg.setText("");
//                    receiveMessage(currentUser, message);
//                } else {
//                    JOptionPane.showMessageDialog(frame, "消息不能为空",
//                                                  "", JOptionPane.WARNING_MESSAGE);
//                }
//            }
//        });

        // 发送文件
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

        // 切换窗口
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
                List<String> chatRecords = client.getChatRecords(chatUser);
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
        txt_port = new JTextField(normal.port);//"8080");
        txt_hostIP = new JTextField();
        txt_name = new JTextField("");
        btn_start = new JButton(normal.connect); //"连接");
        btn_stop = new JButton(normal.exit); //"退出");
        btn_send = new JButton(normal.send); //"发送");
        btn_sendFile = new JButton(normal.sendFile); //"发送文件");

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
        label = new JLabel(normal.ip); //"端口");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_port, constraints);
        northPanel.add(txt_port);

        constraints.weightx = 1.0;
        label = new JLabel(normal.hostIP); //"服务器IP");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_hostIP, constraints);
        northPanel.add(txt_hostIP);

        constraints.weightx = 1.0;
        label = new JLabel(normal.userName); //"姓名");
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_name, constraints);
        northPanel.add(txt_name);
        gridBagLayout.setConstraints(btn_start, constraints);
        northPanel.add(btn_start);
        gridBagLayout.setConstraints(btn_stop, constraints);
        northPanel.add(btn_stop);

        northPanel.setBorder(new TitledBorder(normal.connectInfo)); //"连接信息"));

        rightScroll = new JScrollPane(textArea);
        rightScroll.setBorder(new TitledBorder(normal.messageContext)); //"聊天消息"));

        leftScroll = new JScrollPane(userList);
        leftScroll.setBorder(new TitledBorder(normal.onlineUser)); //"在线用户"));

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
        southPanel.setBorder(new TitledBorder(normal.send)); //"发送"));

        rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightScroll, southPanel);
        rightSplit.setDividerLocation(350);

        eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(rightSplit, "Center");

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll,
                                     eastPanel);
        centerSplit.setDividerLocation(250);

        frame = new JFrame(normal.title); //"有聊");
        frame.setSize(1024, 768);

        frame.setIconImage(toolkit.createImage(ClientView.class.getResource(normal.picUrl))); //"chat.png")));

        frame.setLayout(new BorderLayout());
        frame.add(northPanel, "North");
        frame.add(centerSplit, "Center");

        int screenWidth = toolkit.getScreenSize().width;
        int screenHeight = toolkit.getScreenSize().height;

        frame.setLocation((screenWidth - frame.getWidth()) / 2,
                          (screenHeight - frame.getHeight()) / 2);

        frame.setVisible(true);
    }

    public void receiveMessage(String user, String message) {
        textArea.append(user + " :\r\n");
        textArea.append("        ");
        textArea.append(message);
        textArea.append("\r\n\r\n");
    }
    
    public interface updateGUI {
    	public void updateGUI(String command, String message, String sender);
    }
    
    public class updateForGroup implements updateGUI {
    	public updateForGroup() {
			// TODO Auto-generated constructor stub
		}
    	@Override
    	public void updateGUI(String command, String message, String sender) {
    		// TODO Auto-generated method stub
    		if (chatUser.equals("GroupChat")) {
                receiveMessage(sender, message);
            } else {
                String name = (String)listModel.elementAt(0);
                listModel.remove(0);
                listModel.add(0, name + "(New Message)");
            }
            return;
    	}
    }
    public class updateForOFFINE implements updateGUI {
    	@Override
    	public void updateGUI(String command, String message, String sender) {
    		// TODO Auto-generated method stub
    		for (int i = 0; i < listModel.size(); i++) {
                String name = (String)listModel.elementAt(i);
                if (name.contains(message)) {
                    listModel.remove(i);
                    return;
                }
            }
    	}
    }
    public class updateForFile implements updateGUI {

		@Override
		public void updateGUI(String command, String message, String sender) {
			// TODO Auto-generated method stub
			JOptionPane.showMessageDialog(frame, message, normal.systemMessage, JOptionPane.INFORMATION_MESSAGE); //"系统消息",
		}
    	
    }
    public class updateForP2P implements updateGUI {
    	@Override
    	public void updateGUI(String command, String message, String sender) {
    		// TODO Auto-generated method stub
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
    	}
    }
    public class updateForONLINE implements updateGUI {
    	@Override
    	public void updateGUI(String command, String message, String sender) {
    		// TODO Auto-generated method stub
    		listModel.addElement(message);
    	}
    }
    
    
//    
//    public void updateGUI(String command, String message, String sender) {
//        if (command.equals("GROUP")) {
//            if (chatUser.equals("GroupChat")) {
//                receiveMessage(sender, message);
//            } else {
//                String name = (String)listModel.elementAt(0);
//                listModel.remove(0);
//                listModel.add(0, name + "(New Message)");
//            }
//            return;
//        }
//
//        if (command.equals("P2P")) {
//            if (chatUser.equals(sender)) {
//                receiveMessage(sender, message);
//            } else {
//                for (int i = 0; i < listModel.size(); i++) {
//                    String name = (String)listModel.elementAt(i);
//                    if (name.contains(sender)) {
//                        listModel.remove(i);
//                        listModel.add(i, name + "(New Message)");
//                        return;
//                    }
//                }
//            }
//            return;
//        }
//
//        if (command.equals("ONLINE")) {
//            listModel.addElement(message);
//        }
//
//        if (command.equals("OFFLINE")) {
//            for (int i = 0; i < listModel.size(); i++) {
//                String name = (String)listModel.elementAt(i);
//                if (name.contains(message)) {
//                    listModel.remove(i);
//                    return;
//                }
//            }
//        }
//
//        if (command.equals("FILE")) {
//            JOptionPane.showMessageDialog(frame, message, "系统消息", JOptionPane.INFORMATION_MESSAGE);
//        }
//    }
}
>>>>>>> a7d1e857f98f5c8713371ba671aa4c25df015acb

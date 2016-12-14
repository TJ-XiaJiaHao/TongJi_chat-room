package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class ClientView extends Thread{
	//	定义各项常量
	public class constant {
		public static final String emptyIpPort = "IP地址和端口不能为空";
		public static final String emptyUser = "用户名不能为空";
		public static final String emptyMessage = "消息不能为空";
		public static final String illegalUsername = "用户名不能有[#]字符";
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
	
	//	界面控件
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
    
    //	clientview相关参数
    private String currentUser;		//	当前用户名
    private String serverIP, serverPORT;	//	当前ip，port
    private String chatUser;	//	与之聊天的用户名
    private boolean isGroup;	//	是否为群聊
    private boolean isOnline;	//	是否已登陆
    private Client client;	//	绑定业务层client
    
    //	字体设置
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
    
    //	GUI端主程序
    public void run() {
    	//	获取相关配置
    	try {
    		org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	//	初始化各项参数
    	initParams();
    	//	初始化界面
    	initGUI();
    	//	添加事件监听器
    	addListeners();
    }
    
    //	绑定业务层	client
    public void setClientThread(Client client) {
    	this.client = client;
    }
    
    //	初始化各项参数
    private void initParams() {
    	isGroup = false;
    	isOnline = false;
    }
    
    //	初始化界面
    private void initGUI() {
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
        UIManager.put("RootPane.setupButtonVisible", false);

        // 调整默认字体
        for (int i = 0; i < DEFAULT_FONT.length; i++)
            UIManager.put(DEFAULT_FONT[i], new Font("Microsoft YaHei UI", Font.PLAIN, 15));
        
        //设定消息输入框
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setForeground(Color.gray);

        //设定界面控件
        txt_msg = new JTextArea();
        txt_port = new JTextField(constant.port);
        txt_hostIP = new JTextField();
        txt_name = new JTextField("");
        btn_start = new JButton(constant.connect);
        btn_stop = new JButton(constant.exit); 
        btn_send = new JButton(constant.send); 
        btn_sendFile = new JButton(constant.sendFile); 

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
        label = new JLabel(constant.ip);
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_port, constraints);
        northPanel.add(txt_port);

        constraints.weightx = 1.0;
        label = new JLabel(constant.hostIP); 
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_hostIP, constraints);
        northPanel.add(txt_hostIP);

        constraints.weightx = 1.0;
        label = new JLabel(constant.userName); 
        gridBagLayout.setConstraints(label, constraints);
        northPanel.add(label);

        constraints.weightx = 3.0;
        gridBagLayout.setConstraints(txt_name, constraints);
        northPanel.add(txt_name);
        gridBagLayout.setConstraints(btn_start, constraints);
        northPanel.add(btn_start);
        gridBagLayout.setConstraints(btn_stop, constraints);
        northPanel.add(btn_stop);

        northPanel.setBorder(new TitledBorder(constant.connectInfo)); 

        rightScroll = new JScrollPane(textArea);
        rightScroll.setBorder(new TitledBorder(constant.messageContext)); 

        leftScroll = new JScrollPane(userList);
        leftScroll.setBorder(new TitledBorder(constant.onlineUser)); 

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
        southPanel.setBorder(new TitledBorder(constant.send)); 

        rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightScroll, southPanel);
        rightSplit.setDividerLocation(350);

        eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(rightSplit, "Center");

        centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll,
                                     eastPanel);
        centerSplit.setDividerLocation(250);

        frame = new JFrame(constant.title); 
        frame.setSize(1024, 768);

        frame.setIconImage(toolkit.createImage(ClientView.class.getResource(constant.picUrl)));

        frame.setLayout(new BorderLayout());
        frame.add(northPanel, "North");
        frame.add(centerSplit, "Center");

        int screenWidth = toolkit.getScreenSize().width;
        int screenHeight = toolkit.getScreenSize().height;

        frame.setLocation((screenWidth - frame.getWidth()) / 2,
                          (screenHeight - frame.getHeight()) / 2);

        frame.setVisible(true);
    }
    
    //	添加事件监听器
    private void addListeners() {

    	//	点击连接按钮
    	btn_start.addActionListener(new ActionListener() {	
    		
			@Override
			public void actionPerformed(ActionEvent e) {
				// 判断是否上线
				if(isOnline) return;
				
				String port = txt_port.getText();
				String ip = txt_hostIP.getText();
				String username = txt_name.getText();
				
				if (ip.isEmpty() || port.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, constant.emptyIpPort,
                                                  "", JOptionPane.WARNING_MESSAGE);
                } else if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, constant.emptyUser, 
                                                  "", JOptionPane.WARNING_MESSAGE);
                } else if(username.contains("[#]")) {
                	JOptionPane.showMessageDialog(frame, constant.illegalUsername, 
                            "", JOptionPane.WARNING_MESSAGE);
                } else {
                    serverIP = ip;
                    serverPORT = port;
                    currentUser = username;
                    listModel.addElement("GroupChat");
                    client.connect(serverIP, Integer.parseInt(port), username);
                    isGroup = true;
                    isOnline = true;
                    chatUser = "GroupChat";
                    userList.setSelectedIndex(0);
                }
			}
		});
    	
    	//	点击退出按钮
    	btn_stop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//	判断是否上线
				if(!isOnline) return;
				client.sendMessage("[OFFLINE]");
				client.disconnect();
				frame.dispose();
                System.exit(0);
			}
		});
    	
    	//    	点击关闭按钮
    	frame.addWindowListener(new WindowAdapter(){
    		public void windowClosing(WindowEvent e){
    			super.windowClosing(e);
    			if(isOnline) {
    				client.sendMessage("[OFFLINE]");
    				client.disconnect();
    			}
    			frame.dispose();
                System.exit(0);
    		}
    	});
    	
    	//	点击发送消息按钮
    	btn_send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//	判断是否上线
				if(!isOnline) return;
				
				String message = txt_msg.getText();
				if(message.isEmpty()) {
					JOptionPane.showMessageDialog(frame, constant.emptyMessage,
                            "", JOptionPane.WARNING_MESSAGE);
					return;
				}
                if (!isGroup) {
                    client.sendMessage("P2P[#]" + message + "[#]" + chatUser);
                    receiveMessage(currentUser, message);
                } else {
                    client.sendMessage("GROUP[#]" + message);
                    receiveMessage(currentUser, message);
                }
                txt_msg.setText("");	
			}
		});
    	
    	//	点击发送文件按钮
    	btn_sendFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!isOnline) return;
				JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.showOpenDialog(null);
                File file = fileChooser.getSelectedFile();
                if (file != null) {
                	try {
                		String filename = file.getAbsolutePath();
                        if (isGroup) {
                          client.sendFile("GROUP[#]" + filename, filename);
                        } else {
                          client.sendFile("P2P[#]" + filename + "[#]" + chatUser, filename);
                        }
					} catch (Exception e2) {
						System.out.println(e2);
					}
                }
				
			}
		});
    	
    	//	切换窗口
    	userList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String content = (String) userList.getSelectedValue();
                int i = userList.getSelectedIndex();
                
                //	判断是否有新消息	
                if (content != null && content.contains("(New Message)")) {
                    chatUser = content.substring(0, content.indexOf('('));
                    listModel.add(i, chatUser);
                    listModel.remove(i + 1);
                } else {
                    chatUser = content;
                }
                
                //	判断是否为群聊
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

    //	接收消息
    public void receiveMessage(String username, String message) {
    	textArea.append(username + " :\r\n");
        textArea.append("        ");
        textArea.append(message);
        textArea.append("\r\n\r\n");
    }

    //	策略模式-更新界面
    public interface updateGUI {
    	public void updateGUI(String message, String sender);
    }
    
    //	策略模式-群聊
    public class updateForGroup implements updateGUI {
    	public updateForGroup() {
			// TODO Auto-generated constructor stub
		}
    	@Override
    	public void updateGUI(String message, String sender) {
    		if ("GroupChat".equals(chatUser)) {
                receiveMessage(sender, message);
            } else {
                String name = (String)listModel.elementAt(0);
                listModel.remove(0);
                if(name.contains("(New Message)"))
                	listModel.add(0, name);
                else
                	listModel.add(0, name + "(New Message)");
            }
            return;
    	}
    }

    //	策略模式-上线
    public class updateForONLINE implements updateGUI {
    	@Override
    	public void updateGUI(String message, String sender) {
    		// TODO Auto-generated method stub
    		listModel.addElement(message);
    	}
    }
    
    //	策略模式-下线
    public class updateForOFFINE implements updateGUI {
    	@Override
    	public void updateGUI(String message, String sender) {
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
    
    //	策略模式-发送文件
    public class updateForFile implements updateGUI {

		@Override
		public void updateGUI(String message, String sender) {
			// TODO Auto-generated method stub
			JOptionPane.showMessageDialog(frame, message, constant.systemMessage, JOptionPane.INFORMATION_MESSAGE);
		}
    	
    }

    //	策略模式-私聊
    public class updateForP2P implements updateGUI {
    	@Override
    	public void updateGUI(String message, String sender) {
    		// TODO Auto-generated method stub
    		if (chatUser.equals(sender)) {
                receiveMessage(sender, message);
            } else {
                for (int i = 0; i < listModel.size(); i++) {
                    String name = (String)listModel.elementAt(i);
                    if (name.contains(sender)) {
                        listModel.remove(i);
                        if(name.contains("New Message"))
                        	listModel.add(i, name);
                        else
                        	listModel.add(i, name + "(New Message)");
                        return;
                    }
                }
            }
            return;
    	}
    }
}

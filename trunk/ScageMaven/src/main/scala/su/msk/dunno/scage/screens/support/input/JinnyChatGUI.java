package su.msk.dunno.scage.screens.support.input;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class JinnyChatGUI extends JFrame
	{
		private static final long serialVersionUID = -1670705173282345859L;

		JTextArea jTextArea_newmessage;
		private JScrollPane jScrollPane2;
		private JTextField jTextField_to;
		private JLabel jLabel_to;
		private JTextField jTextField_from;
		private JLabel jLabel_posts_from;
		JTextField jTextField_nickname;
		private JLabel jLabel_nickname;
		private JScrollPane jScrollPane1;
		JLabel jLabel_chat;
		private JTextField jTextField_threadid;
		private JLabel jLabel_threadid;

		JLabel jLabel_sendingmessage;
		private JLabel jLabel_updatingchat;

		boolean isAddPost = false;
		private boolean isCtrlPressed = false;

		public JinnyChatGUI() throws Exception
		{
			super();
			initGUI();
		}

		private void initGUI()
		{
			try {
				getContentPane().setLayout(null);
				{
					jScrollPane2 = new JScrollPane();
					getContentPane().add(jScrollPane2);
					jScrollPane2.setBounds(12, 606, 507, 68);
					{
						jTextArea_newmessage = new JTextArea();
						jScrollPane2.setViewportView(jTextArea_newmessage);
						jTextArea_newmessage.setBounds(12, 668, 324, 16);
					}
				}
				jTextArea_newmessage.addKeyListener(new KeyListener()
				{
					public void keyPressed(KeyEvent arg0)
					{
						switch(arg0.getKeyCode())
						{
						case KeyEvent.VK_CONTROL:
							isCtrlPressed = true;
							break;
						case KeyEvent.VK_ENTER:
							if(isCtrlPressed && !jLabel_sendingmessage.isVisible() && !jLabel_updatingchat.isVisible())
							{
								isAddPost = true;
								jLabel_sendingmessage.setVisible(true);
								/*    Calendar cal = Calendar.getInstance();
								    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
								    String time = sdf.format(cal.getTime());

								currentText.setLength(0);
								currentText.append(jLabel_chat.getText());
								String current_message = "<font color=red><b>"+jTextField_nickname.getText() +"&nbsp;</b></font>" +
										"("+time+") <br>" +
										"<font color=#666666 >"+jTextArea_newmessage.getText()+"</font> <br><br>";

								jLabel_chat.setText(currentText.insert(6, current_message).toString());*/
							}
							break;
						}
					}

					public void keyReleased(KeyEvent arg0)
					{
						switch(arg0.getKeyCode())
						{
						case KeyEvent.VK_CONTROL:
							isCtrlPressed = false;
							break;
						}
					}

					public void keyTyped(KeyEvent arg0) {}
				});
				{
					jScrollPane1 = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
					getContentPane().add(jScrollPane1);
					jScrollPane1.setBounds(12, 12, 507, 547);
					{
						String imgPath = System.getProperty("user.dir").replace("\\", "/")+"/pics/conata.jpg";

						jLabel_chat = new JLabel("<html><div align='center'><img src='file:///"+imgPath+"' /></div>");
						jLabel_chat.setVerticalAlignment(JLabel.TOP);
						jScrollPane1.setViewportView(jLabel_chat);
						jLabel_chat.setBounds(12, 12, 507, 547);
						jLabel_chat.setFont(new Font("", Font.PLAIN, 12));
						jLabel_chat.setBackground(Color.WHITE);
						jLabel_chat.setOpaque(true);
					}
					jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
				}
				{
					jLabel_nickname = new JLabel();
					getContentPane().add(jLabel_nickname);
					jLabel_nickname.setBounds(12, 571, 33, 23);
					jLabel_nickname.setText("Имя: ");
				}
				{
					jTextField_nickname = new JTextField();
					getContentPane().add(jTextField_nickname);
					jTextField_nickname.setBounds(50, 571, 73, 23);
					jTextField_nickname.setText("nickname");
				}
				{
					jLabel_sendingmessage = new JLabel();
					getContentPane().add(jLabel_sendingmessage);
					jLabel_sendingmessage.setBounds(12, 674, 323, 19);
					jLabel_sendingmessage.setText("Отправляем сообщение...");
					jLabel_sendingmessage.setVisible(false);
				}
				{
					jLabel_updatingchat = new JLabel();
					getContentPane().add(jLabel_updatingchat);
					jLabel_updatingchat.setBounds(12, 674, 323, 19);
					jLabel_updatingchat.setText("Обновляем чат...");
					jLabel_updatingchat.setVisible(false);
				}
				{
					jLabel_posts_from = new JLabel();
					getContentPane().add(jLabel_posts_from);
					jLabel_posts_from.setBounds(129, 571, 67, 23);
					jLabel_posts_from.setText("Посты, от:");
				}
				{
					jTextField_from = new JTextField();
					getContentPane().add(jTextField_from);
					jTextField_from.setBounds(195, 571, 30, 23);
				}
				{
					jLabel_to = new JLabel();
					getContentPane().add(jLabel_to);
					jLabel_to.setBounds(235, 571, 25, 23);
					jLabel_to.setText("до: ");
				}
				{
					jTextField_to = new JTextField();
					getContentPane().add(jTextField_to);
					jTextField_to.setBounds(260, 571, 30, 23);
				}
				{
					jLabel_threadid = new JLabel();
					getContentPane().add(jLabel_threadid);
					jLabel_threadid.setBounds(294, 571, 40, 23);
					jLabel_threadid.setText("Тред:");
				}
				{
					jTextField_threadid = new JTextField();
					getContentPane().add(jTextField_threadid);
					jTextField_threadid.setBounds(334, 571, 185, 23);
				}
				this.setTitle("jChat");
				this.setSize(547, 728);
				this.setResizable(false);
				this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				this.setLocationRelativeTo(null);
				this.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

        public static void main(String[] args) throws Exception {
            new JinnyChatGUI();
        }
	}
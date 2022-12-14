package demo;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

public class Receive extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Receive frame = new Receive();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Receive() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(10, 11, 414, 20);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnNewButton = new JButton("Receive mess");
		btnNewButton.setBounds(10, 74, 89, 23);
		contentPane.add(btnNewButton);
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			    try {
					ReceiveMess();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public void ReceiveMess() throws Exception {
		// thi???t l???p m??i tr?????ng cho JMS
		BasicConfigurator.configure();
		// thi???t l???p m??i tr?????ng cho JJNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		// t???o context
		Context ctx = new InitialContext(settings);
		// lookup JMS connection factory
		Object obj = ctx.lookup("ConnectionFactory");
		ConnectionFactory factory = (ConnectionFactory) obj;
		// lookup destination
		Destination destination = (Destination) ctx.lookup("dynamicQueues/thanthidet");
		// t???o connection
		Connection con = factory.createConnection("admin", "admin");
		// n???i ?????n MOM
		con.start();
		// t???o session
		Session session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
		// t???o consumer
		MessageConsumer receiver = session.createConsumer(destination);
		// blocked-method for receiving message - sync
		// receiver.receive();
		// Cho receiver l???ng nghe tr??n queue, ch???ng c?? message th?? notify - async
		System.out.println("T?? was listened on queue...");
		receiver.setMessageListener(new MessageListener() {
			
			public void onMessage(Message msg) {
				// TODO Auto-generated method stub
				try {
					if(msg instanceof TextMessage){
						TextMessage tm=(TextMessage)msg;
						String txt=tm.getText();
//						System.out.println("Nh???n ???????c "+txt);
						textField.setText(txt);
						msg.acknowledge();//g???i t??n hi???u ack
					}
					else if(msg instanceof ObjectMessage){
						ObjectMessage om=(ObjectMessage)msg;

//						System.out.println(om);
//
					}
						//others message type....
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
	}
}

package org.securechat.andriygoltsev.securechatapp.crypt;

import net.java.otr4j.OtrException;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.session.Session;
import net.java.otr4j.session.SessionID;
import net.java.otr4j.session.SessionImpl;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

import org.securechat.andriygoltsev.securechatapp.crypt.Connection;
import org.securechat.andriygoltsev.securechatapp.crypt.Message;
import org.securechat.andriygoltsev.securechatapp.crypt.ProcessedMessage;
import org.securechat.andriygoltsev.securechatapp.crypt.Server;

public class SecureClient {

	// TODO fix for Android
	static Logger logger = Logger.getGlobal();
	private final String account;
	private Session session;
	OtrPolicy policy;
	Connection connection;
	private MessageProcessor processor;
	private Queue<ProcessedMessage> processedMsgs = new LinkedList<ProcessedMessage>();

	public SecureClient(String account) {
		this.account = account;
	}

	public Session getSession() {
		return session;
	}

	public String getAccount() {
		return account;
	}

	public void setPolicy(OtrPolicy policy) {
		this.policy = policy;
	}

	public void send(String recipient, String s) throws OtrException {
		if (session == null) {
			final SessionID sessionID = new SessionID(account, recipient, "ORTProtocol");
			session = new SessionImpl(sessionID, new SecureChatOtrEngineHostImpl(this));
		}

		String outgoingMessage = session.transformSending(s);
		connection.send(recipient, outgoingMessage);
	}

	public void exit() throws OtrException {
		this.processor.stop();
		if (session != null) {
			session.endSession();
		}
		if (connection != null) {
			connection.close();
		}
	}

	public void receive(String sender, String s) throws OtrException {
		this.processor.enqueue(sender, s);
	}

	public void connect(Server server) {
		this.processor = new MessageProcessor();
		new Thread(this.processor).start();
		this.connection = server.connect(this);
	}

	public synchronized void secureSession(String recipient) throws OtrException {
		if (session == null) {
			final SessionID sessionID = new SessionID(account, recipient, "OtrProtocol");
			session = new SessionImpl(sessionID, new SecureChatOtrEngineHostImpl(this));
		}

		session.startSession();
	}

	public Connection getConnection() {
		return connection;
	}

	public ProcessedMessage pollReceivedMessage() {
		synchronized (processedMsgs) {
			ProcessedMessage m;
			while ((m = processedMsgs.poll()) == null) {
				try {
					processedMsgs.wait();
				} catch (InterruptedException e) {
				}
			}

			return m;
		}
	}

	class MessageProcessor implements Runnable {
		private final Queue<Message> messageQueue = new LinkedList<Message>();
		private boolean stopped;

		private void process(Message m) throws OtrException {
			if (session == null) {
				final SessionID sessionID = new SessionID(account, m.getSender(), "OTRProtocol");
				session = new SessionImpl(sessionID, new SecureChatOtrEngineHostImpl(SecureClient.this));
			}

			String receivedMessage = session.transformReceiving(m.getContent());
			synchronized (processedMsgs) {
				processedMsgs.add(new ProcessedMessage(m, receivedMessage));
				processedMsgs.notify();
			}
		}

		public void run() {
			synchronized (messageQueue) {
				while (true) {

					Message m = messageQueue.poll();

					if (m == null) {
						try {
							messageQueue.wait();
						} catch (InterruptedException e) {

						}
					} else {
						try {
							process(m);
						} catch (OtrException e) {
							e.printStackTrace();
						}
					}

					if (stopped)
						break;
				}
			}
		}

		public void enqueue(String sender, String s) {
			synchronized (messageQueue) {
				messageQueue.add(new Message(sender, s));
				messageQueue.notify();
			}
		}

		public void stop() {
			stopped = true;

			synchronized (messageQueue) {
				messageQueue.notify();
			}
		}
	}
}

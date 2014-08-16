package org.securechat.andriygoltsev.securechatapp.crypt;

import net.java.otr4j.OtrException;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.securechat.andriygoltsev.securechatapp.crypt.Connection;
import org.securechat.andriygoltsev.securechatapp.crypt.SecureClient;
import org.securechat.andriygoltsev.securechatapp.crypt.Server;


public class SecureServer implements Server {

	private XMPPConnection xmppConnection;
	
	public SecureServer(XMPPConnection xmppConnection) {
		this.xmppConnection = xmppConnection;
	}

	public void send(Connection sender, String recipient, String msg) throws OtrException {
		sender.send(recipient, msg);
	}

	public synchronized Connection connect(SecureClient client) {

		String connectionName = client.getAccount();
		Connection con = new Connection(xmppConnection, client, connectionName);
		
		// TODO start listening for new chats
		
		return con;
	}
}

package org.securechat.andriygoltsev.securechatapp.crypt;

import android.util.Log;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.securechat.andriygoltsev.securechatapp.crypt.SecureClient;
import org.securechat.andriygoltsev.securechatapp.crypt.Server;

import net.java.otr4j.OtrException;

public class Connection {

	private final SecureClient client;
	private final String connectionName;
	private Chat chat;
	private String sentMessage;
	private XMPPConnection xmppConnection;
	
	
	public String getSentMessage() {
		return sentMessage;
	}

	public Connection(XMPPConnection xmppConnection, SecureClient client, String connectionName) {
		this.client = client;
		this.connectionName = connectionName;
		this.xmppConnection = xmppConnection;
	}

	public SecureClient getClient() {
		return client;
	}

	@Override
	public String toString() {
		return "PriorityConnection{" +
				"connectionName='" + connectionName + '\'' +
				'}';
	}

	public void send(String recipient, String msg) {
		this.sentMessage = msg;
		
		if (chat == null) {

			try {
                Presence presence = new Presence(Presence.Type.available);
                presence.setMode(Presence.Mode.available);
                presence.setTo(recipient);
                presence.setStatus("I am here");
                xmppConnection.sendPacket(presence);

				setChat(ChatManager.getInstanceFor(xmppConnection).createChat(
						recipient, new MessageListener() {

							@Override
							public void processMessage(Chat ch, Message msg) {
								try {
									receive(msg.getFrom(), msg.getBody());
								} catch (OtrException e) {
                                    Log.e("---------------->","ERROR",e);
								}
							}
						}));
			} catch (Exception e) {
				Log.e("---------------->","ERROR",e);
			}
		}
		
		
		try {
			getChat().sendMessage(msg);
		} catch (NotConnectedException e) {
			Log.e("---------------->", "ERROR", e);
		} catch (XMPPException e) {
			Log.e("---------------->","ERROR",e);
		}
	}

	public void receive(String sender, String msg) throws OtrException {
		this.client.receive(sender, msg);
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public void close(){
		getChat().close();
		setChat(null);
	}
}
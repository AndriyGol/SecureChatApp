package org.securechat.andriygoltsev.securechatapp.crypt;

import net.java.otr4j.OtrException;


public interface Server {
	void send(Connection sender, String recipient, String msg) throws OtrException;

	Connection connect(SecureClient client);
}

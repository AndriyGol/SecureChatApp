package org.securechat.andriygoltsev.securechatapp.crypt;

import net.java.otr4j.OtrException;

/**
 * Created by andrey on 7/20/14.
 */
public interface Server {
    void send(Connection sender, String recipient, String msg)
            throws OtrException;

    Connection connect(DummyClient client);
}

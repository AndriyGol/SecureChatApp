package org.securechat.andriygoltsev.securechatapp.crypt;

import net.java.otr4j.OtrException;

/**
 * Created by andrey on 7/20/14.
 */
public class Connection {

    private final DummyClient client;
    private final String connectionName;
    private final Server server;

    public String getSentMessage() {
        return sentMessage;
    }

    private String sentMessage;

    public Connection(Server server, DummyClient client, String connectionName) {
        this.client = client;
        this.server = server;
        this.connectionName = connectionName;
    }

    public DummyClient getClient() {
        return client;
    }

    @Override
    public String toString() {
        return "PriorityConnection{" + "connectionName='" + connectionName
                + '\'' + '}';
    }

    public void send(String recipient, String msg) throws OtrException {
        this.sentMessage = msg;
        server.send(this, recipient, msg);
    }

    public void receive(String sender, String msg) throws OtrException {
        this.client.receive(sender, msg);
    }
}

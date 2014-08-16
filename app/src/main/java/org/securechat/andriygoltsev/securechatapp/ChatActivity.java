package org.securechat.andriygoltsev.securechatapp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import net.java.otr4j.OtrException;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.OtrPolicyImpl;
import net.java.otr4j.session.Session;
import net.java.otr4j.session.SessionID;
import net.java.otr4j.session.SessionImpl;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.securechat.andriygoltsev.securechatapp.crypt.*;


import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;


@ContentView(R.layout.fragment_chat)
public class ChatActivity extends RoboActivity {

    @InjectView(R.id.sendButton)
    private Button sendButton;

    @InjectView(R.id.editSendText)
    private EditText textView;

    @InjectView(R.id.statusTextView)
    private TextView statusTextView;

    private Server server;
    private Connection secureConnection;
    private SecureClient client1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmackAndroid.init(getApplicationContext());
        init();
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = textView.getText().toString();
                if(sendOverChat(text)) {
                    statusTextView.append(text + '\n');
                }
                textView.setText("");
            }
        });
    }

    private boolean sendOverChat(String msg){
        boolean success = false;
        if(server != null) {
            try {
//                String outgoingMessage = session.transformSending(msg);
//                chat.sendMessage(outgoingMessage);
                client1.send("andrey2@jabber.iitsp.com", msg);
                success = true;
            }
            catch (OtrException e) {

            }
        }
        return success;
    }

    private void init() {
        SASLAuthentication.supportSASLMechanism("DIGEST-MD5");
        ConnectionConfiguration config = new ConnectionConfiguration("jabber.iitsp.com",5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        config.setReconnectionAllowed(true);
//        config.setDebuggerEnabled(true);
        config.setSendPresence(true);
        //   config.setS( true );

        final XMPPConnection connection = new XMPPTCPConnection(config);

//        try {
//
//            //connection.
//            connection.connect();
//            connection.login("andrey1", "Andrey1");
//
//            Presence presence = new Presence(Presence.Type.available);
//            presence.setMode(Presence.Mode.available);
//            presence.setTo("andrey2@jabber.iitsp.com");
//            presence.setStatus("I am here");
//            connection.sendPacket(presence);
//
//
//
//            client1 = new SecureClient("andrey1@jabber.iitsp.com");
//            client1.setPolicy(new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ALLOW_V3
//                    | OtrPolicy.ERROR_START_AKE));
//            server = new SecureServer(connection,client1);
//
//            client1.connect(server);
//            client1.send("andrey2@jabber.iitsp.com", "<p>?OTRv23?\n" +
//                    "<span style=\"font-weight: bold;\">Bob@Wonderland/</span> has requested an <a href=\"http://otr.cypherpunks.ca/\">Off-the-Record private conversation</a>. However, you do not have a plugin to support that.\n" +
//                    "See <a href=\"http://otr.cypherpunks.ca/\">http://otr.cypherpunks.ca/</a> for more information.</p>");
//
//            connection.addPacketListener(new PacketListener() {
//                @Override
//                public void processPacket(Packet packet) throws SmackException.NotConnectedException {
//                    Log.w("---------------->", packet.toXML().toString());
//                    // Log.w("---------------->", packet.getXmlns());
//                }
//            }, new PacketFilter() {
//                @Override
//                public boolean accept(Packet packet) {
//                    return true;
//                }
//            });
//        }
//        catch (Exception e){
//            Log.e("---------------->","ERROR",e);
//        }

    }

    private void setChatText(final String userName, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.append(userName + ": " + text + '\n');
            }
        });
    }


//    class SecureServer implements Server {
//
//        private final Map<String, Connection> clients = new HashMap<String, Connection>();
//        private int conCount = 0;
//        private final XMPPConnection connection;
//        private Chat chat;
//        private final DummyClient client;
//
//        public SecureServer(XMPPConnection connection, DummyClient client){
//            this.connection = connection;
//            this.client = client;
//        }
//
//        @Override
//        public void send(Connection sender, String recipient, String msg) throws OtrException {
//
//            try {
//                chat.sendMessage(msg);
//            }
//            catch (Exception e){
//                Log.e("---------------->","ERROR",e);
//            }
//
//        }
//
//        @Override
//        public synchronized Connection connect(final DummyClient client) {
//            // Update the active connection.
//            //clients.put(client.getAccount(), con);
//            String connectionName = client.getAccount() + "." + conCount++;
//            final Connection con = new Connection(this, client, connectionName);
//            try {
//
//                chat = ChatManager.getInstanceFor(connection).createChat("andrey2@jabber.iitsp.com", new MessageListener() {
//                    @Override
//                    public void processMessage(Chat chat, Message message) {
//                        try {
//                            con.receive(message.getFrom(), message.getBody());
//                            org.securechat.andriygoltsev.securechatapp.crypt.Message m = client.pollReceivedMessage();
//                            setChatText(m.getSender(), m.getContent());
//                        }
//                        catch (Exception e){
//                            Log.e("---------------->","ERROR",e);
//                        }
//                    }
//                });
//
//            }
//            catch (Exception e){
//                Log.e("---------------->","ERROR",e);
//            }
//
//            return con;
//        }
//
//    }


}

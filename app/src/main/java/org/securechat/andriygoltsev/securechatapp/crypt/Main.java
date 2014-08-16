package org.securechat.andriygoltsev.securechatapp.crypt;

import net.java.otr4j.OtrPolicy;
import net.java.otr4j.OtrPolicyImpl;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SASLAuthentication.supportSASLMechanism("DIGEST-MD5");
		ConnectionConfiguration config = new ConnectionConfiguration("jabber.iitsp.com",5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setReconnectionAllowed(true);
//        config.setDebuggerEnabled(true);
        config.setSendPresence(true);
        //   config.setS( true );

        final XMPPConnection connection = new XMPPTCPConnection(config);

        try {

            //connection.
            connection.connect();
            connection.login("andrey1", "Andrey1");

            Presence presence = new Presence(Presence.Type.available);
            presence.setMode(Presence.Mode.available);
            presence.setTo("andrey2@jabber.iitsp.com");
            presence.setStatus("I am here");
            connection.sendPacket(presence);

    		SecureClient bob = new SecureClient("andrey2@jabber.iitsp.com");
    		bob.setPolicy(new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ALLOW_V3
    				| OtrPolicy.ERROR_START_AKE));
    		bob.connect(new SecureServer(connection));
    		
    		bob.send("andrey2@jabber.iitsp.com", "<p>?OTRv23?\n" + " OTR???");
    		bob.pollReceivedMessage();
    		bob.pollReceivedMessage();
    		//Thread.currentThread().sleep(20000);
    		bob.send("andrey2@jabber.iitsp.com", " Yo, lets talk through OTR");
//            client1 = new DummyClient("andrey1@jabber.iitsp.com");
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
        }
        catch (Exception e){
            //Log.e("---------------->","ERROR",e);
        }

        

	}

}

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
import android.widget.RelativeLayout;
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

import java.io.IOException;

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

    @InjectView(R.id.chatLayout)
    private RelativeLayout chatLayout;

    @InjectView(R.id.signInLayout)
    private RelativeLayout signInLayout;

    @InjectView(R.id.name)
    private EditText userName;

    @InjectView(R.id.password)
    private EditText password;

    @InjectView(R.id.chatWith)
    private EditText chatWith;

    @InjectView(R.id.port)
    private EditText port;

    @InjectView(R.id.server)
    private EditText server;

    @InjectView(R.id.disconnectButton)
    private Button disconnectButton;

    @InjectView(R.id.connect)
    private Button connectButton;

    private XMPPConnection xmppConnection;

    private SecureClient secureClient;

    private ConnectionParams connectionParams;

    private Runnable messageListener;

    static public class ConnectionParams {

        private String port;
        private String serverName;
        private String chatWith;
        private String userName;
        private String password;

        public ConnectionParams(String serverName, String port, String userName, String password, String chatWith) {
            this.serverName = serverName;
            this.port = port;
            this.chatWith = chatWith;
            this.userName = userName;
            this.password = password;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getChatWith() {
            return chatWith;
        }

        public void setChatWith(String chatWith) {
            this.chatWith = chatWith;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }


        public String getPort() {
            return port;
        }

        public void setPort(String port) {
            this.port = port;
        }


        public String getServerName() {
            return serverName;
        }

        public void setServerName(String serverName) {
            this.serverName = serverName;
        }



    }

    private boolean chatView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmackAndroid.init(getApplicationContext());
        initHandlers();
        init();
        setChatView(false);

    }

    private void init() {
    }

    private void initHandlers(){
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    secureClient.send(connectionParams.getChatWith(), textView.getText().toString());
                    setChatText("me",textView.getText().toString());
                    textView.setText("");
                }
                catch (Exception e){
                    Log.e("---------------->","ERROR",e);
                    disconnect();
                }
            }
        });

        disconnectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });

        connectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String uName = userName.getText().toString();
                String serv = server.getText().toString();

                if(!uName.contains("@")){
                    uName += "@" + serv;
                }
                connectionParams = new ConnectionParams(serv, port.getText().toString(), uName, password.getText().toString(), chatWith.getText().toString());

                connectXMPP(connectionParams);
            }
        });
    }

    private void disconnect(){
        // go to sign in screen
        setChatView(false);
        statusTextView.setText("");
        messageListener = null;
        try {
            secureClient.exit();
        }
        catch (Exception e){
            Log.e("---------------->","ERROR",e);
        }
        secureClient = null;
    }

    private void connectXMPP(ConnectionParams params){
        SASLAuthentication.supportSASLMechanism("DIGEST-MD5");
        try {
            ConnectionConfiguration config = new ConnectionConfiguration(params.getServerName(), new Integer(params.getPort()));
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
            config.setReconnectionAllowed(true);
            config.setSendPresence(true);
            config.setDebuggerEnabled(true);
            xmppConnection = new XMPPTCPConnection(config);
            xmppConnection.connect();
            xmppConnection.login(params.getUserName(), params.getPassword());

            secureClient = new SecureClient(params.getUserName());
            secureClient.setPolicy(new OtrPolicyImpl(OtrPolicy.ALLOW_V2 | OtrPolicy.ALLOW_V3 | OtrPolicy.ERROR_START_AKE));
            secureClient.connect(new SecureServer(xmppConnection));
            secureClient.send(params.getChatWith(), "?OTRv23?\n" + " Lets start OTR???");

            // handshake
            secureClient.pollReceivedMessage();
            secureClient.pollReceivedMessage();

            new Thread(messageListener = new Runnable() {
                @Override
                public void run() {
                    while(secureClient != null){
                        ProcessedMessage m = secureClient.pollReceivedMessage();
                        setChatText(m.getSender(), m.getContent());
                    }
                }
            }).start();


            setChatView(true);
        }
        catch (Exception e){
            Log.e("---------------->","ERROR",e);
        }
    }

    private void setChatText(final String userName, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.append(userName + ": " + text + '\n');
            }
        });
    }


    public boolean isChatView() {
        return chatView;
    }

    public void setChatView(boolean chatView) {
        this.chatView = chatView;
        if(chatView){
            signInLayout.setVisibility(View.GONE);
            chatLayout.setVisibility(View.VISIBLE);
            sendButton.setEnabled(true);
        }
        else {
            signInLayout.setVisibility(View.VISIBLE);
            chatLayout.setVisibility(View.GONE);
            sendButton.setEnabled(false);
        }

    }

}

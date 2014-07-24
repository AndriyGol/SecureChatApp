package org.securechat.andriygoltsev.securechatapp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.*;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.securechat.andriygoltsev.securechatapp.crypt.Test;

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

    private Chat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SmackAndroid.init(getApplicationContext());
        init();
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //textView.setText("Hello there");

                try {
                    (new Test()).testMultipleSessions();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(chat != null) {
                    try {
                        String text = textView.getText().toString();
                        chat.sendMessage(text);
                        statusTextView.append(text + '\n');
                    }
                    catch (SmackException.NotConnectedException e){

                    }
                    catch(XMPPException e){

                    }
                }
                textView.setText("");
            }
        });
    }

    private void init(){
        SASLAuthentication.supportSASLMechanism("DIGEST-MD5");
        ConnectionConfiguration config = new ConnectionConfiguration("jabber.iitsp.com",5222);
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        config.setReconnectionAllowed(true);
        config.setDebuggerEnabled(true);
        //   config.setS( true );

        XMPPConnection connection = new XMPPTCPConnection(config);
        try {

            //connection.
            connection.connect();
            connection.login("andrey1", "Andrey1");
            chat = ChatManager.getInstanceFor(connection).createChat("andrey2@jabber.iitsp.com", new MessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    setChatText(message.getBody());
                }
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setChatText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.append(text + '\n');
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.chat, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
            return rootView;
        }
    }
}

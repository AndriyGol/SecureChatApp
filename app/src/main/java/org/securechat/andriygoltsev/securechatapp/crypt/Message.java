package org.securechat.andriygoltsev.securechatapp.crypt;

/**
 * Created by andrey on 7/20/14.
 */
public class Message {

    public Message(String sender, String content){
        this.sender = sender;
        this.content = content;
    }

    private final String sender;
    private final String content;

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }
}

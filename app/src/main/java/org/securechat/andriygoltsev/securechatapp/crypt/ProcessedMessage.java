package org.securechat.andriygoltsev.securechatapp.crypt;

/**
 * Created by andrey on 7/20/14.
 */
public class ProcessedMessage extends Message {

    final Message originalMessage;

    public ProcessedMessage(Message originalMessage, String content) {
        super(originalMessage.getSender(), content);
        this.originalMessage = originalMessage;
    }
}

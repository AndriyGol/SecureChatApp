package org.securechat.andriygoltsev.securechatapp.crypt;

import org.securechat.andriygoltsev.securechatapp.crypt.Message;

public class ProcessedMessage extends Message {

	final Message originalMessage;

	public ProcessedMessage(Message originalMessage, String content) {
		super(originalMessage.getSender(), content);
		this.originalMessage = originalMessage;
	}
}

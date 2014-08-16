package org.securechat.andriygoltsev.securechatapp.crypt;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import net.java.otr4j.OtrEngineHost;
import net.java.otr4j.OtrException;
import net.java.otr4j.OtrPolicy;
import net.java.otr4j.crypto.OtrCryptoEngineImpl;
import net.java.otr4j.crypto.OtrCryptoException;
import net.java.otr4j.session.InstanceTag;
import net.java.otr4j.session.SessionID;

// Copied from OTR lib
public class SecureChatOtrEngineHostImpl implements OtrEngineHost {

	/**
	 * 
	 */
	private final SecureClient client;

	/**
	 * @param secureClient
	 */
	public SecureChatOtrEngineHostImpl(SecureClient secureClient) {
		client = secureClient;
	}

	public void injectMessage(SessionID sessionID, String msg) throws OtrException {

		client.connection.send(sessionID.getUserID(), msg);

		String msgDisplay = (msg.length() > 10) ? msg.substring(0, 10)
				+ "..." : msg;
		SecureClient.logger.finest("IM injects message: " + msgDisplay);
	}

	public void smpError(SessionID sessionID, int tlvType, boolean cheated)
			throws OtrException {
		SecureClient.logger.severe("SM verification error with user: " + sessionID);
	}

	public void smpAborted(SessionID sessionID) throws OtrException {
		SecureClient.logger.severe("SM verification has been aborted by user: "
				+ sessionID);
	}

	public void finishedSessionMessage(SessionID sessionID, String msgText) throws OtrException {
		SecureClient.logger.severe("SM session was finished. You shouldn't send messages to: "
				+ sessionID);
	}

	public void finishedSessionMessage(SessionID sessionID) throws OtrException {
		SecureClient.logger.severe("SM session was finished. You shouldn't send messages to: "
				+ sessionID);
	}

	public void requireEncryptedMessage(SessionID sessionID, String msgText)
			throws OtrException {
		SecureClient.logger.severe("Message can't be sent while encrypted session is not established: "
				+ sessionID);
	}

	public void unreadableMessageReceived(SessionID sessionID)
			throws OtrException {
		SecureClient.logger.warning("Unreadable message received from: " + sessionID);
	}

	public void unencryptedMessageReceived(SessionID sessionID, String msg)
			throws OtrException {
		SecureClient.logger.warning("Unencrypted message received: " + msg + " from "
				+ sessionID);
	}

	public void showError(SessionID sessionID, String error)
			throws OtrException {
		SecureClient.logger.severe("IM shows error to user: " + error);
	}

	public String getReplyForUnreadableMessage() {
		return "You sent me an unreadable encrypted message.";
	}

	public void sessionStatusChanged(SessionID sessionID) {
		// don't care.
	}

	public KeyPair getLocalKeyPair(SessionID paramSessionID) {
		KeyPairGenerator kg;
		try {
			kg = KeyPairGenerator.getInstance("DSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		return kg.genKeyPair();
	}

	public OtrPolicy getSessionPolicy(SessionID ctx) {
		return client.policy;
	}

	public void askForSecret(SessionID sessionID, String question) {
		SecureClient.logger.finest("Ask for secret from: " + sessionID + ", question: "
				+ question);
	}

	public void verify(SessionID sessionID, boolean approved) {
		SecureClient.logger.finest("Session was verified: " + sessionID);
		if (!approved)
			SecureClient.logger.finest("Your answer for the question was verified."
					+ "You should ask your opponent too or check shared secret.");
	}

	public void unverify(SessionID sessionID) {
		SecureClient.logger.finest("Session was not verified: " + sessionID);
	}

	public byte[] getLocalFingerprintRaw(SessionID sessionID) {
		try {
			return new OtrCryptoEngineImpl()
					.getFingerprintRaw(getLocalKeyPair(sessionID)
							.getPublic());
		} catch (OtrCryptoException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void askForSecret(SessionID sessionID, InstanceTag receiverTag, String question) {

	}

	public void verify(SessionID sessionID, String fingerprint, boolean approved) {

	}

	public void unverify(SessionID sessionID, String fingerprint) {

	}

	public String getReplyForUnreadableMessage(SessionID sessionID) {
		return null;
	}

	public String getFallbackMessage(SessionID sessionID) {
		return null;
	}

	public void messageFromAnotherInstanceReceived(SessionID sessionID) {

	}

	public void multipleInstancesDetected(SessionID sessionID) {

	}

	public String getFallbackMessage() {
		return "Off-the-Record private conversation has been requested. However, you do not have a plugin to support that.";
	}
}
package com.exec.mailer;

public interface IMessaging {
	public final String TELERIVET_API_KEY = "ZzObm151IujT9eE2EaS5DDlvJldf2mnb";
	public final String TELERIVET_PROJECT_ID = "PJ2f70d382063bf63e";
	public final String TO_NUMBER  = "to_number";
	public final String CONTENT = "content";
	Boolean sendMail(final String numberToSendMessage,final String actualMessageToBeSent);
}

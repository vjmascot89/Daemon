package com.exec.mailer;

import java.io.IOException;

import com.telerivet.Message;
import com.telerivet.Project;
import com.telerivet.TelerivetAPI;
import com.telerivet.Util;



public class SmsMessaging implements IMessaging {
	//"Hello_Word telerivet"
	static final String logTag = "SmsMessaging";
	@Override
	public Boolean sendMail(final String numberToSendMessage,final String actualMessageToBeSent) {
		TelerivetAPI telerivetAPI = new TelerivetAPI( IMessaging.TELERIVET_API_KEY);
		Project project = telerivetAPI.initProjectById(IMessaging.TELERIVET_PROJECT_ID);
		//Send the message
		Message message = null;
		try {
			message = project.sendMessage(Util.options(IMessaging.TO_NUMBER,numberToSendMessage,IMessaging.CONTENT,actualMessageToBeSent));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(logTag+ ": Message status " +message.getStatus());
		System.out.println(logTag+ ": Message Number " +numberToSendMessage);
		System.out.println(logTag+ ": Message " +actualMessageToBeSent);
		return true;
	}

}

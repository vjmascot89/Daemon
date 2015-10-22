package com.exec.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.exec.mailer.IMessaging;
import com.exec.module.UserSignInPage;

public class UserEmailAction extends AbstractAction {

	public UserEmailAction(UserSignInPage userSignInPage, Session newSession,HttpServletRequest request,
			HttpServletResponse response, IMessaging iMessaging,
			String servletName, SessionFactory sessionFactory) {
		
		super(userSignInPage, newSession, request , response, iMessaging, servletName,
				sessionFactory);
	}

}

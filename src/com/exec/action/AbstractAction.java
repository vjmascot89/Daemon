package com.exec.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.exec.mailer.IMessaging;
import com.exec.model.LoginDataModel;
import com.exec.model.RequestResponseConstantsForSignInPage;
import com.exec.model.UserDataModel;
import com.exec.module.UserSignInPage;
import com.exec.util.UtilityMethodClass;
import com.exec.validator.RandomNumberGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractAction {

	private Session newSession;
	private HttpServletResponse response;
	private IMessaging iMessaging;
	private String servletName;
	private SessionFactory sessionFactory;
	private UserSignInPage userSignInPage;
	private HttpServletRequest request;

	protected AbstractAction(UserSignInPage userSignInPage, Session newSession,
			HttpServletRequest request, HttpServletResponse response,
			IMessaging iMessaging, String servletName,
			SessionFactory sessionFactory) {
		this.userSignInPage = userSignInPage;
		this.newSession = newSession;
		this.request = request;
		this.response = response;
		this.iMessaging = iMessaging;
		this.servletName = servletName;
		this.sessionFactory = sessionFactory;
	}

	public void performAction(LoginDataModel loginDataModel, String queryString) {
		List list = UtilityMethodClass.queryDataAndLoginObject(queryString,
				newSession);
		System.out.println(list.size());
		// if user exists
		if (list != null && !list.isEmpty()) {
			userSignInPage.log("User logged in time @"
					+ new Date().toGMTString());
			LoginDataModel validUser = (LoginDataModel) list.get(0);
			// user has hashcode and hashcode matches dbs hashcode then send
			// proper response
			if (loginDataModel.getUserOtpHashcode() != null
					&& !loginDataModel.getUserOtpHashcode().isEmpty()
					&& validUser != null
					&& validUser.getUserOtpHashcode() != null
					&& validUser.getUserOtpHashcode().equals(
							loginDataModel.getUserOtpHashcode())) {

				if ("true"
						.equals(request
								.getParameter(RequestResponseConstantsForSignInPage.USER_FORGOT_PASSWORD
										.toString()))
						&& !loginDataModel.getUserPassword().equals(
								validUser.getUserPassword())) {
					validUser.setUserPassword(loginDataModel.getUserPassword());
					persistAndNotify(validUser);
					if ("true"
							.equals(request
									.getParameter(RequestResponseConstantsForSignInPage.USER_FORGOT_PASSWORD
											.toString()))) {
						response.addHeader(
								RequestResponseConstantsForSignInPage.USER_FORGOT_PASSWORD
										.toString(), "false");
					}
				}
				response.addHeader(
						RequestResponseConstantsForSignInPage.USER_LOGGED_IN
								.toString(), "SUCCESS");
				queryString = "select U from UserDataModel U where U.userId="
						+ validUser.getUserId();
				String gsonObjectString = makeGsonObject(queryString);
				response.addHeader(
						RequestResponseConstantsForSignInPage.USER_LOGGING_TIME
								.toString(), new Date().toGMTString());
				response.addHeader(
						RequestResponseConstantsForSignInPage.USER_DATA_MODEL
								.toString(), gsonObjectString);

			}
			// user doesn't have hashcode then persist hashcode and wait for
			// further reply
			else {
				String currentOtpBeingSent = RandomNumberGenerator
						.generateRandom().toString();
				validUser.setUserOtpHashcode(currentOtpBeingSent);
				boolean otpMethodReply = iMessaging.sendMail(
						validUser.getUserMobile(), currentOtpBeingSent);
				if (otpMethodReply) {
					persistAndNotify(validUser);
				}
				if ("true"
						.equals(request
								.getParameter(RequestResponseConstantsForSignInPage.USER_FORGOT_PASSWORD
										.toString()))) {
					response.addHeader(
							RequestResponseConstantsForSignInPage.USER_FORGOT_PASSWORD
									.toString(), "true");
				}
			}

		} else {
			System.out.println("Wrong UserName or Password");
			response.addHeader(
					RequestResponseConstantsForSignInPage.USER_LOGGED_IN
							.toString(), "Wrong UserName or Password");
			response.addHeader(
					RequestResponseConstantsForSignInPage.USER_KEY.toString(),
					"-1");
		}
	}

	private String makeGsonObject(String queryString) {
		List list = UtilityMethodClass.queryDataAndLoginObject(queryString,
				newSession);
		if (list.size() > 0) {
			GsonBuilder gbuilder = new GsonBuilder();
			
			return  gbuilder.registerTypeAdapter(UserDataModel.class, (UserDataModel) list.get(0)).create().toJson((UserDataModel) list.get(0));
		}
		return null;
	}

	private void persistAndNotify(LoginDataModel validUser) {
		newSession.close();
		newSession = sessionFactory.openSession();
		try {
			newSession.beginTransaction();
			newSession.update(validUser);
			newSession.getTransaction().commit();
			response.addHeader("USER_EXISTS", "true");
			response.addHeader("USER_KEY", validUser.getUserId().toString());
			response.addHeader("USER_HASHCODE_PERSISTED", "true");
			response.addHeader("USER_HASHCODE", validUser.getUserOtpHashcode());
		} catch (Exception e) {
			response.addHeader("USER_EXISTS", "true");
			response.addHeader("USER_KEY", validUser.getUserId().toString());
			response.addHeader("USER_HASHCODE_PERSISTED", "false");
			System.out.println(servletName + e.getMessage());
			newSession.getTransaction().rollback();

		} finally {
			newSession.close();
		}
	}
}

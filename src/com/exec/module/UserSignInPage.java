package com.exec.module;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.exec.action.AbstractAction;
import com.exec.action.UserEmailAction;
import com.exec.action.UserEmailPasswordAction;
import com.exec.action.UserIdAction;
import com.exec.filler.UserModelFillerFromRequest;
import com.exec.mailer.IMessaging;
import com.exec.mailer.SmsMessaging;
import com.exec.model.LoginDataModel;
import com.exec.model.RequestResponseConstantsForSignInPage;
import com.exec.util.SignAction;

/**
 * Servlet implementation class UserSignInPage
 */
public class UserSignInPage extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String SERVLETNAME = "USERSIGNINPAGE";
	private SessionFactory sessionFactory;
	private Session newSession;
	private IMessaging iMessaging;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserSignInPage() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {

		sessionFactory = new Configuration().configure().buildSessionFactory();
		iMessaging = new SmsMessaging();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		newSession = sessionFactory.openSession();
		
		// setting up login model
		LoginDataModel loginDataModel = new LoginDataModel();
		fillDataAndLoginObject(request, loginDataModel);

		// make query string set caseAction
		String queryString = null;
		SignAction caseAction;

		if (loginDataModel.getUserId() != null
				&& loginDataModel.getUserId().intValue() > 0) {
			caseAction = SignAction.USERID;
			queryString = "select L from LoginDataModel L where L.userId="
					+ loginDataModel.getUserId();
		} 
		else if (loginDataModel.getUserEmail() != null
				&& "true"
						.equals(request
								.getParameter(RequestResponseConstantsForSignInPage.USER_FORGOT_PASSWORD
										.toString()))) {
			caseAction = SignAction.USEREMAIL;
			queryString = "select L from LoginDataModel L where L.userEmail='"
					+ loginDataModel.getUserEmail() + "'";
		} 
		else {
			caseAction = SignAction.USEREMAILPASSWORD;
			queryString = "select L from LoginDataModel L where (L.userEmail='"
					+ loginDataModel.getUserEmail() + "' and L.userPassword='"
					+ loginDataModel.getUserPassword() + "')";

		}

		// quering for the particular object
		performAction(request, response, loginDataModel, queryString,
				caseAction);

	}

	private void performAction(HttpServletRequest request,
			HttpServletResponse response, LoginDataModel loginDataModel,
			String queryString, SignAction action) {

		AbstractAction abstractAction = null;

		switch (action) {

		case USEREMAIL:
			abstractAction = new UserEmailAction(this, newSession, request,
					response, iMessaging, SERVLETNAME, sessionFactory);
			break;

		case USEREMAILPASSWORD:
			abstractAction = new UserEmailPasswordAction(this, newSession,
					request, response, iMessaging, SERVLETNAME, sessionFactory);
			break;

		case USERID:
			abstractAction = new UserIdAction(this, newSession, request,
					response, iMessaging, SERVLETNAME, sessionFactory);
			break;

		default:
			break;

		}

		if (abstractAction != null)
			abstractAction.performAction(loginDataModel, queryString);

	}

	@Override
	protected void finalize() throws Throwable {

		newSession.close();

	}

	private void fillDataAndLoginObject(HttpServletRequest request,
			LoginDataModel loginDataModel) {
		try {

			new UserModelFillerFromRequest<LoginDataModel>().userModelFiller(
					request, loginDataModel);

		} catch (Exception e) {

			System.out.println(e.getLocalizedMessage());

		}
	}

}

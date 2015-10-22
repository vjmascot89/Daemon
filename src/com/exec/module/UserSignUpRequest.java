package com.exec.module;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.exec.filler.UserModelFillerFromRequest;
import com.exec.model.LoginDataModel;
import com.exec.model.UserDataModel;
import com.exec.util.UtilityMethodClass;

/**
 * Servlet implementation class UserProfileEditor
 */
public class UserSignUpRequest extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SessionFactory sessionFactory;
	private Session newSession;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserSignUpRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() throws ServletException {
		sessionFactory = new Configuration().configure().buildSessionFactory();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		newSession = sessionFactory.openSession();
		LoginDataModel loginDataModel = new LoginDataModel();
		UserDataModel userDataModel = new UserDataModel();
		fillDataAndLoginObject(request, loginDataModel, userDataModel);
		String queryString = "select L.userEmail,L.userMobile from LoginDataModel L";
		List list = UtilityMethodClass.queryDataAndLoginObject(queryString,
				newSession);

		// System.out.println(userExists(list,
		// loginDataModel.getUserEmail(),loginDataModel.getUserMobile()));
		if (!UtilityMethodClass.userExists(list, loginDataModel.getUserEmail(),
				loginDataModel.getUserMobile())) {
			try {
				newSession.beginTransaction();
				Integer id = (Integer) newSession.save(loginDataModel);
				newSession.getTransaction().commit();
				response.addHeader("USER_EXISTS", "false");
				response.addHeader("USER_KEY", id.toString());
			} catch (Exception e) {
				newSession.getTransaction().rollback();
				response.addHeader("USER_NOT_INSERTED", e.getMessage());
				response.addHeader("USER_KEY", "-1");
				e.printStackTrace();
			} finally {
				newSession.close();
			}
		} else {
			System.out.println("USER EXISTS");
			response.addHeader("USER_EXISTS", "true");
			response.addHeader("USER_KEY", "-1");
		}
	}

	private void fillDataAndLoginObject(HttpServletRequest request,
			LoginDataModel loginDataModel, UserDataModel userDataModel) {
		try {

			new UserModelFillerFromRequest<LoginDataModel>().userModelFiller(
					request, loginDataModel);
			new UserModelFillerFromRequest<UserDataModel>().userModelFiller(
					request, userDataModel);
			loginDataModel.setUserDataModel(userDataModel);
			userDataModel.setUserLoginDataModel(loginDataModel);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
	}

}

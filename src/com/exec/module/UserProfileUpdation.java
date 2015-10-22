package com.exec.module;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;

import com.exec.filler.UserModelFillerFromRequest;
import com.exec.model.UserDataModel;
import com.exec.util.UtilityMethodClass;

/**
 * Servlet implementation class UserDataInsertion
 */
public class UserProfileUpdation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SessionFactory sessionFactory;
	private Session newSession;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserProfileUpdation() {
		super();
	}

	public void init(ServletConfig config) throws ServletException {
		sessionFactory = new Configuration().configure().buildSessionFactory();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UserDataModel userHibernateObject = new UserDataModel();

		try {
			new UserModelFillerFromRequest<UserDataModel>().userModelFiller(
					request, userHibernateObject);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println(userHibernateObject);
		synchronized (userHibernateObject.getUserId().toString().intern()) {
			newSession = sessionFactory.openSession();
			String queryString = "from UserDataModel U where U.userId='"
					+ userHibernateObject.getUserId() + "'";
			List l = UtilityMethodClass.queryDataAndLoginObject(queryString,
					newSession);
			if (!l.isEmpty()) {
				try {
					new UserModelFillerFromRequest<UserDataModel>()
							.userModelFillerForUpdation(
									(UserDataModel) l.get(0),
									userHibernateObject);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			newSession.beginTransaction();
			newSession.merge(userHibernateObject);
			newSession.getTransaction().commit();
			newSession.close();
		}
		// response.setContentType("text/html");
		// response.setHeader("name","Name  :"+iname);
		// response.addHeader("name","age   :"+iage);
		// response.addHeader("name","gender:"+isex);
		// response.addHeader("name","Status:"+istatus);
		//

	}

}

package com.exec.module;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.exec.filler.UserModelFillerFromRequest;
import com.exec.model.LoginDataModel;

/**
 * Servlet implementation class LoginUser
 */
public class LoginUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private SessionFactory sessionFactory;
	private Session newSession;
	PrintWriter out = null;
	String query = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginUser() {

	}

	@Override
	public void init() throws ServletException {
		sessionFactory = new Configuration().configure().buildSessionFactory();
		newSession = sessionFactory.openSession();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		LoginDataModel dataModel = new LoginDataModel();
		try {

			new UserModelFillerFromRequest<LoginDataModel>().userModelFiller(
					request, dataModel);
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
		}
		newSession.beginTransaction();
		newSession.save(dataModel);
		newSession.getTransaction().commit();
		newSession.close();

		
	}

}

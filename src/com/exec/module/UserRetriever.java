package com.exec.module;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.exec.filler.UserModelFillerFromRequest;
import com.exec.model.PublishableUserDataModel;
import com.exec.model.UserDataModel;
import com.exec.util.UtilityMethodClass;
import com.google.gson.Gson;
/**
 * Servlet implementation class UserRetriever
 */
public class UserRetriever extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String SERVLETNAME = "USERRETREIVER";
	private SessionFactory sessionFactory;
	private Session newSession;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserRetriever() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		System.out.println("Init executed");
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
		PrintWriter out = response.getWriter();
		UserDataModel usersData =  new UserDataModel();
	
		try {
			new UserModelFillerFromRequest<UserDataModel>().userModelFiller(request,usersData);

			String queryString = "select U from UserDataModel U where U.userId!="
					+ usersData.getUserId();
			List<UserDataModel> list = UtilityMethodClass.queryDataAndLoginObject(queryString,
					newSession);
			Gson gson = new Gson();
			List<PublishableUserDataModel> pubList  = new ArrayList<PublishableUserDataModel>();
			for(UserDataModel model :list){
			PublishableUserDataModel pubUserModel = new PublishableUserDataModel(model);
			pubList.add(pubUserModel);
			}
			response.addHeader("UsersList", gson.toJson(pubList));
			response.setContentType("text/html");
			System.out.println(pubList);
		}
		catch (Exception ex) {
			out.println("Unable to connect to database." + ex.getMessage());
		}
	
	}	

}

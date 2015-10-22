package com.exec.module;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exec.filler.UserModelFillerFromRequest;
import com.exec.model.UserDataModel;

import java.sql.Statement;

/**
 * Servlet implementation class UserProfileDataRetriever1
 */
public class UserProfileDataRetriever extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private String data[];
	private int profileId;
	private PrintWriter out;
	private String query;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserProfileDataRetriever() {
		super();
		data = new String[4];
	}

	public void init(ServletConfig config) throws ServletException {
		connection = DatabaseConnectionHelper.databaseConnection();
	}

	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			out = response.getWriter();
			UserDataModel userDataWithOnlyId = new UserDataModel();
			new UserModelFillerFromRequest<UserDataModel>().userModelFiller(request,userDataWithOnlyId);
			profileId = Integer.parseInt(request.getParameter("uid"));
			statement = connection.createStatement();
			query = "Select * from ud where uid=" + profileId + "";
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {

				data[0] = resultSet.getString(2);
				data[1] = resultSet.getString(3);
				data[2] = resultSet.getString(4);
				data[3] = resultSet.getString(10);
			}
			// check weather connection is established or not by isClosed()
			// method
			if (!connection.isClosed())
				out.println("Successfully connected to "
						+ "MySQL server using TCP/IP...   " + data[0]);
			connection.close();
		} catch (Exception ex) {
			out.println("Unable to connect to database." + ex.getMessage());
		}

		response.setContentType("text/html");
		response.setHeader("name", "Name   :" + data[0]);
		response.addHeader("name", "age    :" + data[1]);
		response.addHeader("name", "gender :" + data[2]);
		response.addHeader("name", "Status :" + data[3]);

	}

}

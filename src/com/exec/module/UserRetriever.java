package com.exec.module;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.exec.filler.UserModelFillerFromRequest;
import com.exec.model.UserDataModel;
import com.mysql.jdbc.Driver;
/**
 * Servlet implementation class UserRetriever
 */
public class UserRetriever extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection = null;
	Statement statement = null;
	ResultSet resultSet = null;

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
		connection  = DatabaseConnectionHelper.databaseConnection();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		UserDataModel userDataCheck =  new UserDataModel();
	
		int userId = Integer.parseInt(request.getParameter("uid"));
		try {
			new UserModelFillerFromRequest<UserDataModel>().userModelFiller(request,userDataCheck);
			statement = connection.createStatement();

			String query = "select L.userID,ID from ui where ID !=" + userId + "";
			resultSet = statement.executeQuery(query);
			int i = 0;
			while (resultSet.next()) {
				i++;
			}
			String x[][] = new String[i][2];
			int j = 0;
			resultSet.beforeFirst();
			while (resultSet.next()) {
				x[j][0] = resultSet.getString(1);
				x[j][1] = resultSet.getString(2);
				j++;
			}
			// check weather connection is established or not by isClosed()
			// method

			if (!connection.isClosed())
				out.println("Successfully connected to "
						+ "MySQL server using TCP/IP...   ");
			connection.close();
			j = 0;
			response.setContentType("text/html");

			response.setHeader("name", x[0][0]);
			response.setHeader("boo", x[0][1]);
			for (j = 1; j < i; j++) {

				response.addHeader("name", x[j][0]);
				response.addHeader("boo", x[j][1]);
			}
		}

		catch (Exception ex) {
			out.println("Unable to connect to database." + ex.getMessage());
		}
	}

}

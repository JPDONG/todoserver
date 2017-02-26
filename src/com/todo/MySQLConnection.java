package com.todo;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;



public class MySQLConnection extends HttpServlet{
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/todoapp?autoReconnect=true&useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "1234";
	
	public MySQLConnection() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		StringBuilder stringBuilder = new StringBuilder();
		Connection connection= null;
		Statement statement = null;
		//resp.setContentType("text/html");
		PrintWriter printWriter = resp.getWriter();
		/*printWriter.println(
				"<html>\n" +
				"<head><title>JDBC</title></head>\n" +
				"<body bgcolor=\"#f0f0f0\">\n" +
				"<h1 align=\"center\">tasks</h1>\n");*/
		try {
			Class.forName(JDBC_DRIVER);
			connection = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);
			statement = (Statement) connection.createStatement();
			String SQL_SELECT = "select * from tasks";
			ResultSet resultSet = statement.executeQuery(SQL_SELECT);
			while (resultSet.next()) {
				String id = resultSet.getString("id");
				String title = resultSet.getString("title");
				String description = resultSet.getString("description");
				int completed = resultSet.getInt("completed");
				/*printWriter.println("<h3>" + id + "</h3>");
				printWriter.println("<h3>" + title + "</h3>");
				printWriter.println("<h3>" + description + "</h3>");
				printWriter.println("<h3>" + completed + "</h3>");*/
				stringBuilder.append(id + "," + title + "," + description + "," + completed + ";");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", id);
				jsonObject.put("title", title);
				jsonObject.put("description", description);
				jsonObject.put("completed", completed);
				//printWriter.write(jsonObject.toString());
				System.out.println(jsonObject.toString());
			}
			//printWriter.println("</body></html>");
			printWriter.write(stringBuilder.toString());
			printWriter.flush();
			printWriter.close();
			resultSet.close();
			statement.close();
			connection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
	
	
}

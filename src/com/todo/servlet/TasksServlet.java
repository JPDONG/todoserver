package com.todo.servlet;

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

public class TasksServlet extends HttpServlet{
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/todoapp?autoReconnect=true&useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "1234";
	
	private Connection mConnection = null;
	private Statement mStatement = null;
	
	public static enum Operation {
		SAVE,GET,COMPLETE,ACTIVATE,GETTIME,UPDATE
	}
	
	public TasksServlet() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		getConnection();
		if (mStatement == null) {
			return;
		}
		StringBuilder stringBuilder = new StringBuilder();
		//resp.setContentType("text/html");
		PrintWriter printWriter = resp.getWriter();
		/*printWriter.println(
				"<html>\n" +
				"<head><title>JDBC</title></head>\n" +
				"<body bgcolor=\"#f0f0f0\">\n" +
				"<h1 align=\"center\">tasks</h1>\n");*/
		try {
			String SQL_SELECT = "select * from tasks";
			ResultSet resultSet = mStatement.executeQuery(SQL_SELECT);
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
				/*JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", id);
				jsonObject.put("title", title);
				jsonObject.put("description", description);
				jsonObject.put("completed", completed);
				printWriter.write(jsonObject.toString());
				System.out.println(jsonObject.toString());*/
			}
			//printWriter.println("</body></html>");
			printWriter.write(stringBuilder.toString());
			System.out.println("get data");
			printWriter.flush();
			printWriter.close();
			resultSet.close();
			mStatement.close();
			mConnection.close();
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (mStatement != null) {
					mStatement.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
			try {
				if (mConnection != null) {
					mConnection.close();
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		mConnection= null;
		mStatement = null;
		String SQL = null;
		String id = null;
		String description = null;
		String title = null;
		int completed = 0;
		Operation currentOP = null;
		String operation = req.getParameter("operation");
		if ("activate".equals(operation)) {
			currentOP = Operation.ACTIVATE;
		} else if ("complete".equals(operation)) {
			currentOP = Operation.COMPLETE;
		} else if ("save".equals(operation)) {
			currentOP = Operation.SAVE;
		} else if ("gettime".equals(operation)) {
			currentOP = Operation.GETTIME;
		} else if ("update".equals(operation)) {
			currentOP = Operation.UPDATE;
		}
		if (currentOP == null) {
			return;
		}
		switch (currentOP) {
		case SAVE:
			System.err.println("save");
			id = req.getParameter("id");
			description = req.getParameter("description");
			title = req.getParameter("title");
			completed = Integer.valueOf(req.getParameter("completed"));
			SQL = "insert into tasks (id,title,description,completed) values ('" + id + "','" 
					+ title + "','" + description + "','" + completed + "')";
			if (SQL == null) {
				return;
			}
			connectAndUpdate(SQL, resp);
			break;
		case ACTIVATE:
			id = req.getParameter("id");
			SQL = "update tasks set completed='0' where id='" + id + "'";
			break;
		case COMPLETE:
			id = req.getParameter("id");
			SQL = "update tasks set completed='1' where id='" + id + "'";
			break;
		case GETTIME:
			getModifiedTime(resp);
			break;
		case UPDATE:
			System.err.println("update");
			id = req.getParameter("id");
			description = req.getParameter("description");
			title = req.getParameter("title");
			completed = Integer.valueOf(req.getParameter("completed"));
			SQL = "update tasks (title,description,completed) values (" 
					+ title + "','" + description + "','" + completed + "') where id like " + id +";";
			if (SQL == null) {
				return;
			}
			connectAndUpdate(SQL,resp);
			break;
		default:
			break;
		}
		
	}
	
	private void getConnection() {
		try {
			Class.forName(JDBC_DRIVER);
			mConnection = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);
			mStatement = (Statement) mConnection.createStatement();
		} catch (SQLException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void getModifiedTime(HttpServletResponse resp) {
		getConnection();
		if (mStatement == null) {
			return;
		}
		String time = null;
		try {
			String SQL_GET_TIME = "select * from tasks order by time desc limit 1";
			ResultSet resultSet = mStatement.executeQuery(SQL_GET_TIME);
			PrintWriter printWriter = resp.getWriter();
			if (resultSet.next()) {
				time = resultSet.getString("time");
			}
			printWriter.write(time);
			printWriter.flush();
			printWriter.close();
			mStatement.close();
			mConnection.close();
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (mStatement != null) {
						mStatement.close();
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
				try {
					if (mConnection != null) {
						mConnection.close();
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		
	}

	private void connectAndUpdate(String SQL, HttpServletResponse resp) {
		getConnection();
		if (mStatement == null) {
			return;
		}
		try {
			int resultSet = mStatement.executeUpdate(SQL);
			System.out.println("insert data" + resultSet);
			PrintWriter printWriter = resp.getWriter();
			printWriter.write("ok");
			printWriter.close();
			mStatement.close();
			mConnection.close();
			} catch (SQLException se) {
				// TODO Auto-generated catch block
				se.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (mStatement != null) {
						mStatement.close();
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
				try {
					if (mConnection != null) {
						mConnection.close();
					}
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		
	}

	
	
	
}

package com.todo.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.deploy.LoginConfig;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import com.sun.org.apache.bcel.internal.generic.RETURN;

/**
 * Servlet implementation class UsersServlet
 */
@WebServlet("/UsersServlet")
public class UsersServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection mConnection= null;
	private Statement mStatement = null; 
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/todoapp?autoReconnect=true&useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "1234";
	
	private int SQL_UPDATE = 100;
	private int SQL_SELECT = 101;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UsersServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("get");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("post");
		String operation = request.getParameter("operation");
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		if ("".equals(name) || "".equals(password)) {
            return;
        }
        if ("register".equals(operation)) {
            register(response, name, password);
        } else if ("login".equals(operation)){
        	login(response, name, password);
        }

}
	private void login(HttpServletResponse response, String name,
			String password) {
		System.out.println("login");
		String sql = "select userid,password from users where name like '" + name + "'";
		getConnection();
		if (mStatement == null) {
			return;
		}
		try {
				PrintWriter printWriter = response.getWriter();
				ResultSet resultSet = mStatement.executeQuery(sql);
				if (resultSet.next()) {
					String passwordInDB = resultSet.getString("password");
					String userId = resultSet.getString("userid");
					if (password.equals(passwordInDB)) {
						printWriter.write(userId);
					} else {
						printWriter.write("wrong");
					}
				} else {
					printWriter.write("fail");
				}
			
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			}
		}
	}

	private void getConnection() {
		System.out.println("getConnection");
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

	private void register(HttpServletResponse response, String name, String password) {
		System.out.println("register");
		PrintWriter printWriter;
		try {
			printWriter = response.getWriter();
			if (existName(name)){
				printWriter.write("already");
				System.out.println("already");
				printWriter.close();
				return;
			}
			UUID uuid = UUID.randomUUID();
			String userId = uuid.toString();
			if (saveUser(userId, name, password)) {
				printWriter.write("ok,"+ userId);
				System.out.println("ok");
				printWriter.close();
				return;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private boolean existName(String name) {
		String sql = "select * from users where name like '" + name + "'"; 
		return sendSQL(sql, SQL_SELECT);
	}

	private boolean saveUser(String userId, String name, String password) {
		String SQL = "insert into users (userid,name,password) values ('" + userId +"','"+ name + "','" + password + "')";
		return sendSQL(SQL, SQL_UPDATE);
	}

	private boolean sendSQL(String sql, int statue) {
		getConnection();
		if (mStatement == null) {
			return false;
		}
		try {
			if (statue == SQL_SELECT) {
				ResultSet resultSet = mStatement.executeQuery(sql);
				if (resultSet.next()) {
					return true;
				} else {
					return false;
				}
			} else if (statue == SQL_UPDATE) {
				mStatement.executeUpdate(sql); 
			} else {
				return false;
			}
			return true;
		} catch (SQLException e) {
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
			}
		}
		return false;
	}
	
}

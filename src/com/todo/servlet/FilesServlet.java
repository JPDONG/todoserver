package com.todo.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

/**
 * Servlet implementation class FilesServlet
 */
@WebServlet("/FilesServlet")
public class FilesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Connection mConnection = null;
	private Statement mStatement = null;
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost:3306/todoapp?autoReconnect=true&useSSL=false";
	private static final String USER = "root";
	private static final String PASS = "1234";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FilesServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("post");
		/*
		 * String userId = request.getParameter("userid"); BufferedInputStream
		 * fileIn = new BufferedInputStream( request.getInputStream()); String
		 * fileName = "icon_" + System.currentTimeMillis() + ".jpg"; byte[] buf
		 * = new byte[1024]; String savePath =
		 * this.getServletContext().getRealPath( "/WEB-INF/uploads"); File file
		 * = new File(savePath); if (!file.exists()) {
		 * System.out.println(savePath + "目录不存在，需要创建"); // 创建目录 file.mkdir(); }
		 * BufferedOutputStream fileOut = new BufferedOutputStream( new
		 * FileOutputStream(new File(savePath + "\\" + fileName))); while (true)
		 * { int bytesIn = fileIn.read(buf, 0, 1024);
		 * System.out.println(bytesIn); if (bytesIn == -1) { break; } else {
		 * fileOut.write(buf, 0, bytesIn); } } fileOut.flush(); fileOut.close();
		 * PrintWriter printWriter = response.getWriter();
		 * printWriter.print(file.getAbsolutePath());
		 * 
		 * getConnection(); if (mStatement == null) { return; } String
		 * saveImgSQL = "update users set icon = '" + fileName +
		 * "' where userid like '" + userId + "' limit 1"; try {
		 * mStatement.executeUpdate(saveImgSQL); } catch (SQLException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } finally { try
		 * { if (mStatement != null) { mStatement.close(); } } catch (Exception
		 * e2) { // TODO: handle exception } try { if (mConnection != null) {
		 * mConnection.close(); } } catch (Exception e2) { // TODO: handle
		 * exception } }
		 */

		// 检测是否为多媒体上传
		if (!ServletFileUpload.isMultipartContent(request)) {
			// 如果不是则停止
			PrintWriter writer = response.getWriter();
			writer.println("Error: 表单必须包含 enctype=multipart/form-data");
			writer.flush();
			return;
		}
		String userId = null;
		System.out.println(userId);
		String fileName = null;
		request.setCharacterEncoding("utf-8");
		// 获得磁盘文件条目工厂。
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 获取文件上传需要保存的路径，upload文件夹需存在。
		// String path =
		// request.getSession().getServletContext().getRealPath("/upload");
		// 设置暂时存放文件的存储室，这个存储室可以和最终存储文件的文件夹不同。因为当文件很大的话会占用过多内存所以设置存储室。

		String savePath = this.getServletContext().getRealPath("/uploads");
		File file = new File(savePath);
		if (!file.exists()) {
			System.out.println(savePath + "目录不存在，需要创建");
			// 创建目录
			file.mkdir();
		}

		factory.setRepository(new File(savePath));
		// 设置缓存的大小，当上传文件的容量超过缓存时，就放到暂时存储室。
		factory.setSizeThreshold(1024 * 1024);
		// 上传处理工具类（高水平API上传处理？）
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			// 调用 parseRequest（request）方法 获得上传文件 FileItem 的集合list 可实现多文件上传。
			List<FileItem> list = (List<FileItem>) upload.parseRequest(request);
			for (FileItem item : list) {
				// 获取表单属性名字。
				String name = item.getFieldName();
				// 如果获取的表单信息是普通的文本信息。即通过页面表单形式传递来的字符串。
				if (item.isFormField()) {
					// 获取用户具体输入的字符串，
					userId = item.getString();
					System.out.println(userId);
				}
				// 如果传入的是非简单字符串，而是图片，音频，视频等二进制文件。
				else {
					// 获取路径名
					String value = item.getName();
					// 取到最后一个反斜杠。
					int start = value.lastIndexOf("\\");
					// 截取上传文件的 字符串名字。+1是去掉反斜杠。
					fileName = value.substring(start + 1);
					request.setAttribute(name, fileName);

					/*
					 * 第三方提供的方法直接写到文件中。 item.write(new File(path,filename));
					 */
					// 收到写到接收的文件中。
					OutputStream out = new FileOutputStream(new File(savePath,
							fileName));
					InputStream in = item.getInputStream();

					int length = 0;
					byte[] buf = new byte[1024];
					System.out.println("获取文件总量的容量:" + item.getSize());

					while ((length = in.read(buf)) != -1) {
						out.write(buf, 0, length);
					}
					in.close();
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		getConnection();
		if (mStatement == null) {
			return;
		}
		String saveImgSQL = "update users set icon = '" + fileName + "' where userid like '" + userId + "' limit 1";
		try {
			mStatement.executeUpdate(saveImgSQL);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (mStatement != null) {
					mStatement.close();
				}
			} catch (Exception e2) {
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
		try {
			Class.forName(JDBC_DRIVER);
			mConnection = (Connection) DriverManager.getConnection(DB_URL,
					USER, PASS);
			mStatement = (Statement) mConnection.createStatement();
		} catch (SQLException se) {
			// TODO Auto-generated catch block
			se.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

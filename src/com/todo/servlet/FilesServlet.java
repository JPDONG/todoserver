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
		 * System.out.println(savePath + "Ŀ¼�����ڣ���Ҫ����"); // ����Ŀ¼ file.mkdir(); }
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

		// ����Ƿ�Ϊ��ý���ϴ�
		if (!ServletFileUpload.isMultipartContent(request)) {
			// ���������ֹͣ
			PrintWriter writer = response.getWriter();
			writer.println("Error: ��������� enctype=multipart/form-data");
			writer.flush();
			return;
		}
		String userId = null;
		System.out.println(userId);
		String fileName = null;
		request.setCharacterEncoding("utf-8");
		// ��ô����ļ���Ŀ������
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// ��ȡ�ļ��ϴ���Ҫ�����·����upload�ļ�������ڡ�
		// String path =
		// request.getSession().getServletContext().getRealPath("/upload");
		// ������ʱ����ļ��Ĵ洢�ң�����洢�ҿ��Ժ����մ洢�ļ����ļ��в�ͬ����Ϊ���ļ��ܴ�Ļ���ռ�ù����ڴ��������ô洢�ҡ�

		String savePath = this.getServletContext().getRealPath("/uploads");
		File file = new File(savePath);
		if (!file.exists()) {
			System.out.println(savePath + "Ŀ¼�����ڣ���Ҫ����");
			// ����Ŀ¼
			file.mkdir();
		}

		factory.setRepository(new File(savePath));
		// ���û���Ĵ�С�����ϴ��ļ���������������ʱ���ͷŵ���ʱ�洢�ҡ�
		factory.setSizeThreshold(1024 * 1024);
		// �ϴ��������ࣨ��ˮƽAPI�ϴ�������
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			// ���� parseRequest��request������ ����ϴ��ļ� FileItem �ļ���list ��ʵ�ֶ��ļ��ϴ���
			List<FileItem> list = (List<FileItem>) upload.parseRequest(request);
			for (FileItem item : list) {
				// ��ȡ���������֡�
				String name = item.getFieldName();
				// �����ȡ�ı���Ϣ����ͨ���ı���Ϣ����ͨ��ҳ�����ʽ���������ַ�����
				if (item.isFormField()) {
					// ��ȡ�û�����������ַ�����
					userId = item.getString();
					System.out.println(userId);
				}
				// ���������ǷǼ��ַ���������ͼƬ����Ƶ����Ƶ�ȶ������ļ���
				else {
					// ��ȡ·����
					String value = item.getName();
					// ȡ�����һ����б�ܡ�
					int start = value.lastIndexOf("\\");
					// ��ȡ�ϴ��ļ��� �ַ������֡�+1��ȥ����б�ܡ�
					fileName = value.substring(start + 1);
					request.setAttribute(name, fileName);

					/*
					 * �������ṩ�ķ���ֱ��д���ļ��С� item.write(new File(path,filename));
					 */
					// �յ�д�����յ��ļ��С�
					OutputStream out = new FileOutputStream(new File(savePath,
							fileName));
					InputStream in = item.getInputStream();

					int length = 0;
					byte[] buf = new byte[1024];
					System.out.println("��ȡ�ļ�����������:" + item.getSize());

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

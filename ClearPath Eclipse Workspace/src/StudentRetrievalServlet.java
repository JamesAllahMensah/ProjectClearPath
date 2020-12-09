import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@WebServlet("/StudentRetrievalServlet")
public class StudentRetrievalServlet extends HttpServlet {

	private static final String USER = "useraccount";
	private static final String PASSWORD = "password";


	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		response.setContentType("text/html");
		
		PrintWriter out = response.getWriter();
		String name = request.getParameter("name");
		String emailConcat = "";
		
		try {
			String students = retrieveStudents(name);
			Scanner scnr = new Scanner(students);
			scnr.useDelimiter(";");
			
			while(scnr.hasNext()) {
				String studentName = scnr.next();
				String email = retrieveEmail(studentName);
				emailConcat += email + ";";
			}
			emailConcat = emailConcat.substring(0, emailConcat.length() - 1);
			
			out.print(students);
			out.print("\n");
			out.print(emailConcat);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	

	public static String retrieveStudents(String name) throws ClassNotFoundException {
		String studentConcat;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT Students FROM Advisor WHERE Name = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, name);
			ResultSet rs = statement.executeQuery();

			boolean res = rs.next();
			
			studentConcat = rs.getString("Students");
			
			connection.close();
			return studentConcat;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	public static String retrieveEmail(String name) throws ClassNotFoundException {
		System.out.println(name);
		String email;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT Email FROM Student WHERE Name = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, name);
			ResultSet rs = statement.executeQuery();

			boolean res = rs.next();
			
			email = rs.getString("Email");
			System.out.println(email);
			
			rs.close();
			connection.close();
			return email;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws ClassNotFoundException {
		String name = "Lynn Lambert";
		String studentsList = retrieveStudents(name);
		String emailConcat = "";
		
		Scanner scnr = new Scanner(studentsList);
		scnr.useDelimiter(";");
		
		while(scnr.hasNext()) {
			String studentName = scnr.next();
			String email = retrieveEmail(studentName);
			emailConcat += email + ";";
		}
		
		emailConcat = emailConcat.substring(0, emailConcat.length() - 1);
		System.out.println(emailConcat);

	}

}

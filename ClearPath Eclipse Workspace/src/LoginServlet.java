import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

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
		Student student = null;
		
		
		String name = request.getParameter("username");
		String email = request.getParameter("email");
		boolean accountAdded = false;
		String studentData = null;
		String studentPlan = null;
		
		try {
			if (isAdvisor(email)) {
				out.println("Advisor");
			}
			
			else if (!hasAccount(email)) {
				accountAdded = addAccount(name, email);
				out.print("Account Added!");

			}
			else {
				student = retrieveAccount(email);
				studentData = student.toString();
				studentPlan = student.getPlanNames();
				
				out.println(studentData + "\t" + studentPlan);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	public static boolean addAccount(String name, String email) throws ClassNotFoundException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			
			String SQL = "INSERT INTO Student VALUES (?,?,\'',\'',null,\'',\'',\'',null)";
			PreparedStatement ps = connection.prepareStatement(SQL);
			ps.setString(1, name);
			ps.setString(2, email);
			
			int insertedRows = ps.executeUpdate();
			if (insertedRows == 0) {
				return false;
			}
			else {
				return true;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean isAdvisor(String email) throws ClassNotFoundException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT * FROM Advisor WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();
//			connection.close();

			if (!rs.next()){
				return false;
			}
			else {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean hasAccount(String email) throws ClassNotFoundException {
		ArrayList<Student> students = new ArrayList<Student>();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT * FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			String name, emailAdd, major, minor, fullPlan, advisor, planNames, planMajor;
			boolean isGrad;

			while (rs.next()) {
				name = rs.getString(1);
				emailAdd = rs.getString(2);
				major = rs.getString(3);
				minor = rs.getString(4);
				isGrad = rs.getBoolean(5);
				fullPlan = rs.getString(6);
				advisor = rs.getString(7);
				planNames = rs.getString(8);
				planMajor = rs.getString(9);
				Student student = new Student(name, emailAdd, major, minor, isGrad, fullPlan, advisor, planNames, planMajor);
				students.add(student);
			}

			connection.close();
			if (students.size() == 0) {
				return false;
			} else {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	

	public static Student retrieveAccount(String email) throws ClassNotFoundException {
		ArrayList<Student> students = new ArrayList<Student>();

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT * FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			String name, emailAdd, major, minor, fullPlan, advisor, planNames, planMajor;
			boolean isGrad;

			while (rs.next()) {
				name = rs.getString(1);
				emailAdd = rs.getString(2);
				major = rs.getString(3);
				minor = rs.getString(4);
				isGrad = rs.getBoolean(5);
				fullPlan = rs.getString(6);
				advisor = rs.getString(7);
				planNames = rs.getString(8);
				planMajor = rs.getString(9);
				Student student = new Student(name, emailAdd, major, minor, isGrad, fullPlan, advisor, planNames, planMajor);
				students.add(student);
			}

			connection.close();
			if (students.size() == 0) {
				return null;
			} else {
				Student student = students.get(0);
				return student;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

//	public static void main(String[] args) throws ClassNotFoundException {
//		String email = "Lynn.Lambert@cnu.edu";
//		boolean b = isAdvisor(email);
//		System.out.println(b);
//
//	}

}

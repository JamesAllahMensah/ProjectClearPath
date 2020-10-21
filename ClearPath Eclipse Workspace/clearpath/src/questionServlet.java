import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;


@WebServlet("/questionServlet")
public class questionServlet extends HttpServlet {

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
		
		String email = request.getParameter("email-hidden");
		String major = request.getParameter("majors");
		String doubleMajor = request.getParameter("double-majors");	// can be null
		String minor = request.getParameter("minors");				// can be null
		String doubleMinor = request.getParameter("double-minors");	// can be null
		String gradSchool = request.getParameter("selector-gs");	
		String advisor = request.getParameter("advisor");
		
		
		try {
			updateProfile(email, major, doubleMajor, minor, doubleMinor, gradSchool, advisor);
			out.println("User Account Updated");
		} catch (ClassNotFoundException e) {
			out.println("Failed");
			e.printStackTrace();
		}
		response.sendRedirect("table.html");
	}
	
	public static void updateProfile(String email, String major, String doubleMajor, String minor, String doubleMinor, String gradSchool, String advisor) throws ClassNotFoundException {
		ArrayList<Student> students = new ArrayList<Student>();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath;allowMultipleQueries=true";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);

			if (!doubleMajor.equals("Select Major")) {
				major = major + ";" + doubleMajor;
			}
			
			if (minor.equals("Select Minor")) {
				minor = null;
			}
			
			
			if (!doubleMinor.equals("Select Minor")) {
				minor = minor + ";" + doubleMinor;
			}
			
			boolean isGrad = false;
			if (gradSchool.equals("yes")) {
				isGrad = true;
			}
			else {
				isGrad = false;
			}
			
			String SQL = "UPDATE Student SET Major = ?, Minor = ?, isGrad = ?, Advisor = ? WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, major);
			statement.setString(2, email);
			statement.setString(2, minor);
			statement.setBoolean(3, isGrad);
			statement.setString(4, advisor);
			statement.setString(5, email);
			statement.execute();
			connection.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) throws ClassNotFoundException {
//		String email = "jimmy.allahmensah.17@cnu.edu";
//		String major = "Computer Science";
//		String doubleMajor = "Cyber Security";
//		String minor = null;
//		String doubleMinor = null;
//		String gradSchool = "yes";	
//		String advisor = "Zhang";
//		
//		updateProfile(email, major, doubleMajor, minor, doubleMinor, gradSchool, advisor);
	}

}

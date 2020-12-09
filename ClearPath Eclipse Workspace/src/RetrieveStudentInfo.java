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

@WebServlet("/RetrieveStudentInfo")
public class RetrieveStudentInfo extends HttpServlet {

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
		String email = request.getParameter("email");
		String fullPlans, planNames;
		
		try {
			planNames = retrievePlanNames(email);
			fullPlans = retrieveFullPlans(email);
			out.print(planNames);
			out.print("\t");
			out.print(fullPlans);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	

	public static String retrieveFullPlans(String email) throws ClassNotFoundException {
		String fullPlan;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT FullPlan FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			boolean res = rs.next();
			
			fullPlan = rs.getString("FullPlan");
			
			connection.close();
			return fullPlan;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String retrievePlanNames(String email) throws ClassNotFoundException {
		String planName;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT PlanName FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			boolean res = rs.next();
			
			planName = rs.getString("PlanName");
			
			connection.close();
			return planName;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	

	public static void main(String[] args) throws ClassNotFoundException {
		String email = "Jimmy.allahmensah.17@cnu.edu";
		String planName = retrievePlanNames(email);
		String plans = retrieveFullPlans(email);
		System.out.println(planName);
		System.out.println(plans);

	}

}

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

@WebServlet("/planServlet")
public class planServlet extends HttpServlet {

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
		String action = request.getParameter("action");
		String email = request.getParameter("email");
		String planName = request.getParameter("planName");
		String copyName = request.getParameter("copyName");
		
		ArrayList<String> names = null;
		ArrayList<String> plans = null;
		ArrayList<String> majors = null;
		
		try {
			if (action.trim().equals("Get Major")) {
				String major = retrieveMajorFromPlan(planName, email);
				out.println(major);
				return;
			}
			String currentPlanNames = retrievePlanNames(email);
			String currentPlans = retrieveFullPlans(email);
			String currentMajors = retrievePlanMajor(email);
			
			String[] planNameArr = currentPlanNames.split(";");
			String [] planArr = currentPlans.split(";");
			String [] majorArr = currentMajors.split(";");
			
			names = arrayToArrayList(planNameArr);
			plans = arrayToArrayList(planArr);
			majors = arrayToArrayList(majorArr);
			
			out.println(planName);
			out.println(names);
			
			if (action.equals("Delete")) {
				int deleteIndex = names.indexOf(planName);
				out.println(deleteIndex);
				names.remove(deleteIndex);
				plans.remove(deleteIndex);
				majors.remove(deleteIndex);
				
				String updatedNames = "";
				String updatedPlans = "";
				String updatedMajors = "";
				
				for (int i = 0; i < names.size(); i++) {
					String currentPlanName = names.get(i) + ";";
					updatedNames = updatedNames + currentPlanName;
					String currentPlan = plans.get(i) + ";";
					updatedPlans = updatedPlans + currentPlan;
					String currentMajor = majors.get(i) + ";";
					updatedMajors = updatedMajors + currentMajor;
				}
				
				boolean updated = updatePlan(email, updatedPlans, updatedNames, updatedMajors);
				out.println(updated);
				
			}
			else {
				int copyIndex = names.indexOf(planName);
//				String newName = "COPY OF " + names.get(copyIndex);
				String newName = copyName;
				names.add(newName);
				plans.add(plans.get(copyIndex));
				majors.add(majors.get(copyIndex));
				
				String updatedNames = "";
				String updatedPlans = "";
				String updatedMajors = "";
				
				for (int i = 0; i < names.size(); i++) {
					String currentPlanName = names.get(i) + ";";
					updatedNames = updatedNames + currentPlanName;
					String currentPlan = plans.get(i) + ";";
					updatedPlans = updatedPlans + currentPlan;
					String currentMajor = majors.get(i) + ";";
					updatedMajors = updatedMajors + currentMajor;
				}
				
				boolean updated = updatePlan(email, updatedPlans, updatedNames, updatedMajors);
				out.println(updated);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> arrayToArrayList(String [] array){
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			String value = array[i];
			list.add(value);
		}
		return list;
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
	
	public static String retrieveMajorFromPlan(String planName, String email) throws ClassNotFoundException {
		String planMajor;
		String nameOfPlan;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT * FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			boolean res = rs.next();
			
			planMajor = rs.getString("PlanMajor");
			nameOfPlan = rs.getString("PlanName");
			
			connection.close();
			
			String [] planNames = nameOfPlan.split(";");
			String [] majors = planMajor.split(";");
			
			ArrayList<String> splitMajors = new ArrayList<String>();
			for (int i = 0; i < planNames.length; i++) {
				splitMajors.add(planNames[i]);
			}
			
			int index = splitMajors.indexOf(planName);
			return majors[index];
			
			

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
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
	
	public static String retrievePlanMajor(String email) throws ClassNotFoundException {
		String planMajor;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT PlanMajor FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			boolean res = rs.next();
			
			planMajor = rs.getString("PlanMajor");
			
			connection.close();
			return planMajor;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static boolean updatePlan(String email, String addedFullPlan, String addedPlanName, String addedMajor) throws ClassNotFoundException {
		String fullPlan;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "UPDATE Student SET FullPlan = ?, PlanName = ?, PlanMajor = ?  WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, addedFullPlan);
			statement.setString(2, addedPlanName);
			statement.setString(3, addedMajor);
			statement.setString(4, email);

			int insertedRows = statement.executeUpdate();
			
			connection.close();
			return true;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}	

	public static void main(String[] args) throws ClassNotFoundException {
		String email = "Jimmy.allahmensah.17@cnu.edu";
		String currentPlanNames = retrievePlanNames(email);
		String currentPlans = retrieveFullPlans(email);
		String currentMajors = retrievePlanMajor(email);
		String planName = "COPY OF 941XYessir";

		String[] planNameArr = currentPlanNames.split(";");
		String [] planArr = currentPlans.split(";");
		String [] majorArr = currentMajors.split(";");
		
		ArrayList<String> names = arrayToArrayList(planNameArr);
		ArrayList<String> plans = arrayToArrayList(planArr);
		ArrayList<String> majors = arrayToArrayList(majorArr);
		
		int deleteIndex = names.indexOf(planName);
		System.out.println(names);
		names.remove(deleteIndex);
		System.out.println(names);
//		plans.remove(deleteIndex);
//		majors.remove(deleteIndex);
		
		System.out.println(deleteIndex);
	}

}

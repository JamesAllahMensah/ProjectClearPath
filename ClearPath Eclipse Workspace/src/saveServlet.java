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

@WebServlet("/saveServlet")
public class saveServlet extends HttpServlet {

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
		String planName = request.getParameter("planName");
		String email = request.getParameter("email");
		String newPlan = request.getParameter("fullPlan");
		String isNewPlan = request.getParameter("newPlan");
		String major = request.getParameter("newMajor");
		
		try {
			String currentPlans = retrieveFullPlans(email);	// Get Full Plans
			String currentPlanNames = retrievePlanNames(email);
			String currentPlanMajor = retrievePlanMajor(email);
			boolean isSaved = false;
			if (isNewPlan.equals("True")) {		// If New Plan
				if (!currentPlans.contains(";")) {	// If existing plan doesn't contain a semi colon, it is a NEW PLAN
					isSaved = saveNewPlan(email, newPlan + ";", planName + ";", major + ";");	// Save new plan with semi colon 
					out.println(isSaved);	
				}
				else {				// If additional plan
					currentPlans += newPlan + ";";	// Add semi colon and append to current plan
					currentPlanNames += planName + ";";	// Add the plan and planNames to current
					currentPlanMajor += major + ";";
					
					boolean checkExisting = checkPlanName(planName, email);
					if (!checkExisting) {
						out.println("Already Exists");
						return;
					}
					
					isSaved = saveNewPlan(email, currentPlans, currentPlanNames, currentPlanMajor);	// Save the plan
					out.println(isSaved);
				}
			}
			else {	// if Existing plan
				ArrayList<String> plans = new ArrayList<String>();
				ArrayList<String> names = new ArrayList<String>();
				
				Scanner nameScanner = new Scanner(currentPlanNames);
				nameScanner.useDelimiter(";");
				while (nameScanner.hasNext()) {
					String nameInst = nameScanner.next();
					names.add(nameInst);
				}
				// Adds all names into name arraylist
				
//				out.println(planName);
				int addIndex = names.indexOf(planName);
//				out.println("Should be 0: " + addIndex);
				// Gets the index of name
				
				Scanner planScanner = new Scanner(currentPlans);
				planScanner.useDelimiter(";");
				while (planScanner.hasNext()) {
					String planInst = planScanner.next();
					plans.add(planInst);
				}
				// Adds all plans into arraylist
				
				
				plans.add(addIndex, newPlan);
				plans.remove(addIndex + 1);
				// Replace existing plan with new plan

				
				String concatPlans = "";
				for (int i = 0; i < names.size(); i++) {
					String currentPlan = plans.get(i) + ";";
					concatPlans = concatPlans + currentPlan;
				}
				
//				out.println(concatPlans);
				
				isSaved = saveExistingPlan(email,concatPlans);
				out.println(isSaved);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			out.println(e.getLocalizedMessage());
		}
		

	}
	
	public static boolean checkPlanName(String newPlanName, String email) {
		try {
			String existingPlanNames = retrievePlanNames(email);
			String [] arrPlans = existingPlanNames.split(";");
			for (int i = 0; i < arrPlans.length; i++) {
				String plan = arrPlans[i];
				if (plan.trim().equals(newPlanName.trim())) {
					return false;
				}
			}
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
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
	
	public static boolean saveNewPlan(String email, String addedFullPlan, String addedPlanName, String addedPlanMajor) throws ClassNotFoundException {
		String fullPlan;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "UPDATE Student SET FullPlan = ?, PlanName = ?, PlanMajor = ? WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, addedFullPlan);
			statement.setString(2, addedPlanName);
			statement.setString(3, addedPlanMajor);
			statement.setString(4, email);

			int insertedRows = statement.executeUpdate();
			
			connection.close();
			return true;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean saveExistingPlan(String email, String addedFullPlan) throws ClassNotFoundException {
		String fullPlan;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "UPDATE Student SET FullPlan = ? WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, addedFullPlan);
			statement.setString(2, email);

			int insertedRows = statement.executeUpdate();
			
			connection.close();
			return true;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}	

	public static void main(String[] args) throws ClassNotFoundException {
//		String email = "Jimmy.allahmensah.17@cnu.edu";
//		String currentPlans = ",,,,,,";
//		String currentPlanNames = "Plan 1;Plan 2";
//		String currentPlanMajor = "Computer Science;Computer Science";
//		String planName = "New Plan";
//		String major = "Computer Science";
//		String newPlan = ",,,,,,,,,,,,,,,,,,";
//		
//		ArrayList<String> plans = new ArrayList<String>();
//		ArrayList<String> names = new ArrayList<String>();
//		ArrayList<String> majors = new ArrayList<String>();
//		
//		Scanner nameScanner = new Scanner(currentPlanNames);
//		nameScanner.useDelimiter(";");
//		while (nameScanner.hasNext()) {
//			String nameInst = nameScanner.next();
//			names.add(nameInst);
//		}
//		
////		int addIndex = names.indexOf(planName);
//		int addIndex = 0;
//		
//		Scanner planScanner = new Scanner(currentPlans);
//		planScanner.useDelimiter(";");
//		while (planScanner.hasNext()) {
//			String planInst = planScanner.next();
//			plans.add(planInst);
//		}
//		
//		Scanner majorScanner = new Scanner(currentPlanMajor);
//		majorScanner.useDelimiter(";");
//		while (majorScanner.hasNext()) {
//			String majorInst = majorScanner.next();
//			majors.add(majorInst);
//		}
//		
//		System.out.println(names);
//		names.add(addIndex, planName);
//		names.remove(addIndex + 1);
//		System.out.println(names);
//		
//		plans.add(addIndex, newPlan);
//		plans.remove(addIndex + 1);
//		
//		majors.add(addIndex, major);
//		majors.remove(addIndex + 1);
//		
//		String concatNames = "";
//		String concatPlans = "";
//		String concatMajors = "";
//		for (int i = 0; i < names.size(); i++) {
//			String currentName = names.get(i);
//			String currentPlan = plans.get(i);
//			String currentMajor = majors.get(i);
//			concatNames += currentName;
//			concatPlans += currentPlan;
//			concatMajors += currentMajor;
//		}
	
//		System.out.println(currentPlans);
//		System.out.println(currentPlanNames);
//		System.out.println(currentPlanMajor);
	}

}

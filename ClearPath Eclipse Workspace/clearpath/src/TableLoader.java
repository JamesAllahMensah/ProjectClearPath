import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/TableLoader")
public class TableLoader extends HttpServlet{
	
	private static final String USER = "useraccount";
	private static final String PASSWORD = "password";
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doPost(request, response);
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		ArrayList<String> majors = new ArrayList<String>();
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String email = request.getParameter("email");
		String planName = request.getParameter("planName");
		String option = request.getParameter("option");
		String savedPlan = null;
		String major = null;
		String majorStr = null;
		String minor = null;
		String minorStr = null;
		
		if (option != null && option.equals("Get Major")) {
			try {
				major = getMajor(email);
				out.print(major);
				return;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				out.print(e.getLocalizedMessage());
			}
			
		}
		
		// TEST AND FIX
		// SHOULD PRINT OUT THE PLAN DATA, THEN THE MAJOR REQUIREMENTS, THEN MINOR REQUIREMENTS
		
		
		if (planName == null) {				// If new User
			try {
				major = getMajor(email);	// Get Major
				if (major.contains(";")) {	
					String[] majorArr = major.split(";");
					for (String s: majorArr) {
						majorStr = getMajorString(s);
						out.print(majorStr);
						out.print(";");
					}
					out.println("");
					for (String s: majorArr) {
						majorStr = getMajorRequirements(s);
						out.print(majorStr);
						out.print(";");		// If Multiple Majors, gets both plans and separates by semicolon
					}
				}
				else {
					majorStr = getMajor(email);
					majorStr = getMajorString(majorStr);
					out.println(majorStr);
					majorStr = getMajorRequirements(getMajor(email));
					out.println("");
					out.println(majorStr);	// If One Major, get plan
				}
				
				minor = getMinor(email);		// Gets Minor
				
				if (minor != null) {			// If has a minor, prints out string requirements
					out.println("");
					minorStr = getMinorRequirements(minor);
					out.println(minorStr);
				}
				
			} catch (ClassNotFoundException e) {
				out.print(e.getLocalizedMessage());
			}
		}
		else {
			try {
				savedPlan = getSavedPlan(email, planName); // Get saved plan
				out.println(savedPlan);
				out.println("");
				
				major = getMajor(email);
				if (major.contains(";")) {
					String[] majorArr = major.split(";");
					for (String s: majorArr) {
						majorStr = getMajorRequirements(s);
						out.print(majorStr);
						out.print(";");
					}
				}
				else {
					majorStr = getMajor(email);
					majorStr = getMajorRequirements(majorStr);
					out.println(majorStr);
				}
				
				minor = getMinor(email);
				
				if (minor != null) {
					out.println("");
					minorStr = getMinorRequirements(minor);
					out.println(minorStr);
				}
				
			} catch (ClassNotFoundException e) {
				out.println(e.getLocalizedMessage());
			}
		}
		
	}
	
	public static String getSavedPlan(String email, String planName) throws ClassNotFoundException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT FullPlan, PlanName FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			String fullPlan = null;
			String planNames = null;

			while (rs.next()) {
				fullPlan = rs.getString(1);
				planNames = rs.getString(2);
			}
			
			if (planNames.contains(";")) {
				String [] multPlanNames = planNames.split(";");
				String [] multPlans = fullPlan.split(";");
				int index = 0;
				for (int i = 0; i < multPlanNames.length; i++) {
					if (planName.equals(multPlanNames[i].trim())) {
						index = i;
						break;
					}
				}
				connection.close();
				return multPlans[index];
			}
			else {
				connection.close();
				return fullPlan;
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getMajor(String email) throws ClassNotFoundException {
		ArrayList<Student> students = new ArrayList<Student>();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT * FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			String name, emailAdd, major, minor, fullPlan, advisor, planNames;
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
				Student student = new Student(name, emailAdd, major, minor, isGrad, fullPlan, advisor, planNames);
				students.add(student);
			}
			
			connection.close();
			return students.get(0).getMajor();

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getMinor(String email) throws ClassNotFoundException {
		ArrayList<Student> students = new ArrayList<Student>();
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT * FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			String name, emailAdd, major, minor, fullPlan, advisor, planNames;
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
				Student student = new Student(name, emailAdd, major, minor, isGrad, fullPlan, advisor, planNames);
				students.add(student);
			}
			
			connection.close();
			return students.get(0).getMinor();

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getMajorString(String major) {
		String str = null;
		switch (major) {
		case ("Computer Science"):
			str = "CPSC150,CPSC150L,MATH140,AOI/ECON/FL,ENGL123\n"
			+ "CPSC250,CPSC250L,MATH240,AOI/ECON/FL,AOI/ECON/FL,AOI/ECON/FL\n"
			+ "CPSC255,PHYS151/201,PHYS151L/201L,MATH235/260/ENGR340/PHYS340,ENGL223,AOI/ECON/FL\n"
			+ "CPSC270,CPSC280,PHYS152/PHYS202,PHYS152L/202L,CPEN214,ENGR213\n"
			+ "CPSC327,CPSC360,PHYS341,PRO ELECTIVE,AOI/ECON/FL\n"
			+ "CPSC410,CPSC420,CPEN371W,AINW,AINW-Lab,PRO ELECTIVE\n"
			+ "PRO ELECTIVE,AINW,ELECTIVE,WI2,ELECTIVE\n"
			+ "CPSC498,ELECTIVE,ELECTIVE,ELECTIVE";
			return str;
			
		case ("Computer Engineering"):
			str = "ENGR121,MATH140,CPSC150,CPSC150L,FL\n"
			+ "ENGL123,MATH240,CPSC250,CPSC250L,AOI,AOI\n"
			+ "PHYS201,PHYS201L,ENGR210,CPSC255,ENGL223,CPEN214\n"
			+ "PHYS202,PHYS202L,CPEN315,CPEN315L,ENGR213,CPSC270\n"
			+ "ENGR211,ENGR211L,CPSC327,PHYS341,CPEN371W,MATH320\n"
			+ "ENGR212,ENGR212L,CPEN414,CPSC420,ECON201/202,PRO ELECTIVE\n"
			+ "CPEN431,CPSC410,CPEN498W,CHEM121,CHEM121L,PRO ELECTIVE\n"
			+ "CPEN498W,CHEM122,AOI,AOI,ELECTIVE";
			return str;
		
		case ("Cyber Security"):
			str = "CPSC150,CPSC150L,ENGL123,MATH125,AOI/ECON/FL,AOI/ECON/FL\n"
			+ "CPSC250,CPSC250L,MATH140,AOI/ECON/FL,AOI/ECON/FL\n"
			+ "CPSC255,MATH235,PHYS201,PHYS201L,ENGL223\n"
			+ "CPSC270,ENGR213,PHYS202,PHYS202L,AOI/ECON/FL,AOI/FL\n"
			+ "CPSC327,CYBR328,CPEN371W,AINW,MAJOR ELECTIVE\n"
			+ "CPSC420,CPSC428,CPSC335,AINW,AINW-LabL,MAJOR ELECTIVE\n"
			+ "CPSC336,CPSC429,CPSC440,ELECTIVE,ELECTIVE\n"
			+ "CYBR498W,CYBR448,ELECTIVE,ELECTIVE,ELECTIVE";
			return str;
			
		case ("Electrical Engineering"):
			str = "MATH140,ENGL123,PHYS201,PHYS201L,FL\n"
			+ "MATH240,ENGR121,PHYS202,PHYS202L,CPSC150,CPSC150L\n"
			+ "MATH320,ENGR211,ENGR211L,ENGR210,CPEN214,CPSC250,CPSC250L\n"
			+ "MATH250/ENGR213,ENGR212,ENGR212L,ENGR311,ENGR311L,EENG221,ENGL223\n"
			+ "EENG321,EENG321L,EENG361,EENG361L,PHYS341,CPSC256,CPEN371W\n"
			+ "PRO ELECTIVE,PRO ELECTIVE,ECON201/202,AOI,AOI\n"
			+ "EENG498W,PRO ELECTIVE,CHEM121,CHEM121L,AOI,MATH335\n"
			+ "EENG498W,PRO ELECTIVE,CHEM122,AOI,ELECTIVE";
			return str;
			
		case ("Information Science"):
			str = "CPSC150,CPSC150L,ENGL123,AOI/FL,AOI/FL,AOI/FL\n"
			+ "CPSC250,CPSC250L,MATH135/148/140,CPSC215,AOI,AOI\n"
			+ "CPSC216,MATH125,ECON201,AINW,AINW-Lab,ENGL223\n"
			+ "CPSC335,MATH235/260,ECON202,AINW,AINW-Lab,AOI/FL\n"
			+ "CPSC350,ACCT200,CPEN371W,CPSC440,PRO ELECTIVE\n"
			+ "CPSC351,PSYC202,BUSN303,PRO ELECTIVE,ELECTIVE\n"
			+ "PRO ELECTIVE,PRO ELECTIVE,ELECTIVE,ELECTIVE,ELECTIVE\n"
			+ "CPSC445W,ELECTIVE,ELECTIVE,ELECTIVE\n";
			
			return str;
			
		
		}
		return str;
		
	}
	
	public static String getMajorRequirements(String major) {
		String req = "";
		
		switch (major) {
		
		case ("Computer Science"):
			req += "CPEN214,CPEN371W,CPSC150/150L,CPSC250/250L," +
			"CPSC255,CPSC270,CPSC280,CPSC327,CPSC360,CPSC410,CPSC420," +
			"MATH140/MATH148,MATH240,PHYS151/151L,PHYS201/201L," +
			"PHYS152/152L,PHYS202/202L,PHYS340/ENGR210/MATH235/MATH260,ENGR213,"+
			"PHYS341\n"
			+ "3:(CPSC425;CPSC428;CPSC440;CPSC450;CPSC460;CPSC470;CPSC471;CPSC472;CPSC475;CPSC480;CPSC485;"
			+ "CPSC495;MATH380;PHYS421;PHYS441)";
			return req;
		
		case ("Computer Engineering"):
			req += "CHEM121/121L,CHEM122,PHYS201,PHYS201L,PHYS202,PHYS202L,PHYS341," +
			"MATH140/MATH148,MATH240,MATH320,ENGR121,ENGR210,ENGR211/211L,ENGR212/212L,ENGR213," +
			"CPEN214,CPEN315/315L,CPEN371W,CPEN414,CPEN431,CPEN498W,CPSC150/150L,CPSC250/250L" +
			"CPSC255,CPSC270,CPSC327,CPSC410,CPSC420\n"
			+ "6:(CPEN422;CPEN495;CPSC360;CPSC425;CPSC428;CPSC440;CPSC450;" +
			"CPSC470;CPSC471;CPSC472;CPSC475;CPSC480;CPSC495;PHYS421)";
			return req;
			
		case ("Cyber Security"):
			req += "MATH125/PHYS341,MATH140/MATH148,MATH235,ENGR213,CPSC150/150L,CPSC250/250L,CPSC255,"+
					"CPSC270,CPSC327,CPSC420,CPSC335,CPSC336,CPSC440,CYBR328,CYBR448,CPSC428,CPC429,CPEN371W\n" +
					"2:(CPSC350;CPSC360;CPSC410;CPSC430;CPSC450;CPSC460;CPSC470;CPSC471;CPSC475;CPEN422;CYBR498W)";
			return req;
			
			
		case ("Electrical Engineering"):
			req += "CHEM121/121L,CHEM122,PHYS201/201L,PHYS202/202L,PHYS341,"
			+ "MATH140/MATH148,MATH240,MATH320,MATH250/MATH335/ENGR213,CPSC150/150L,CPSC250/250L,"
			+ "CPSC256/CPSC327,CPEN214,CPEN371W,ENGR121,ENGR210,ENGR211/211L,ENGR212/212L,"
			+ "EENG221,EENG311/311L,EENG321/321L,EENG361/361L,EENG498W\n"
			+ "4:(1:(CPEN315/315L;CPEN414),1:(EENG421;CPEN422),1:(EENG461;EENG481),"
			+ "CPEN315/CPEN315L/CPEN414/EENG421/CPEN422/EENG461/EENG481)";
			return req;
		
		case ("Information Science"):
			req += "CPSC150/150L,CPSC250/250L,MATH125,MATH135/MATH140/MATH148,MATH235/MATH260,"
			+ "ACCT200,ECON201,ECON202,BUSN303,PSYC202,CPSC445W,CPSC215,CPSC216,CPSC335,CPSC350,CPSC351,"
			+ "CPSC440,CPEN371W\n"
			+ "4:(1:(CPSC336;CPSC428;CPSC429),1:(CPSC255;CPSC280;CPSC480),1:(BUSN305;CPSC336CPSC441),"
			+ "CPSC336,CPSC428,CPSC429,CPSC255,CPSC280,CPSC480,BUSN305,CPSC3336,CPSC441,CPSC430,CPSC475,PSYC303,PSYC313,"
			+ "FINC300,MGMT450)";
			return req;
			
		}
		
		return req;
	}
	
	public String getMinorRequirements(String minor) {
		String req = "";
		
		switch (minor) {
		
		case ("Computer Science"):
			req = "CPSC150,CPSC150L,CPSC250,CPSC250L,12xCPSC300/400";
			return req;
		
		case ("Information Science"):
			req = "CPSC215,CPSC150,CPSC150L,CPSC250,CPSC250L,CPSC335,CPSC350,CPSC351";
			return req;
			
		
		}
		
		return req;
	}
	
	
	
	public static void main(String [] args) throws ClassNotFoundException {
		String email = "jimmy.allahmensah.17@cnu.edu";
		String major = getMajor(email);
		System.out.println(major);
	}

}

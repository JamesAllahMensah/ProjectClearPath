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
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		String email = request.getParameter("email");
		String major = null;
		
		try {
			major = getMajor(email);
			out.println(major);
		} catch (ClassNotFoundException e) {
			out.print(e.getLocalizedMessage());
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
	
	public String getMajorString(String major) {
		String str = null;
		switch (major) {
		case ("Computer Science"):
			str = "CPSC150,CPSC150L,ENGL123,AOI,AOI,AOI\n"
			+ "CPSC250,CPSC250L,MATH135/148/140,CPSC215,AOI,AOI\n"
			+ "CPSC216,MATH125,ECON201,AINW,AINW-Lab,ENGL223\n"
			+ "CPSC335,MATH235/260,ECON202,AINW,AINW-Lab,FL/ELECTIVE\n"
			+ "CPSC350,ACCT200,CPEN371W,CPSC440,PRO ELECTIVE\n"
			+ "CPSC351,PSYC202,BUSN303,PRO ELECTIVE,ELECTIVE\n"
			+ "PRO ELECTIVE, PRO ELECTIVE, ELECTIVE, ELECTIVE, ELECTIVE"
			+ "CPSC445W, ELECTIVE, ELECTIVE, ELECTIVE";
			return str;
			
		case ("Computer Engineering"):
			str = "";
			return str;
		
		case ("Cyber Security"):
			str = "";
			return str;
			
		case ("Electrical Engineering"):
			str = "";
			return str;
			
		case ("Information Science"):
			str = "";
			return str;
			
		case ("Physics"):
			str = "";
			return str;
		
		}
		return str;
		
	}
	
	public static void main(String [] args) {
		
	}

}

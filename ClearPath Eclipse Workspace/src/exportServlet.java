import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@WebServlet("/exportServlet")
public class exportServlet extends HttpServlet {
	
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
		String plan = request.getParameter("fullPlan");
		String email = request.getParameter("email");
		String planName = request.getParameter("planName");
		try {
			String fullName = getFullName(email);
			boolean exported = exportPlan(plan, fullName, planName);
			out.println(exported);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean exportPlan(String plan, String studentName, String planName) throws IOException {
		ArrayList<ArrayList<String>> multPlans = new ArrayList<>();
		String[] planArr = plan.split("\n");
		for (int i = 0; i < planArr.length - 1; i += 2) {
			String fall = planArr[i];
			String spring = planArr[i + 1];

			String[] fallCourses = fall.split(";");
			fallCourses = fillArray(fallCourses);

			String[] springCourses = spring.split(";");
			springCourses = fillArray(springCourses);

			for (int j = 0; j < fallCourses.length; j++) {
				ArrayList<String> planHolder = new ArrayList<String>();

				String fallCourseName, fallCourseCredits;
				try {
					fallCourseName = fallCourses[j].split(",")[0];
					if (fallCourseName.equals("BLANK")) {
						fallCourseName = "";
					}
				} catch (Exception e) {
					fallCourseName = "";
				}

				try {
					fallCourseCredits = fallCourses[j].split(",")[1];
					if (fallCourseCredits.equals("100")) {
						fallCourseCredits = "0";
					}
				} catch (Exception e) {
					fallCourseCredits = "0";
				}

				planHolder.add(fallCourseName);
				planHolder.add(fallCourseCredits);

				String springCourseName, springCourseCredits;
				try {
					springCourseName = springCourses[j].split(",")[0];
					if (springCourseName.equals("BLANK")) {
						springCourseName = "";
					}
				} catch (Exception ex) {
					springCourseName = "";
				}

				try {
					springCourseCredits = springCourses[j].split(",")[1];
					if (springCourseCredits.equals("100")) {
						springCourseCredits = "0";
					}
				} catch (Exception ex) {
					springCourseCredits = "0";
				}

				planHolder.add(springCourseName);
				planHolder.add(springCourseCredits);

				multPlans.add(planHolder);
			}

		}

		String fileName = studentName + "_" + planName + ".csv";
		String home = System.getProperty("user.home");
		File file = new File(home+"/Downloads/" + fileName); 
		FileWriter writer = new FileWriter(file);
		writer.write(planName);
		writer.write("\n");
		writer.write(studentName);
		writer.write("\n");
		writer.write("\n");

		int splitCount = 0;
		int semesterLabel = 0;
		int fallCnt = 0;
		int springCnt = 0;
		int totalCredits = 0;
		for (int i = 0; i < multPlans.size(); i++) {
			ArrayList<String> courseInfo = multPlans.get(i);
			if (splitCount == 0) {
				String semLabel = getSemesterLabel(semesterLabel);
				semesterLabel++;

				writer.write(semLabel);
				writer.write("\n");
				writer.write("Fall Semester,,Spring Semester");
				writer.write("\n");
			}
			try {
				fallCnt += Integer.parseInt(courseInfo.get(1));
				springCnt += Integer.parseInt(courseInfo.get(3));
				totalCredits += (Integer.parseInt(courseInfo.get(1)) + Integer.parseInt(courseInfo.get(3)));
				writer.write(courseInfo.get(0) + "," + courseInfo.get(1) + "," + courseInfo.get(2) + ","
						+ courseInfo.get(3));
			} catch (Exception ex) {
				fallCnt += 0;
				springCnt += 0;
				totalCredits += 0;
				writer.write(",,,,");
			}
			writer.write("\n");
			splitCount++;
			if (splitCount == 6) {
				writer.write("Credits: " + fallCnt + ",,Credits: " + springCnt);
				writer.write("\n");
				writer.write("\n");
				splitCount = 0;
				fallCnt = 0;
				springCnt = 0;
			}

		}
		writer.write("Total Credits: " + totalCredits);
		writer.close();

		return true;
	}
	
	public static String getFullName(String email) throws ClassNotFoundException {
		String name;

		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			String SQL = "SELECT Name FROM Student WHERE Email = ?";
			PreparedStatement statement = connection.prepareStatement(SQL);
			statement.setString(1, email);
			ResultSet rs = statement.executeQuery();

			boolean res = rs.next();
			
			name = rs.getString("Name");
			
			connection.close();
			return name;
			

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String[] fillArray(String[] arr) {
		ArrayList<String> list = new ArrayList<String>();
		if (arr.length == 6) {
			return arr;
		} else {
			int length = arr.length;
			int remainder = 6 - length;
			for (int x = 0; x < arr.length; x++) {
				String info = arr[x];
				list.add(info);
			}

			int i = 0;
			while (i < remainder) {
				list.add("BLANK,100");
				i++;
			}
		}

		String[] ret = new String[6];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = list.get(i);
		}
		return ret;
	}

	public static String getSemesterLabel(int number) {
		switch (number) {
		case (0):
			return "First Year";

		case (1):
			return "Second Year";

		case (2):
			return "Third Year";

		case (3):
			return "Fourth Year";

		}
		return null;
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
//		String plan = "CPSC150,3;CPSC150L,1;MATH140,3;AOI/ECON/FL,3;ENGL123,3;ClassA,3;\n"
//		+"CPSC250,3;CPSC250L,1;MATH240,3;AOI/ECON/FL,3;AOI/ECON/FL,3;AOI/ECON/FL,3;\n"
//		+"CPSC255,3;PHYS151/201,3;PHYS151L/201L,1;MATH235/260/ENGR340/PHYS340,3;ENGL223,3;AOI/ECON/FL,3;\n"
//		+"CPSC270,3;CPSC280,3;PHYS152/PHYS202,3;PHYS152L/202L,1;CPEN214,3;ENGR213,3;\n"
//		+"CPSC327,3;CPSC360,3;PHYS341,3;PRO ELECTIVE,3;AOI/ECON/FL,3;ClassB,3;\n"
//		+"CPSC410,3;CPSC420,3;CPEN371W,2;AINW,3;AINW-Lab,1;PRO ELECTIVE,3;\n"
//		+"PRO ELECTIVE,3;AINW,3;ELECTIVE,3;WI2,2;ELECTIVE,3;ClassC,3;\n"
//		+"CPSC498,3;ELECTIVE,3;ELECTIVE,3;ELECTIVE,3;ClassD,3;ClassE,3";

		String plan = "CPSC150,3;CPSC150L,1;MATH140,3;AOI/ECON/FL,3;ENGL123,3;\n"
				+ "CPSC250,3;CPSC250L,1;MATH240,3;AOI/ECON/FL,3;AOI/ECON/FL,3;AOI/ECON/FL,3;\n"
				+ "CPSC255,3;PHYS151/201,3;PHYS151L/201L,1;;ENGL223,3;AOI/ECON/FL,3;\n"
				+ "CPSC270,3;CPSC280,3;PHYS152/PHYS202,3;PHYS152L/202L,1;CPEN214,3;ENGR213,3;\n"
				+ "CPSC327,3;CPSC360,3;PHYS341,3;PRO ELECTIVE,3;AOI/ECON/FL,3;ClassB,3;\n"
				+ "CPSC410,3;CPSC420,3;CPEN371W,2;AINW,3;AINW-Lab,1;PRO ELECTIVE,3;\n"
				+ "PRO ELECTIVE,3;AINW,3;ELECTIVE,3;WI2,2;ELECTIVE,3;ClassC,3;\n"
				+ "CPSC498,3;ELECTIVE,3;ELECTIVE,3;ELECTIVE,3;ClassD,3;ClassE,3";

		String studentName = "Jimmy Allah-Mensah";
		String planName = "Computer Science Plan A";

		exportPlan(plan, studentName, planName);
		

	}

}

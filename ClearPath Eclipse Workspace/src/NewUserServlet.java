import java.io.*;
import java.sql.*;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

@WebServlet("/NewUserServlet")
public class NewUserServlet extends HttpServlet {

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
		String advisor = request.getParameter("advisor");
		try {
			setAdvisor(email, advisor);
			out.println("Success.");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}


	}
	
	public static void setAdvisor(String email, String advisor) throws ClassNotFoundException {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String connectionUrl = "jdbc:sqlserver://DESKTOP-RI159U3:1433;databaseName=ClearPath";
			Connection connection = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
			
			String SQL = "UPDATE Student SET Advisor = ? WHERE Email = ?";
			PreparedStatement ps = connection.prepareStatement(SQL);
			ps.setString(1, advisor);
			ps.setString(2, email);
			
			int insertedRows = ps.executeUpdate();

			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	

	public static void main(String[] args) throws ClassNotFoundException {
		setAdvisor("James.Mensah@cnu.edu", "Lambert");

	}

}

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Advisor {
	
	private String name;
	private String email;
	private String students;
	
	public Advisor(String name, String email, String students) {
		this.name = name;
		this.email = email;
		this.students = students;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public ArrayList<String> getStudentList(){
		ArrayList<String> students = new ArrayList<>();
		if (this.students.length() == 0) {
			return students;
		}
		
		Scanner scnr = new Scanner(this.students);
		scnr.useDelimiter(";");
		
		while (scnr.hasNext()) {
			String student = scnr.next().trim();
			students.add(student);
		}
		return students;
		
	}
	
	public String toString() {
		String str = "Advisor Name: " + this.name + ". Advisor Email: " + this.email + ". Students: " + this.students;
		return str;
	}
	
//	public static void main(String [] args) {
//		Advisor Lambert = new Advisor("Lynn Lambert", "Lynn.lambert@cnu.edu", "Jimmy Allah-Mensah;John Johnson;Robert Perkins; Adam Bates");
//		System.out.println(Lambert.getName());
//	}

}

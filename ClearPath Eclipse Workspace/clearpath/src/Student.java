
public class Student {

	private String name;
	private String email;
	private String major;
	private String minor;
	private boolean isGrad;
	private String plan;
	private String advisor;
	private String planNames;

	public Student(String name, String email, String major, String minor, boolean isGrad, String plan, String advisor,
			String planNames) {

		this.name = name;
		this.email = email;
		this.major = major;
		this.minor = minor;
		this.isGrad = isGrad;
		this.plan = plan;
		this.advisor = advisor;
		this.planNames = planNames;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPlanNames() {
		return planNames;
	}
	
	public String getMajor() {
		return major;
	}
	
	public String getMinor() {
		return minor;
	}

	public String toString() {
		
		String str = name + " " + email + " " + major + " " + minor + " " + isGrad + " " + " " + planNames + " " + advisor + " " + plan;
		return str;
	}
	
	
	
//	public void setPlan() {
//		String plan = "";
//		if (!this.isGrad && major.equals("Computer Science")) {
//			plan = "";
//		}
//		else {
//			plan = "";
//		}
//		this.plan = plan;
//	}
	
	public static void main(String [] args) {
		
	}

}

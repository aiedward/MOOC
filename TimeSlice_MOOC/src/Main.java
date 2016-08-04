import java.io.IOException;


public class Main {
	
	public static void main(String[] args) throws IOException {
		
		Tool tool = new Tool();
		tool.getCourseData();
		tool.getReviewData();
		tool.calculateInterval();
		tool.writeResults();
		

		
	}
}

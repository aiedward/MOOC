import java.io.IOException;


public class Main {
	
	public static void main(String[] args) throws IOException {
		
		Tool tool = new Tool();
		tool.getCourseData();
		tool.getReviewData();
		
		tool.adjustDataForTimeSeries(); 	// this function is to adjust data for Cross-sectional Time series (Fixed Effects model);
		tool.makeXTdata();
//		tool.writeXTdataToJson();	// this function is to adjust data for Cross-sectional Time series (Fixed Effects model);
		
		
		
//		tool.calculateInterval();
//		tool.writeResults();
		
		

		
	}
}

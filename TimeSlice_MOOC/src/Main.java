import java.io.IOException;


public class Main {
	
	public static void main(String[] args) throws IOException {
		
		Tool tool = new Tool();
		tool.getCourseData();
		tool.getReviewData();
		tool.getKeywordData();
		
		tool.adjustDataForTimeSeries(); // this function is to adjust data for Cross-sectional Time series (Fixed Effects model);
		tool.makeXTdata();
		tool.calculateCourseReviewCountandValue();
		
//		tool.printCourseListWithStartingPoint();
		
//		tool.printCourseListWithTime();
//		tool.writeXTdataToJson();
//		tool.writeReviewToJson();
//		tool.printTimeSeries();
		
//		tool.courseTimeAdjust();
		
		tool.writeXTdataToCsv();
		tool.writeXTReviewToCsv();
//		tool.filterTimeForWritingConnection();

	
				
	}
}

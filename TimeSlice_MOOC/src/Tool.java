import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.opencsv.CSVReader;

public class Tool {
	
	private CSVReader mReader = null;
	private HashMap<Integer, String> term_map = null;
	SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public ArrayList<Course> courseList = null;
	public ArrayList<Review> reviewList = null;
//	private ArrayList<Course> courseList;
	
	public void getCourseData() throws IOException{
		
		final String inputFile = "Integrated_Course_20160712"; 
		
		try{
			mReader = new CSVReader(new FileReader(inputFile + ".csv"));
		}catch (FileNotFoundException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println(inputFile + " file open succeed");
		String[] nextLine = null;
		
		
		courseList = new ArrayList<Course>();
		
		
		// read dummy first of all.
		mReader.readNext();
		boolean flag;
	
		while( (nextLine = mReader.readNext()) != null ){
			
			flag = false;
			
//			System.out.println(Charset.forName("UTF-8").encode(nextLine[2]) );
			
//			System.out.println(nextLine[0]);
			String title = nextLine[0];
			String provier = nextLine[1];
			String subject = nextLine[2];
			String school = nextLine[3];
			String language = nextLine[4];
			String url = nextLine[14];
			
			
//			System.out.println(nextLine[6]);
			
			
			Date session = null;
			try {
				session = transFormat.parse(nextLine[6]);
//				System.out.println(session);
//				System.out.println(transFormat.format(session));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int course_length = Integer.valueOf(nextLine[7]);
						
			
			
			
			if(courseList.size() > 0){
				
				for(int i=0; i < courseList.size(); i++){
					if(courseList.get(i).url.equals(url)){
						courseList.get(i).sessionList.add(session);
						flag = true;
						continue;
					}
				}
			}
			
			if(flag) continue;
			
			Course temp = new Course();
			temp.sessionList = new ArrayList<Date>();
			temp.title = title;
			temp.provider = provier;
			temp.subject = subject;
			temp.school = school;
			temp.sessionList.add(session);
			temp.course_length = course_length;
			temp.url = url;
			
			courseList.add(temp);
			
			temp = null;
			
			
									
		}
		
		
		for(int i=0; i<courseList.size(); i++){
			courseList.get(i).reviewIntervalList = new ArrayList<Date>();
			courseList.get(i).endSessionList = new ArrayList<Date>();
			
			if(courseList.get(i).title.equals("Machine Learning")){
				System.out.println("sessionList Size of Machine Learning - " + courseList.get(i).sessionList.size());
			}
			
//			System.out.println(courseList.get(i).title);
//			System.out.println("session count - "  + courseList.get(i).sessionList.size());
//			System.out.println("course length(weeks) - " + courseList.get(i).course_length);
			for(int j=0; j<courseList.get(i).sessionList.size(); j++){
//				System.out.println("session date - "  + transFormat.format(courseList.get(i).sessionList.get(j)));
							
				// review interval calculate and store
				Calendar c = Calendar.getInstance();
				c.setTime(courseList.get(i).sessionList.get(j));
				c.add(Calendar.DATE, courseList.get(i).course_length*7);
				courseList.get(i).endSessionList.add(c.getTime());
				c.add(Calendar.MONTH, 3);
				
				courseList.get(i).reviewIntervalList.add(c.getTime());
//				System.out.println("review date interval - " + transFormat.format(c.getTime()));
			}
			
			
			
//			course.sessionList.get(0) + course.course_length;
			
		}
		System.out.println("size of courseList - " + courseList.size());
			
		
		mReader.close();
		
//		writeConnectionData("connection");

	}
	
	
	public void getReviewData() throws IOException{
		
		final String inputFile = "Integrated_Reivew_20160712";
		
		reviewList = new ArrayList<Review>();
		
		try{
			mReader = new CSVReader(new FileReader(inputFile + ".csv"));
		}catch (FileNotFoundException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println(inputFile + " file open succeed");
		String[] nextLine = null;
		mReader.readNext();

	
		while( (nextLine = mReader.readNext()) != null ){
			

//			System.out.println(nextLine[2]);
			
			String platform = nextLine[1];
			String title = nextLine[2];
			String provider = nextLine[3];
			float review_value = Float.valueOf(nextLine[5]);
			
			Date review_date = null;
			try {
				review_date = transFormat.parse(nextLine[6]);
//				System.out.println(session);
//				System.out.println(transFormat.format(session));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
						
			Review temp = new Review();
			temp.platform = platform;
			temp.title = title;
			temp.provider = provider;
			temp.review_date = review_date;
			temp.review_value = review_value;
			
			
			reviewList.add(temp);
			
			temp = null;
									
		}
		
		System.out.println("reviewList size - " + reviewList.size());
		
			
		
		mReader.close();
		
//		writeConnectionData("connection");

	}
	
	public void calculateInterval(){
		
		for(int i=0; i<courseList.size(); i++){
			courseList.get(i).reviewCount = new ArrayList<Integer>();
			courseList.get(i).reviewValue = new ArrayList<Float>();
			
			// session list 크기만큼 reviewCount와 reviewValue list initialization
			for(int x =0; x<courseList.get(i).sessionList.size(); x++){
				courseList.get(i).reviewCount.add(0);
				courseList.get(i).reviewValue.add(0f);
			}
			
			for(int j=0; j<reviewList.size(); j++){
				
				// title and provider matched
				if(reviewList.get(j).provider.equals(courseList.get(i).provider) 
						&& reviewList.get(j).title.equals(courseList.get(i).title)){
					
					// 순수 total reviewCount & reviewValue 계산 시 코드
					courseList.get(i).totalReview_count = courseList.get(i).totalReview_count + 1;
					courseList.get(i).totalReview_value = courseList.get(i).totalReview_value + reviewList.get(j).review_value;
					

						
					// review interval comparison
					for(int k=0; k<courseList.get(i).sessionList.size(); k++){
						Date startSession = courseList.get(i).sessionList.get(k);
						Date interval = courseList.get(i).reviewIntervalList.get(k);
						Date reviewDate = reviewList.get(j).review_date;
						
						if(startSession.before(reviewDate) ){
							
							if(reviewDate.before(interval)){
	//									System.out.println("startSession - " + transFormat.format(startSession));
	//									System.out.println("reviewDate - " + transFormat.format(reviewDate));
	//									System.out.println("interval - " + transFormat.format(interval));
								float pre_reviewValue = courseList.get(i).reviewValue.get(k);
								int pre_reviewCount = courseList.get(i).reviewCount.get(k) + 1 ;
								courseList.get(i).reviewValue.set(k, pre_reviewValue + reviewList.get(j).review_value );
								courseList.get(i).reviewCount.set(k, pre_reviewCount);
															
							}
							
						}else{
							
						}
							
					}
								
				}else{
					continue;
				}
				
			}
			
			
			
					
			
		}
		
		for(int i=0; i<courseList.size(); i++){
			courseList.get(i).duplicated_flag = new ArrayList<String>();
			courseList.get(i).differenceBetweenSessions = new ArrayList<Long>();
			
//			System.out.println("title - " + courseList.get(i).title);
			boolean flag_of_duplicated = false;
			for(int j=0; j<courseList.get(i).sessionList.size(); j++){
//				System.out.println("sesssion - " + transFormat.format(courseList.get(i).sessionList.get(j) ) + "; interval -" + transFormat.format( courseList.get(i).reviewIntervalList.get(j) )
//				+ "; reviewCount - " + transFormat.format( courseList.get(i).reviewCount.get(j)) );
				
				String duplicated;
				long diff;
				long diffDays;
				
				
				if(j+1 < courseList.get(i).sessionList.size()){
					Date current_startSession = courseList.get(i).sessionList.get(j);
					Date past_endSession = courseList.get(i).endSessionList.get(j+1);
					
					diff = current_startSession.getTime() - past_endSession.getTime();
					
					diffDays = diff / (24*60*60*1000);
					
					
					
					if(current_startSession.before(past_endSession) || current_startSession.equals(past_endSession)){
						duplicated = "duplicated with the last session";
						flag_of_duplicated = true;
						
					}else{
//						
						duplicated = "not duplicated with the last session";
					}
					
				}else{
					duplicated = "first session";
					diffDays = 0;
				}
				courseList.get(i).duplicated_flag.add(duplicated);
				courseList.get(i).differenceBetweenSessions.add(diffDays);
				
				// interval 별 review count summation시 필요한 코드. 
//				courseList.get(i).totalReview_count = courseList.get(i).totalReview_count + courseList.get(i).reviewCount.get(j);
//				courseList.get(i).totalReview_value = courseList.get(i).totalReview_value + courseList.get(i).reviewValue.get(j);
			}
			
			if(flag_of_duplicated == true){
				courseList.get(i).flag_of_duplicated = "TRUE";
			}else{
				courseList.get(i).flag_of_duplicated = "FALSE";
			}
			
			
//			System.out.println("total reviewCount = " + courseList.get(i).totalReview_count);
//			System.out.println("total reviewValue = " + courseList.get(i).totalReview_value);
		}
		
		
		
	}
	

	public void writeResults() {
		// TODO Auto-generated method stub
		String name = "./calculatedInterval.txt";
		
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "UTF-8"));
			String s = "url%title%totalReviewCount%totalReviewvalue%startSession%endSession%differenceBetwwenSessions%duplicated%duplicated_min_one%interval%reviewCount%reviewValue%sessionSize";
			
			out.write(s);
			out.newLine();
			
			for(int i=0; i<courseList.size(); i++){
				s = courseList.get(i).url + "%" + courseList.get(i).title + "%" + courseList.get(i).totalReview_count + "%" + courseList.get(i).totalReview_value + "%";
				
				for(int j=0; j<courseList.get(i).sessionList.size(); j++){
					String s_1 = transFormat.format(courseList.get(i).sessionList.get(j)).toString() + "%" +
							transFormat.format(courseList.get(i).endSessionList.get(j)).toString() + "%" + courseList.get(i).differenceBetweenSessions.get(j).toString() + "%" +
							courseList.get(i).duplicated_flag.get(j) + "%" + courseList.get(i).flag_of_duplicated.toString() + "%" + 
							transFormat.format(courseList.get(i).reviewIntervalList.get(j)).toString() + "%" +
									courseList.get(i).reviewCount.get(j).toString() +"%" + courseList.get(i).reviewValue.get(j).toString() + "%" +
									courseList.get(i).sessionList.size();
					out.write(s+s_1);
					out.newLine();
							
				}
			}
			out.close();
			
		}catch(IOException e){
			System.err.println(e);
			System.exit(1);
		}
		
	}
	

	




}

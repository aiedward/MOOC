import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class Tool {
	
	private CSVReader mReader = null;
	private Time timeFor_fixedEffect;
	
	SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	public ArrayList<Course> courseList = null;
	public ArrayList<Review> reviewList = null;
//	private ArrayList<Course> courseList;
	
	public void getCourseData() throws IOException{
		
		final String inputFile = "Integrated_Course_20160712"; 
		
		try{
			mReader = new CSVReader(new FileReader(inputFile + ".csv"));
		}catch (FileNotFoundException e){
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
			
			int session_length = Integer.valueOf(nextLine[7]);
				
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
			temp.language = language;
			temp.sessionList.add(session);
			temp.session_length = session_length;
			temp.url = url;
			
			courseList.add(temp);
			
			temp = null;
										
		}
		
		
		for(int i=0; i<courseList.size(); i++){
			courseList.get(i).reviewIntervalList = new ArrayList<Date>();
			courseList.get(i).endSessionList = new ArrayList<Date>();
			
			// There are some duplicated title of courses .. so needed check
			if(courseList.get(i).title.equals("Machine Learning")){
				System.out.println("sessionList Size of Machine Learning - " + courseList.get(i).sessionList.size());
			}
			
//			System.out.println(courseList.get(i).title);
//			System.out.println("session count - "  + courseList.get(i).sessionList.size());
//			System.out.println("course length(weeks) - " + courseList.get(i).session_length);
			for(int j=0; j<courseList.get(i).sessionList.size(); j++){
//				System.out.println("session date - "  + transFormat.format(courseList.get(i).sessionList.get(j)));
							
				// review interval calculate and store
				Calendar c = Calendar.getInstance();
				c.setTime(courseList.get(i).sessionList.get(j));
				c.add(Calendar.DATE, courseList.get(i).session_length*7);
				courseList.get(i).endSessionList.add(c.getTime());
				c.add(Calendar.MONTH, 3);
				
				
				courseList.get(i).reviewIntervalList.add(c.getTime());
//				System.out.println("review date interval - " + transFormat.format(c.getTime()));
			}
					
		}
		System.out.println("size of courseList - " + courseList.size());
		
		mReader.close();
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
			
			String course_url = nextLine[0];
			String platform = nextLine[1];
			String title = nextLine[2];
			String provider = nextLine[3];
			float review_value = Float.valueOf(nextLine[5]);
			String reviewer_id = nextLine[7];
			
			Date review_date = null;
			try {
				review_date = transFormat.parse(nextLine[6]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
						
			Review temp = new Review();
			temp.course_url = course_url;
			temp.platform = platform;
			temp.title = title;
			temp.provider = provider;
			temp.review_date = review_date;
			temp.review_value = review_value;
			temp.reviewer_id = reviewer_id;	// this is reviewer's url
				
			
			reviewList.add(temp);
			
			temp = null;
									
		}
		
		System.out.println("reviewList size - " + reviewList.size());
				
		mReader.close();

	}
	
	public void getKeywordData() throws IOException{
		
		final String inputFile = "keywords_MOOCs";
		
		try{
			mReader = new CSVReader(new FileReader(inputFile + ".csv"));
		}catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println(inputFile + " file open succeed");
		String[] nextLine = null;
		mReader.readNext();
		
		for(Course course: courseList){
			course.keywordsList = new ArrayList<String>();
		}
		
		while( (nextLine = mReader.readNext()) != null ){
			ArrayList<String> tempList = new ArrayList<String>();
			
			String course_url = nextLine[0];
			for(int i=1; i<nextLine.length; i++){
				String temp_keyword = nextLine[i];
				tempList.add(temp_keyword);
				
			}
			
			for(Course course: courseList){
				if(course.url.equals(course_url)){
					course.keywordsList = tempList;
					tempList = null;
					continue;
				}
			}
		}
				
		mReader.close();
	
	}
	
	public void adjustDataForTimeSeries() {
		// sort session list
		System.out.println("session List sorting start");
		for(int i=0; i<courseList.size(); i++){
			
			// define collection.sort method to sort by Date
			Collections.sort(courseList.get(i).sessionList, new Comparator<Date>(){
				public int compare(Date d1, Date d2){
					return d1.before(d2) ? -1 : 1;
				}
			});
		}
		System.out.println("session List sorting finished and assign starting point(opening day) to every course");
		for(int i=0; i<courseList.size(); i++){
			// assign to every element of courseList with first date of sessionList.
			courseList.get(i).startingPoint = courseList.get(i).sessionList.get(0);
			
		}
					
		// sort courseList by course opening date(session start date)
		System.out.println("courseList sotring start");
		Collections.sort(courseList, new Comparator<Course>(){
			public int compare(Course c1, Course c2){
				return c1.startingPoint.before(c2.startingPoint) ? -1 : 1;
			}
			
		});
		

		// sort reviewList by date
		System.out.println("reviewList sorting start");
		Collections.sort(reviewList, new Comparator<Review>(){
			public int compare(Review r1, Review r2){
				if(r1.review_date.before(r2.review_date))
					return -1;
				else if(r1.review_date.equals(r2.review_date))
					return 0;
				else
					return 1;
			
			}
			
		});
		System.out.println("reviewList sorting finished");
				
	}
	
	
	
	// for adding interval to the courseList
	public void updateIntervaltoCourseList(int timeIndex, int reviewIndex ){
		
		Review review = reviewList.get(reviewIndex);
		CourseTime tempCourseTime = new CourseTime();
		
		for(int i=0; i<courseList.size(); i++){
			// matching with course title and platform
			if(courseList.get(i).title.equals(review.title) && courseList.get(i).provider.equals(review.provider)){
				// update the element of the courseList
//				System.out.println(courseList.get(i).title);
												
				boolean flagOfadded = false;
				for(int j=0; j<courseList.get(i).TimeData.size(); j++){
					// if Time-series data has already the same interval store, then just add up 1 review count and value
					if(courseList.get(i).TimeData.get(j).timeIndex == timeIndex){
						courseList.get(i).TimeData.get(j).reviewValue += review.review_value;
						courseList.get(i).TimeData.get(j).reviewCount += 1;
						flagOfadded = true;
						break;
					}
				}
				
				// if Time-series data doesn't have any element, then just add the time data
				if(courseList.get(i).TimeData.isEmpty() || !flagOfadded){
					tempCourseTime.reviewCount = 1;
					tempCourseTime.reviewValue = review.review_value;
					tempCourseTime.timeIndex = timeIndex;
					courseList.get(i).TimeData.add(tempCourseTime);
				}
			
			}
		}
		
	}
	
	// add review count to each course's totalReview_count from timeSeries Data
	// and normalize review value between 0~5
	public void calculateCourseReviewCountandValue(){
		
		for(int i=0; i<courseList.size(); i++){
						
			for(int j=0; j<courseList.get(i).TimeData.size(); j++){
				courseList.get(i).totalReview_count += courseList.get(i).TimeData.get(j).reviewCount;
				courseList.get(i).totalReview_value += courseList.get(i).TimeData.get(j).reviewValue;
				
			}
			courseList.get(i).totalReview_value = courseList.get(i).totalReview_value / courseList.get(i).totalReview_count; 
		
		}
	}

	
	public void makeXTdata(){
		// first, assign to T1 with  
		timeFor_fixedEffect = new Time();
		timeFor_fixedEffect.ReviewCountList = new ArrayList<Integer>();
		timeFor_fixedEffect.ReviewValueList = new ArrayList<Float>();
		timeFor_fixedEffect.TimeList = new ArrayList<Date>();
		
		timeFor_fixedEffect.StartingPoint = courseList.get(0).startingPoint;
		timeFor_fixedEffect.TimeList.add(timeFor_fixedEffect.StartingPoint);
		timeFor_fixedEffect.ReviewCountList.add(0);
		timeFor_fixedEffect.ReviewValueList.add(0f);
		System.out.println( "T1 - " + transFormat.format(timeFor_fixedEffect.StartingPoint) );
		
		// cousreTimeList init to add each time interval
		for(Course course : courseList)
			course.TimeData = new ArrayList<CourseTime>();
		
		// Set interval as <<<< 1 week >>>>(ex: T1 to T2)
		int Timeinterval = 28;
		
		Date nextTime = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(timeFor_fixedEffect.StartingPoint);
		c.add(Calendar.DATE, Timeinterval);
		nextTime = c.getTime();
//		c.add(Calendar.MONTH, 3);
		
		timeFor_fixedEffect.TimeList.add(nextTime);
		timeFor_fixedEffect.ReviewCountList.add(0);
		timeFor_fixedEffect.ReviewValueList.add(0f);
		System.out.println( "T2 - " + transFormat.format(nextTime));
		
		// set destination time as 08/01/2016 (07/30/2016)		
		Calendar destDate = new GregorianCalendar(2016, Calendar.JULY, 30);
		Date destinationTime = destDate.getTime();
		System.out.println( "Tn - " + transFormat.format(destinationTime));
		
		// Make interval list
		while(true){
			
			if(nextTime.after(destinationTime) || nextTime.equals(destinationTime)) 
				break;
			
			c = Calendar.getInstance();
			c.setTime(nextTime);
			c.add(Calendar.DATE, Timeinterval);
			nextTime = c.getTime();
			
			timeFor_fixedEffect.TimeList.add(nextTime);
			timeFor_fixedEffect.ReviewCountList.add(0);
			timeFor_fixedEffect.ReviewValueList.add(0f);
//			System.out.println(transFormat.format(nextTime));		
	
		}
		
		// assign review count and value to XTData 
		for(int i=0; i<timeFor_fixedEffect.TimeList.size()-1; i++){
			// Set startPoint as Ti < reviewDate < T(i+1) +1day
			Date startPoint = timeFor_fixedEffect.TimeList.get(i);
						
			c = Calendar.getInstance();
			c.setTime(timeFor_fixedEffect.TimeList.get(i+1));
			c.add(Calendar.DATE, 1);
			Date endPoint = c.getTime();
			int currentReviewCount = timeFor_fixedEffect.ReviewCountList.get(i);
			float currentReviewValue = timeFor_fixedEffect.ReviewValueList.get(i);
			
			for(int j=0; j<reviewList.size(); j++){
				
				// this condition is for checking whether reviewer has their ID.
				if(reviewList.get(j).reviewer_id.equals("null"))
					continue;	
				
				if(startPoint.before(reviewList.get(j).review_date) && endPoint.after(reviewList.get(j).review_date)){
										
					timeFor_fixedEffect.ReviewCountList.set(i, currentReviewCount++);
					currentReviewValue += reviewList.get(j).review_value;
					timeFor_fixedEffect.ReviewValueList.set(i, currentReviewValue);
					
					// update this Interval to ReviewList
					reviewList.get(j).includedTime = i;
									
					// call the update function for adding interval to the courseList 
					updateIntervaltoCourseList(i, j);
				}
			}
			// add reviewCount and reviewValue to total review and total value
			timeFor_fixedEffect.totalReviewCount += currentReviewCount;
			timeFor_fixedEffect.totalReviewValue += currentReviewValue;
					
		}
		

	}
	
	public void filterTimeForWritingConnection(){
		ArrayList <Integer> existingTimeList = new ArrayList<Integer>();
		
		for(int i=0; i<courseList.size(); i++){
			
			for(int j=0; j<courseList.get(i).TimeData.size(); j++){
				int temp_time = courseList.get(i).TimeData.get(j).timeIndex;
				if(existingTimeList.contains(temp_time)){
					continue;
				}else{
					existingTimeList.add(temp_time);
				}
			}
			
		}
		// classical sort
//		existingTimeList.sort(new Comparator<Integer>(){
//			public int compare(Integer i1, Integer i2){
//				return i2 - i1;
//			}
//		});
		
		// sorting arrayList with lamda
		existingTimeList.sort((i1,i2)-> i1 - i2);
		
		for(int timeIndex : existingTimeList){
			
			try {
				writeConnectionData(timeIndex);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		}
		
	}
	
	///////// Write social edge file function ///////
	public void writeConnectionData(int timeIndex) throws IOException{
		
		String result = "./results_network/networkData_T-" + timeIndex;
		CSVWriter writer = new CSVWriter(new FileWriter(result + ".csv"));
		
		System.out.println("time - " + timeIndex );
		int num_courses = 0;
		
		// course_title, reviewer_id (edge-list)
		String [] colname = {"course","reviewer"};
		writer.writeNext(colname);
		
		ArrayList<String> tempCourses = new ArrayList <String>();
		for(int i=0; i<courseList.size(); i++){
			boolean course_flag = false;
			
			for(int j=0; j<courseList.get(i).TimeData.size(); j++){
				
				// compare current timeIndex with courses' timeIndex
				if(timeIndex == courseList.get(i).TimeData.get(j).timeIndex){
					String title = courseList.get(i).title;
					String platform = courseList.get(i).provider;
							
					for(int k=0; k<reviewList.size(); k++){
						if(reviewList.get(k).reviewer_id.equals("null"))
							continue;
						
						if(title.equals(reviewList.get(k).title) && platform.equals(reviewList.get(k).provider)){
							String [] edge = {title, reviewList.get(k).reviewer_id };
							writer.writeNext(edge);
							edge = null;
							course_flag = true;
						}
					}
					
				}
										
			}
			
			if(course_flag)
				num_courses ++;
		}
		
		System.out.println("the number of courses = " + num_courses);
		writer.close();
		
	}

	///////// Write csv file function /////////
	
	public void writeXTdataToCsv() throws IOException{
		
		String result = "./results/CourseXTdata.csv";
		CSVWriter writer = new CSVWriter(new FileWriter(result));
		
		ArrayList<String> record = new ArrayList<String>();
		// url,title,platform,institution,subject,language,keywords,totalReviewCount,totalReviewValue,
		// timeIndex,timeReviewCount,timeReviewValue
		String [] colname = { "url","title","platform","institution","subject"
				,"language","keywords","totalReviewCount","totalReviewValue",
				"time","timeReviewCount","timeReviewValue"};
		
		writer.writeNext(colname);
		for(int i=0; i<courseList.size(); i++){
			
			record.add(courseList.get(i).url);
			record.add(courseList.get(i).title);
			record.add(courseList.get(i).provider);
			record.add(courseList.get(i).school);
			record.add(courseList.get(i).subject);
			record.add(courseList.get(i).language);
			String s = ""; 
			for(int j=0; j<courseList.get(i).keywordsList.size(); j++){
				if( j == courseList.get(i).keywordsList.size()-1)
					s += courseList.get(i).keywordsList.get(j);
				else
					s += courseList.get(i).keywordsList.get(j) + ",";
			}
			record.add(s);
			record.add(String.valueOf(courseList.get(i).totalReview_count));
			record.add(String.valueOf(courseList.get(i).totalReview_value));
			
			// Time
			for(int j=0; j<courseList.get(i).TimeData.size(); j++){
				record.add(String.valueOf(courseList.get(i).TimeData.get(j).timeIndex));
				record.add(String.valueOf(courseList.get(i).TimeData.get(j).reviewCount));
				record.add(String.valueOf(courseList.get(i).TimeData.get(j).reviewValue));
				
				String [] temp = record.toArray(new String[record.size()]);
				writer.writeNext(temp);
				int list_length = record.size();
				record.remove(list_length-1);
				record.remove(list_length-2);
				record.remove(list_length-3);
				temp = null;
			}
			
			
			
//			String [] temp = record.toArray(new String[record.size()]);
//			writer.writeNext(temp);
			
			record.clear();
//			temp = null;
		}
		writer.close();
		
	}
	
	public void writeXTReviewToCsv() throws IOException{
		
		String result = "./results/reviewXTdata.csv";
		CSVWriter writer = new CSVWriter(new FileWriter(result));
		
		ArrayList<String> record = new ArrayList<String>();
		// course_url, title, course_platform, review_platfom, reviewer_id, reviewDate, time, reviewValue,

		String [] colname = { "course_url","title","course_platform","review_platform","time","reviewr_id"
				,"review_date","review_value"};
		
		writer.writeNext(colname);
		for(int i=0; i<reviewList.size(); i++){
			if(reviewList.get(i).reviewer_id.equals("null"))
				continue;
			
			record.add(reviewList.get(i).course_url);
			record.add(reviewList.get(i).title);
			record.add(reviewList.get(i).provider);
			record.add(reviewList.get(i).platform);
			record.add(String.valueOf(reviewList.get(i).includedTime));
			record.add(reviewList.get(i).reviewer_id);
			record.add(transFormat.format(reviewList.get(i).review_date));
			record.add(String.valueOf(reviewList.get(i).review_value));
		
			String [] temp = record.toArray(new String[record.size()]);
			writer.writeNext(temp);
			
			record.clear();
			temp = null;
		}
		writer.close();
		
	}

	
	////////// Write json file function //////////
	
	public void writeXTdataToJson() {
		
		JSONObject root = new JSONObject();
		JSONArray course_list = new JSONArray();
			
//		for(int i=0; i<10; i++){
		for(int i=0; i<courseList.size(); i++){
			
			JSONObject obj = new JSONObject();
			obj.put("url", courseList.get(i).url);
			obj.put("title", courseList.get(i).title);
			obj.put("provider", courseList.get(i).provider);
			obj.put("subject", courseList.get(i).subject);
			obj.put("language", courseList.get(i).language);
//			obj.put("effortHours", courseList.get(i).effort_hours);
			
			//// session assign
			JSONObject Session = new JSONObject();
			Session.put("sessionCounts", courseList.get(i).sessionList.size());
			Session.put("sessionLength", courseList.get(i).session_length);
			
			JSONArray sessions = new JSONArray();
			for(int j=0; j<courseList.get(i).sessionList.size(); j++){
				JSONObject session = new JSONObject();
				
				Calendar c = Calendar.getInstance();
				c.setTime(courseList.get(i).sessionList.get(j));
				
				session.put("Year", c.get(Calendar.YEAR));
				session.put("Month", c.get(Calendar.MONTH));
				session.put("Day", c.get(Calendar.DATE));
								
				sessions.add(session);
			}
			Session.put("session", sessions);
			obj.put("Sessions", Session);
			
			//// Keywords assign
							
			JSONArray keywords = new JSONArray();
			for(int j=0; j<courseList.get(i).keywordsList.size(); j++){
				JSONObject keyword = new JSONObject();
				
				keyword.put("keyword-"+(j+1), courseList.get(i).keywordsList.get(j));
												
				keywords.add(keyword);
			}
			obj.put("Keywords", keywords);
	
			
			
			

			//// Time Series data assign
			JSONObject TimeSeries = new JSONObject();
			TimeSeries.put("totalReviewCounts", courseList.get(i).totalReview_count);
			TimeSeries.put("totalReviewValue", courseList.get(i).totalReview_value);
					
			JSONArray times = new JSONArray();
			for(int j=0; j<courseList.get(i).TimeData.size(); j++){
				JSONObject time = new JSONObject();
				time.put("time", courseList.get(i).TimeData.get(j).timeIndex);
				time.put("reviewCount", courseList.get(i).TimeData.get(j).reviewCount);
				// normalizing reviewValue between 0~5
				time.put("reviewValue", courseList.get(i).TimeData.get(j).reviewValue/courseList.get(i).TimeData.get(j).reviewCount);
				
				// needs to add other Time-varying independent variable
				// ; network centrality of courseNetwork(connected by reviewer) or keyworkNetwork
												
				times.add(time);
			}
			TimeSeries.put("Time", times);
			obj.put("TimeSeries", TimeSeries);
											
			course_list.add(obj);
	
		}
		root.put("Courses", course_list);
		
		try {
			FileWriter file = new FileWriter("./results/CoursesXTdata.json");
			file.write(root.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	// write reviewData as JSON format
	
	public void writeReviewToJson() {
		
		JSONObject root = new JSONObject();
		JSONArray review_list = new JSONArray();
			
//		for(int i=0; i<10; i++){
		for(int i=0; i<reviewList.size(); i++){
			
			JSONObject obj = new JSONObject();
			obj.put("reviewer_id", reviewList.get(i).reviewer_id);
			obj.put("reivew_value", reviewList.get(i).review_value);
			
			obj.put("course_url", reviewList.get(i).course_url);	// not valid, need to fix course_url raw data
			obj.put("course_title", reviewList.get(i).title);
			obj.put("mooc_platform", reviewList.get(i).provider);
			obj.put("reivew_platform", reviewList.get(i).platform);
			
			obj.put("time", reviewList.get(i).includedTime);
			
			Calendar c = Calendar.getInstance();
			c.setTime(reviewList.get(i).review_date);
			JSONObject reviewDate = new JSONObject();
			reviewDate.put("Year", c.get(Calendar.YEAR));
			reviewDate.put("Month", c.get(Calendar.MONTH));
			reviewDate.put("Day", c.get(Calendar.DATE));
			
			obj.put("review_date", reviewDate);
									
			review_list.add(obj);
	
		}
		root.put("Reviews", review_list);
		
		try {
			FileWriter file = new FileWriter("./results/reviewXTdata.json");
			file.write(root.toJSONString());
			file.flush();
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	
	
	////////////  print function //////////
	
	public void printTimeSeries(){
		System.out.println(timeFor_fixedEffect.TimeList.size());
		
		// Print every interval and the reviewCount and the reveiwValue 
		for(int i=0; i<timeFor_fixedEffect.ReviewCountList.size()-1; i++){
			
			System.out.println("T" +(i+1)+ "'s (From" + transFormat.format(timeFor_fixedEffect.TimeList.get(i)) + " to "
					+ transFormat.format(timeFor_fixedEffect.TimeList.get(i+1)) + ")"
					+ " reviewCounts : " + timeFor_fixedEffect.ReviewCountList.get(i)
					+ " / reviewValue : " + timeFor_fixedEffect.ReviewValueList.get(i));
		}	
		// Print total reviewCount and reveiwValue
		System.out.println("totalReviewCount - " + timeFor_fixedEffect.totalReviewCount);
		System.out.println("totalReviewValue - " + timeFor_fixedEffect.totalReviewValue);
		
		
	}
	
	public void printCourseListWithTime(){
		
		// Print every courseData with TimeSeries data 
		for(int i=0; i<courseList.size(); i++){
			
			String s = "title - " + courseList.get(i).title;
			
			s += "\n existing Time Series List";
			for(int j=0; j<courseList.get(i).TimeData.size(); j++){
				s += "\nT" + (courseList.get(i).TimeData.get(j).timeIndex + 1) + "'s - " 
						+ " reviewCounts : " + courseList.get(i).TimeData.get(j).reviewCount
						+ "/ reviewValue : " + courseList.get(i).TimeData.get(j).reviewValue;
				
			}
			s += "\n total ReviewCount : " + courseList.get(i).totalReview_count 
					+ "/ total ReviewValue : " + courseList.get(i).totalReview_value;
			
			System.out.println(s);
		
		}

		
	}
	
	
	
	
	
	// previous version function


	
	public void calculateInterval(){
		
		for(int i=0; i<courseList.size(); i++){
			courseList.get(i).reviewCount = new ArrayList<Integer>();
			courseList.get(i).reviewValue = new ArrayList<Float>();
			
			// session list �겕湲곕쭔�겮 reviewCount�� reviewValue list initialization
			for(int x =0; x<courseList.get(i).sessionList.size(); x++){
				courseList.get(i).reviewCount.add(0);
				courseList.get(i).reviewValue.add(0f);
			}
			
			for(int j=0; j<reviewList.size(); j++){
				
				// title and provider matched(	There are some duplicated title of courses .. so needed check)
				if(reviewList.get(j).provider.equals(courseList.get(i).provider) 
						&& reviewList.get(j).title.equals(courseList.get(i).title)){
					
					// �닚�닔 total reviewCount & reviewValue 怨꾩궛 �떆 肄붾뱶
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
				
				// interval 蹂� review count summation�떆 �븘�슂�븳 肄붾뱶. 
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

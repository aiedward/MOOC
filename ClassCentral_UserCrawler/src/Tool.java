import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;



public class Tool {
	
	boolean endData=false;
	int page = 1;
	int index = 0;
	ArrayList<Integer> pagelist = new ArrayList<Integer>();
	ArrayList<String> instructorList = new ArrayList<String>();
	
	Data courseData = new Data();
	
	
	
	WebDriver driver = new FirefoxDriver();
	int urlSize = 0;
	

	
	public ArrayList<String> getURL() throws IOException
	{
		
		
		final ArrayList<String> urlList=new ArrayList<String>();
		
		
		FileInputStream inputFile = new FileInputStream("./users_url.txt");
		
		// Construct BufferReader from inputStreamReader
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputFile));
			
		
		String line = null;
		
		// read url line by line 
		while ( (line = fileReader.readLine()) != null){
			urlList.add(line);
		}
		
		fileReader.close();
			
		
		urlSize = urlList.size();
		System.out.println("size of url - " + urlSize);
		
		return urlList; 
		
		
	}
	
	
	
		
	/*
	 * End of get All url function
	 */
	
	/*
	 * creatDomRoot
	 * 
	 */
	
	
	org.w3c.dom.Document newCreatedDocument = null;
	
	synchronized public org.w3c.dom.Document createDomRoot(){
		System.out.println("----------------Root create-----------------");

		try {
			newCreatedDocument = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		org.w3c.dom.Node root = newCreatedDocument.createElement("ROOT");
		newCreatedDocument.appendChild(root);
		return newCreatedDocument;
		
	}
	
	// To prevent occurring null pointer exception during transforming XML
	public static Text createTextNodeWithoutNull(Document doc, String str){
		Text textNode;
		if(str != null) textNode = doc.createTextNode(str);
		else textNode = doc.createTextNode("null");
		
		return textNode;
	}
	
	/*
	 * Crawl data.
	 * 
	 */
	
	public void crawlData(final String url, final Integer index) throws IOException{
		
		// open website
		
		driver.navigate().to(url);
				
		// sleep 10 seconds to wait until onload
		try{
			Thread.sleep(1000*6);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// newCreatedDocument is destination of XML.
		NodeList nodelist=newCreatedDocument.getElementsByTagName("ROOT");
		Node root=nodelist.item(0);
				
		
		Data data = new Data();
						
		data.url = url;
		
			
		////////////// User Info parsing //////////////
		WebElement user_info = driver.findElement(By.cssSelector("div.col-md-3.profile-info"));
		
		data.name = user_info.findElement(By.cssSelector("div.info-row.info-name"))
				.findElement(By.cssSelector("span.info-text")).getText();
		
		if(user_info.findElements(By.cssSelector("div.info-row.info-location")).size() > 0 ){
			data.location = user_info.findElement(By.cssSelector("div.info-row.info-location"))
					.findElement(By.cssSelector("span.info-text")).getText();
			
		}else{
			data.location = "null";
		}
		
		
		if(user_info.findElements(By.cssSelector("div.info-row.info-profession")).size() > 0 ){
			data.professional = user_info.findElement(By.cssSelector("div.info-row.info-profession"))
					.findElement(By.cssSelector("span.info-text")).getText();
			
		}else{
			data.professional = "null";
		}
		
		if(user_info.findElements(By.cssSelector("div.info-row.info-education")).size() > 0 ){
			data.education = user_info.findElement(By.cssSelector("div.info-row.info-education"))
					.findElement(By.cssSelector("span.info-text")).getText();
			
		}else{
			data.education = "null";
		}
		
		System.out.println("name- " + data.name);
		System.out.println("education- " + data.education);
		System.out.println("professional- " + data.professional);
		System.out.println("location- " + data.location);
		
		
		
		
		/////////////////////////////////////////////////
		////////////// course data parsing //////////////
			
		
		ArrayList <WebElement> status_list = new ArrayList <WebElement> 
			( driver.findElement(By.id("transcripts")).findElements(By.cssSelector("h2.large-thin-title")));
				
		
		ArrayList <WebElement> courses_list = new ArrayList <WebElement> 
			( driver.findElement(By.id("transcripts")).findElements(By.cssSelector("div.tab-pane-item.left-point")) );
		
		// if there is no course enrolled, break this function.
		if(! (courses_list.size() > 0) ){
			return;
		}
		
		System.out.println("type of enrollements - " + status_list.size());
		System.out.println("the number of courses - " + courses_list.size());
		
		
		data.courseList = new ArrayList<Course>();
				
		int pre_index;
		for(int i=0; i<status_list.size() ; i++){
			String[] temp = status_list.get(i).getText().split(" ");
			System.out.println(temp[temp.length-2]);
			
//			System.out.println(status_list.get(i).getText().split(" "));
			
			
			if(i==0){
				pre_index = 0;
			}else{
				String[] temp2 = status_list.get(i-1).getText().split(" ");
						
				pre_index =+ Integer.valueOf(temp2[temp2.length-2]);
			}
			
			int course_count;
			try{
				// NO COURSES FOUND일 경우
				course_count = Integer.valueOf(temp[temp.length-2]);
			}catch(Exception e){
				return;
			}
			
			System.out.println("course-count: " + course_count);
			System.out.println("pre-index: " + pre_index);
			
			for(int j=pre_index; j< pre_index+course_count; j++){
				Course temp_course = new Course();
				temp_course.status = status_list.get(i).getText();
				System.out.println(temp_course.status);
				temp_course.title = courses_list.get(j).findElement(By.cssSelector("span.item-title")).getText();
				System.out.println(temp_course.title);
				temp_course.url = courses_list.get(j).findElement(By.cssSelector("span.item-title"))
						.findElement(By.tagName("a")).getAttribute("href");
				System.out.println(temp_course.url);
				try{
					temp_course.provider = courses_list.get(j).findElement(By.cssSelector("span.item-info"))
							.findElement(By.cssSelector("a.initiativelinks.colored")).getText();
					System.out.println(temp_course.provider);
				}catch(Exception e){
					temp_course.provider = "null";
				}
				
				
				try{
					temp_course.school = courses_list.get(j).findElement(By.cssSelector("span.item-info"))
							.findElement(By.cssSelector("a.uni-name")).getText();
				}catch(Exception e){
					temp_course.school = "null";
				}
				
				System.out.println(temp_course.school);
				
				data.courseList.add(temp_course);
				
			}
			
		}
		
		//////////// interested courses /////////////////
		
		ArrayList <WebElement> interested_course_list = new ArrayList <WebElement> 
		( driver.findElement(By.id("interested")).findElements(By.cssSelector("div.tab-pane-item.left-point")));
		
		for( WebElement element : interested_course_list){
			Course temp_course = new Course();
			temp_course.title = element.findElement(By.cssSelector("span.item-title")).findElement(By.tagName("a")).getText();
			temp_course.url = element.findElement(By.cssSelector("span.item-title")).findElement(By.tagName("a")).getAttribute("href");
			
			try{
				temp_course.provider = element.findElement(By.cssSelector("a.initiativelinks.colored")).getText();
			}catch(Exception e){
				temp_course.provider = "null";
			}
			
			
			temp_course.status = "Interested";
			
			try{
				temp_course.school = element.findElement(By.cssSelector("a.uni-name")).getText();
			}catch(Exception e){
				temp_course.school = "null";
			}
			
			data.courseList.add(temp_course);
			
			
			
		}
		
	
		// making dom elements
		
		org.w3c.dom.Element UserInfo = newCreatedDocument.createElement("UserInfo");
						
		root.appendChild(UserInfo);
		{
			
			// user name
			org.w3c.dom.Element user_name = newCreatedDocument.createElement("name");
			user_name.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.name));
			UserInfo.appendChild(user_name);
			
			// user url
			org.w3c.dom.Element user_url = newCreatedDocument.createElement("user_url");
			user_url.appendChild(createTextNodeWithoutNull(newCreatedDocument, url));
			UserInfo.appendChild(user_url);
			
			// user education
			org.w3c.dom.Element user_edu = newCreatedDocument.createElement("education");
			user_edu.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.education));
			UserInfo.appendChild(user_edu);
			
			// user profession
			org.w3c.dom.Element user_profession = newCreatedDocument.createElement("profession");
			user_profession.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.professional));
			UserInfo.appendChild(user_profession);
			
			// user location
			org.w3c.dom.Element user_location = newCreatedDocument.createElement("location");
			user_location.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.location));
			UserInfo.appendChild(user_location);
						
			// enrolled course list
			org.w3c.dom.Element course_list = newCreatedDocument.createElement("enrolled_courses");
			for(int i=0; i<data.courseList.size(); i++){
				org.w3c.dom.Element course = newCreatedDocument.createElement("course");
				
				org.w3c.dom.Element status = newCreatedDocument.createElement("status");
				status.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.courseList.get(i).status));
				org.w3c.dom.Element provider = newCreatedDocument.createElement("provider");
				provider.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.courseList.get(i).provider));
				org.w3c.dom.Element school = newCreatedDocument.createElement("school");
				school.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.courseList.get(i).school));
				org.w3c.dom.Element title = newCreatedDocument.createElement("title");
				title.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.courseList.get(i).title));
				org.w3c.dom.Element course_url = newCreatedDocument.createElement("url");
				course_url.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.courseList.get(i).url));
				
				
				course.appendChild(status);
				course.appendChild(provider);
				course.appendChild(school);
				course.appendChild(title);
				course.appendChild(course_url);
				
				
				course_list.appendChild(course);
			}
			UserInfo.appendChild(course_list);
			
			// interested course list
		
								
		}

		
	
	}
	
	

	
}

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
	final WebDriver driver = new FirefoxDriver();
	int urlSize = 0;
	
	public ArrayList<String> getVideoURL() throws IOException{
		
		driver.navigate().to("https://www.edx.org/course");
		
		final ArrayList<String> urlList = new ArrayList<String>();
		boolean plag = false;
		int position = 1000;
//		int count = 0;
		while(!plag){
			
			((JavascriptExecutor)driver).executeScript("scroll(0,"+position + ")");
						
			if(! (driver.findElements(By.cssSelector("[class='loading']")).size() > 0)){
				plag = true;
			}
			
					
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			position += 1000;
			
			
//			if(count==10){
//				break;
//			}
//			count++;	
			
		}
		
		ArrayList <WebElement> url_list = new ArrayList<WebElement>(driver.findElements(By.cssSelector("div.course-card")));
			
		int size_of_url = url_list.size();
		
		for(int i=0; i<size_of_url; i++){
			
			String url = url_list.get(i).findElement(By.tagName("a")).getAttribute("href").toString();
			
			// xseries 강의들은 제외하고 list에 담는다.
			if(url.contains("xseries")){
				continue;
			}else{
				urlList.add(url);
				System.out.println(url);
			}
			
			
		}
			
			
		
		/*
		FileInputStream inputFile = new FileInputStream("./courses_url_edX.txt");
		
		// Construct BufferReader from inputStreamReader
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputFile));
		
		
		
		String line = null;
		
		// read url line by line 
		while ( (line = fileReader.readLine()) != null){
			urlList.add(line);
		}
		
		fileReader.close();
		*/
		
		
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
		
		data.id = driver.findElement(By.id("course-info-page")).getAttribute("data-course-id").toString();
		System.out.println(data.id);

	
		ArrayList <WebElement> metadata = new ArrayList <WebElement> (driver
				.findElements(By.tagName("meta")));
		
		System.out.println("meta data size - " + metadata.size());
		
		
		
		for(int i=0; i<metadata.size(); i++){
			WebElement element = metadata.get(i);
			
			if(element.getAttribute("property") == null){
				continue;
			}
								
			if(element.getAttribute("property").equals("og:title")){
				//course title
				data.title = element.getAttribute("content").toString();
				System.out.println("title - " +data.title);
				
			}else if(element.getAttribute("property").equals("og:description")){
				//course intro
				data.intro = element.getAttribute("content").toString();
				System.out.println("intro - " +data.intro);
				
			}else if(element.getAttribute("property").equals("article:published_time")){
				// course published time
				data.date = element.getAttribute("content").toString();
				System.out.println("published date - " +data.date);
				
				// break loop when assign three values
				break;
			}
			
		}

		// Parsing course-Summary
		//System.out.println(driver.findElement(By.id("course-summary-area")).findElements(By.cssSelector("li")).get(1).getText());
		
		ArrayList <WebElement> Summary = new ArrayList <WebElement> (driver.findElement(By.id("course-summary-area"))
				.findElements(By.cssSelector("li")) );
		
		for(int i=0; i<Summary.size(); i++){
			// Sometimes attributes are not assigned with 'data-field'
			if(Summary.get(i).getAttribute("data-field") == null){
				continue;
			}
			
			if(Summary.get(i).getAttribute("data-field").equals("school")){
				data.school = Summary.get(i).getText();
				System.out.println("school - " + data.school);
			}else if(Summary.get(i).getAttribute("data-field").equals("subject")){
				data.subject = Summary.get(i).getText();
				System.out.println("subject - " + data.subject);
			}else if(Summary.get(i).getAttribute("data-field").equals("price")){
				data.price = Summary.get(i).getText();
				System.out.println("Price - " + data.price);
				
			}else if(Summary.get(i).getAttribute("data-field").equals("level")){
				data.level = Summary.get(i).getText();
				System.out.println("level - " + data.level); 
			}else if(Summary.get(i).getAttribute("data-field").equals("language")){
			
				try{
					data.language = Summary.get(i).getText();
				}catch(Exception e){
					data.language = "no language";
				}
				// break loop when assign three values
				break;
			}
			
		}
		
		// Parsing course-about
		
		ArrayList <WebElement> course_Detail = new ArrayList <WebElement> (driver.findElement(By.id("course-about-area"))
				.findElements(By.cssSelector("div.content-grouping")) );
		
		// Before parsing course_about, we need to span the element of description.
		// interact(click) with "see more" and validate whether the element is exist.
		List<WebElement> more_content = course_Detail.get(0).findElements(By.linkText("See more"));
		
		if(more_content.size()>0){
			more_content.get(0).click();
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// after interacting with the element, parsing description and 'what you'll learn
		try{
			data.description = course_Detail.get(0).findElement(By.cssSelector("div.see-more-content")).getText().toString();
			System.out.println("description - " + data.description);
			
		}catch(Exception e){
			data.description = "no description";
		}
		
		try{

			data.learnAbout = course_Detail.get(1).findElement(By.cssSelector("ul")).getText().toString();
			System.out.println("What you'll learn - " + data.learnAbout);
			
		}catch(Exception e){
			data.learnAbout = "no info";
		}
		
		// Parsing instructor page
		ArrayList <WebElement> instructors = new ArrayList <WebElement> 
			(driver.findElements(By.cssSelector("li.list-instructor__item")));
		
		for(WebElement instructor : instructors){
			instructorList.add(instructor.findElement(By.tagName("a")).getAttribute("href"));
			
		}
		

		// making dom elements
		
		org.w3c.dom.Element course_info = newCreatedDocument.createElement("CourseInfo");
						
		root.appendChild(course_info);
		{
			
			// index
			org.w3c.dom.Element course_id = newCreatedDocument
					.createElement("course_id");
			course_id.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.id));
			course_info.appendChild(course_id);
						
			// title
			org.w3c.dom.Element course_title = newCreatedDocument.createElement("title");
			course_title.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.title));
			course_info.appendChild(course_title);
			
			// published_date
			org.w3c.dom.Element published_date = newCreatedDocument.createElement("published_date");
			published_date.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.date));
			course_info.appendChild(published_date);

			// course intro
			org.w3c.dom.Element course_intro = newCreatedDocument.createElement("course_intro");
			course_intro.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.intro));
			course_info.appendChild(course_intro);

			// subject
			org.w3c.dom.Element course_subject = newCreatedDocument.createElement("subject");
			course_subject.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.subject));
			course_info.appendChild(course_subject);
			
			// price
			org.w3c.dom.Element course_price = newCreatedDocument.createElement("price");
			course_price.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.price));
			course_info.appendChild(course_price);
			
			// level
			org.w3c.dom.Element course_level = newCreatedDocument.createElement("level");
			course_level.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.level));
			course_info.appendChild(course_level);

			// school
			org.w3c.dom.Element course_school = newCreatedDocument.createElement("school");
			course_school.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.school));
			course_info.appendChild(course_school);
			
			// language
			org.w3c.dom.Element course_language = newCreatedDocument.createElement("language");
			course_language.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.language));
			course_info.appendChild(course_language);
			
						
			// description
			org.w3c.dom.Element course_description = newCreatedDocument.createElement("description");
			course_description.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.description));
			course_info.appendChild(course_description);
			
			// learn about
			org.w3c.dom.Element learn_about = newCreatedDocument.createElement("learn_about");
			learn_about.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.learnAbout));
			course_info.appendChild(learn_about);
			
			// url
			org.w3c.dom.Element course_url = newCreatedDocument.createElement("url");
			course_url.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.url));
			course_info.appendChild(course_url);
			
								
		}

		if(index == urlSize -1){
			System.out.println("Last page");
			driver.close();
			OutputStream output = new FileOutputStream("instructor_url.txt");
			
			for(String instructor : instructorList){
				System.out.println(instructor);
				try{
					output.write((instructor+'\n').getBytes());
				}catch(Exception e){
					
				}
								
			}
			output.close();
			
			
		}
	
	}
	
	

	
}

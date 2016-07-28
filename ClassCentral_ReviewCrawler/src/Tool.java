import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
	
	public ArrayList<String> getVideoURL(String targetURL) throws IOException{
		
		driver.navigate().to(targetURL);
		driver.manage().window().maximize();
		
		
		final ArrayList<String> urlList = new ArrayList<String>();

		
		boolean plag = false;
				
		float position = 2000;
		// page span to load all data 
		while(!plag){
			
			((JavascriptExecutor)driver).executeScript("scroll(0,"+position + ")");
			
			WebElement loadCourses = driver.findElement(By.id("show-more-courses"));
			
			
			if( loadCourses.getCssValue("display").equals("none")) {
				plag = true;
				break;
				
			}else{
								
				System.out.println(loadCourses.getCssValue("display") );
				loadCourses.click();
				position = position + position * 0.5f;
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
						
					
			

		}
		
		ArrayList <WebElement> url_list = new ArrayList<WebElement>(driver.findElements(By.className("course-name")));
		
		for(int i=0; i<url_list.size(); i++){
			if( url_list.get(i).getAttribute("class").equals("course-name") ){
				System.out.println(url_list.get(i).getAttribute("href"));
				urlList.add(url_list.get(i).getAttribute("href"));
			}
			
			
		}
						
		
		System.out.println("size of url - " + urlList.size());
		
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
		
		WebElement info_element;
		
		try{
			info_element = driver.findElement(By.className("course-info"));
		}catch(Exception e){
			// error occurred in web-site, then break this function
			return;
			
		}
		
		
		data.title = info_element.findElement(By.className("course-title")).getText();
//		data.description = info_element.findElement(By.className("course-desc")).getText();
		
		System.out.println(data.title);
//		System.out.println(data.description);
		
		data.reviewCount = driver.findElement(By.cssSelector("span.review-count")).getText();
		data.review_sum = driver.findElement(By.cssSelector("span.average-rating.course-rating")).getAttribute("data-score").toString();
		
		if(Integer.valueOf(data.reviewCount) == 0){
			// review count가 0일 경우 함수 빠져나감.
			return;
			
			
		}else{
			// review count가 한개 이상일 경우.
			
			ArrayList <WebElement> reviews;
			
			try{
				reviews = new ArrayList <WebElement> (driver.findElements(By.cssSelector("div.single-review")));
			}catch (Exception e){
				return; 
			}
			
			for(int i=0; i<reviews.size(); i++){
				// review date
				data.review_date = 	reviews.get(i).findElement(By.cssSelector("a.review-date"))
					.findElement(By.tagName("meta")).getAttribute("content").toString();
				
				try{
					// reviewer id and url
					data.reviewer_url = reviews.get(i).findElement(By.cssSelector("div.crop-circle.crop-circle--bordered"))
					.findElement(By.tagName("a")).getAttribute("href").toString();
												

				}catch (Exception e){
					// assign id value as anonymous
					data.reviewer_url = "null";
									
				}
				
				data.reviewer_id = reviews.get(i).findElement(By.cssSelector("div.review-title.title-with-image"))
						.findElement(By.cssSelector("span.author")).getText();
				
				// review value
				data.reviewer_rating_value = reviews.get(i).findElement(By.cssSelector("div.small-star-rating"))
				.findElement(By.tagName("meta")).getAttribute("content").toString();
				
				// review content
				if(reviews.get(i).findElements(By.cssSelector("div.review-full")).size() > 0 ){
					data.review_contents = reviews.get(i).findElement(By.cssSelector("div.review-full")).getText().toString();
				}else{
					data.review_contents = "none";
				}
				
				System.out.println(data.review_contents);
				
				
				
				// making dom elements
				org.w3c.dom.Element course_info = newCreatedDocument.createElement("CourseReview");
								
				root.appendChild(course_info);
				{
					
											
					// title
					org.w3c.dom.Element course_title = newCreatedDocument.createElement("title");
					course_title.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.title));
					course_info.appendChild(course_title);
					
					// review count
					org.w3c.dom.Element review_count = newCreatedDocument.createElement("review_count");
					review_count.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.reviewCount));
					course_info.appendChild(review_count);
										
					// review value Summation
					org.w3c.dom.Element review_sum = newCreatedDocument.createElement("review_sum");
					review_sum.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.review_sum));
					course_info.appendChild(review_sum);
					
					// user_id
					org.w3c.dom.Element user_id = newCreatedDocument.createElement("reviewer_id");
					user_id.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.reviewer_id));
					course_info.appendChild(user_id);

								
					// reviewer value
					org.w3c.dom.Element reviewer_rating_value = newCreatedDocument.createElement("single_rating_value");
					reviewer_rating_value.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.reviewer_rating_value));
					course_info.appendChild(reviewer_rating_value);
					
					// review date
					org.w3c.dom.Element review_date = newCreatedDocument.createElement("review_date");
					review_date.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.review_date));
					course_info.appendChild(review_date);
					
					// reviewer url 
					org.w3c.dom.Element reviewer_url = newCreatedDocument.createElement("reviewer_url");
					reviewer_url.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.reviewer_url));
					course_info.appendChild(reviewer_url);
					
					
					// status(completed, etc)
					
					
					// review content
					org.w3c.dom.Element review_contents = newCreatedDocument.createElement("review_contents");
					review_contents.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.review_contents));
					course_info.appendChild(review_contents);
					
					
					// url
					// this will be an identical value
					org.w3c.dom.Element course_url = newCreatedDocument.createElement("url");
					course_url.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.url));
					course_info.appendChild(course_url);
				
										
				}
				
				
				
				
				
			}
			
			
		}
				
		

	
	
	}
	
	

	
}

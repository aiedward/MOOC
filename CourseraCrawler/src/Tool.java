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
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;



public class Tool {
	
	boolean endData=false;
	int page = 1;
	int index = 0;
	ArrayList<Integer> pagelist = new ArrayList<Integer>();
	
	
	Data courseData = new Data();
//	final WebDriver driver = new FirefoxDriver();
	int urlSize = 0;
	
	WebDriver driver;

	public void initDriver() throws InterruptedException{
		
		System.setProperty(
				"webdriver.chrome.driver",
				"D:/chromedriver_win32/chromedriver.exe"
		);
		
		driver = new ChromeDriver();
		driver.get("http://www.google.com");
		driver.manage().window().maximize();
		
		
		driver.navigate().to("https://www.coursera.org");
		Actions actionObject = new Actions(driver);
		Thread.sleep(15000);
		
		// currently the coursera platform block the login-access through selenuim web-driver 
		// so, sleep the program and I manually login to the platform
		
		// *********************** PLEASE LOGIN MANUANLLY!!! *************************
	
		
		/*System.out.println(driver.findElement(By.cssSelector("ul.c-navbar-list.bt3-nav.bt3-navbar-nav.bt3-navbar-right")).getLocation());
		
		WebElement login = driver.findElement(By.cssSelector("ul.c-navbar-list.bt3-nav.bt3-navbar-nav.bt3-navbar-right"));
		((JavascriptExecutor) driver).executeScript("window.scrollTo(" + login.getLocation().x + "," + (login.getLocation().y + 20) + ")");
		System.out.println(login.getLocation());
		actionObject.click(login).perform();
		Thread.sleep(2000);
		
		WebElement id = driver.findElement(By.cssSelector("input#user-modal-email.placeholder"));
		
		Thread.sleep(500);
			
						
		// move to the location of the element to input	
		((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + (id.getLocation().y) + ")");
		actionObject.sendKeys("totoro1865@naver.com").perform();
				
//		id.sendKeys("totoro1865@naver.com");
		Thread.sleep(1000);
		actionObject.sendKeys(Keys.ENTER).perform();
		Thread.sleep(3000);
//		((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + (pw.getLocation().y) + ")");
		actionObject.sendKeys("heungseok2").perform();
		Thread.sleep(1000);
//		actionObject.sendKeys(Keys.ENTER).perform();
//		pw.sendKeys("heungseok2");
//		pw.sendKeys(Keys.ENTER);
		Thread.sleep(3000);
		
	*/	
	}
	
	
	public ArrayList<String> getVideoURL(String targetSite) throws IOException, InterruptedException{
		
		
		
		
		
		driver.navigate().to(targetSite);
		
		
		
		
		final ArrayList<String> urlList = new ArrayList<String>();
		
		Thread.sleep(5000);
				
		ArrayList <WebElement> url_list = new ArrayList<WebElement>(driver.findElements(By.cssSelector("a.c-directory-link")));
			
		int size_of_url = url_list.size();
		
		for(int i=0; i<size_of_url; i++){
			
			String url = url_list.get(i).getAttribute("href").toString();
			System.out.println(url);
			urlList.add(url);
			
		}
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
	public void crawlData(final String url, int index) throws IOException, InterruptedException{
			
		// open website
		driver.navigate().to(url);
				
		// sleep 5 seconds to wait until onload
		
		try{
			Thread.sleep(1000*5);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		// newCreatedDocument is destination of XML.
		NodeList nodelist=newCreatedDocument.getElementsByTagName("ROOT");
		Node root=nodelist.item(0);
				
		
		Data data = new Data();
		
		data.id = Integer.toString(index);
		
		// assign course url 
		data.url = url;
		
		// title
		List<WebElement> tempList = new ArrayList<WebElement>();
		tempList = driver.findElements(By.cssSelector("div.title.display-3-text"));
		// if the size of the list parsed from title web elements is zero, then the course is not supported anymore in the platform(Coursera) 
		if(tempList.size() == 0) 
			return;
		else
			data.title = tempList.get(0).getText().replaceAll("&amp;", "");
				
		
		// subject
		WebElement tempElement = driver.findElement(By.cssSelector("div.rc-BannerBreadcrumbs.caption-text"));
		tempList = tempElement.findElements(By.cssSelector("span.item"));
		data.subject = tempList.get(tempList.size()-1).getText();
		
		// institution
		data.school = driver.findElement(By.cssSelector("div.headline-1-text.creator-names")).getText().replaceAll("Created by:  ", "");
		
		// platform
		data.provider = "Coursera";
		
		// instructors(Name, job, img)
		tempList = driver.findElements(By.cssSelector("div.rc-InstructorInfo"));
		data.instructors = new ArrayList<String>();
		data.instructors_bio = new ArrayList<String>();
		data.instructors_img = new ArrayList<String>();
		
		
		List<WebElement> temp = new ArrayList<WebElement>();
		
		for(int i=0; i<tempList.size(); i++){
			String name = tempList.get(i).findElement(By.cssSelector("span.body-1-text")).getText();
//			name = name.replaceAll("Taught by:", "");
			String bio;
			temp = tempList.get(i).findElements(By.cssSelector("div.instructor-bio.caption-text.color-accent-brown"));
			if(temp.size() == 0)
				bio = "null";
			else
				bio = temp.get(0).getText();
			String img = tempList.get(i).
					findElement(By.cssSelector("div.instructor-photo.bt3-col-xs-4.bt3-col-sm-2")).
					findElement(By.tagName("img")).getAttribute("src");
			
			data.instructors.add(name);
			data.instructors_img.add(img);
			data.instructors_bio.add(bio);
			
		}
		
		// course length
		tempList = driver.findElements(By.cssSelector("div.week"));
		data.length = Integer.toString(tempList.size());
		
		
		WebElement BasicInfo = driver.findElement(By.cssSelector("div.rc-BasicInfo"));
		List<WebElement> infoTable = new ArrayList<WebElement>();
		infoTable = BasicInfo.findElements(By.tagName("tr"));
		for(int i=0; i<infoTable.size(); i++){
			tempList = infoTable.get(i).findElements(By.tagName("td"));
			
			String key = tempList.get(0).getText().toLowerCase();
			String value = tempList.get(1).getText();
			if(key.equals("level")){
				data.level = value;
			}else if(key.equals("commitment")){
				data.effort = value;
			}else if(key.equals("language")){
				data.language = value;
			}else if(key.equals("basic info")){
				data.basic_info = value;
			}
			
		}

		// course description
		data.description = driver.findElement(By.cssSelector("div.about-section-wrapper")).getText();
		
		// course price for certification parsing
		driver.findElement(By.cssSelector("button.course-enroll-button-wrapper.comfy.primary")).click();
		Thread.sleep(1500);
		
//		List <WebElement> price_option = driver.findElements(By.cssSelector("div.bt3-radio.choice-radio-container"));
		List <WebElement> price_option = driver.findElements(By.cssSelector("h4.primary-description"));
		
		int length_of_prices = price_option.size();
		switch(length_of_prices){
			case 0:
				data.price = null;
				break;
			case 1:
				data.price = price_option.get(0).getText();
				break;
			case 2:
				data.price = price_option.get(0).getText();
				break;
			case 3:
				data.price = price_option.get(1).getText();
				break;
			default:
				data.price = null;
				
		}
		System.out.println(data.price);
		
		
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
			
			// length
			org.w3c.dom.Element course_length = newCreatedDocument.createElement("course_length");
			course_length.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.length));
			course_info.appendChild(course_length);
			
			// effort
			org.w3c.dom.Element course_effort = newCreatedDocument.createElement("effort");
			course_effort.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.effort));
			course_info.appendChild(course_effort);

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
			
			
			// url
			org.w3c.dom.Element course_url = newCreatedDocument.createElement("url");
			course_url.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.url));
			course_info.appendChild(course_url);
			
			// instructors
			org.w3c.dom.Element instructors = newCreatedDocument.createElement("instructors");
			for(int i=0; i<data.instructors.size(); i++){
				org.w3c.dom.Element instructor = newCreatedDocument.createElement("instructor");
				
				org.w3c.dom.Element name = newCreatedDocument.createElement("name");
				name.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.instructors.get(i)));
				org.w3c.dom.Element bio = newCreatedDocument.createElement("bio");
				bio.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.instructors_bio.get(i)));
				org.w3c.dom.Element img = newCreatedDocument.createElement("img");
				img.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.instructors_img.get(i)));
				instructor.appendChild(name);
				instructor.appendChild(bio);
				instructor.appendChild(img);
				
				
				instructors.appendChild(instructor);
			}
			course_info.appendChild(instructors);
			
			// basic info
			org.w3c.dom.Element basic_info = newCreatedDocument.createElement("basic_info");
			basic_info.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.basic_info));
			course_info.appendChild(basic_info);
			
			// description
			org.w3c.dom.Element course_description = newCreatedDocument.createElement("description");
			course_description.appendChild(createTextNodeWithoutNull(newCreatedDocument, data.description));
			course_info.appendChild(course_description);
	
								
		}

		if(index == urlSize -1){
			System.out.println("Last page");
			driver.close();

//			OutputStream output = new FileOutputStream("instructor_url.txt");
//			
//			for(String instructor : instructorList){
//				System.out.println(instructor);
//				try{
//					output.write((instructor+'\n').getBytes());
//				}catch(Exception e){
//					
//				}
//								
//			}
//			output.close();
			
			
		}
	
	}
	
	

	
}

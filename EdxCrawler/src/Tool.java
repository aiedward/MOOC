import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class Tool {
	
	boolean endData=false;
	int page = 1;
	int index = 0;
	ArrayList<Integer> pagelist = new ArrayList<Integer>(); 
	Data courseData = new Data();
	
	public ArrayList<String> getVideoURL() throws IOException{
		
		FileInputStream inputFile = new FileInputStream("./courses_url_edX.txt");
		
		// Construct BufferReader from inputStreamReader
		BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputFile));
		
		final ArrayList<String> urlList = new ArrayList<String>();
		
		String line = null;
		
		// read url line by line 
		while ( (line = fileReader.readLine()) != null){
			urlList.add(line);
		}
		
		fileReader.close();
		
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
	
	/*
	 * Crawl data.
	 * 
	 */
	
	public void crawlData(final String url, final Integer index){
		
		//new Thread(new Runnable() {
			
			//@Override
			//public void run() {
				// TODO Auto-generated method stub
				Connection connection;
				// connection time to 5 seconds 
				connection = Jsoup.connect(url).timeout(1000*5);
				
				
				// newCreatedDocument is destination of XML.
				NodeList nodelist=newCreatedDocument.getElementsByTagName("ROOT");
				Node root=nodelist.item(0);
				
				Document document = null;
				
				Data data = new Data();
				data.keyword = new ArrayList<String>();
				data.tag = new ArrayList<String>();


				try {
					document = connection.get();
				} catch (Exception e) {
				}
				
				data.url = url;
				
				Elements metadata = document.getElementsByTag("meta");
				
				for( Element element : metadata){
					
					if(element.attr("property").equals("og:title")){
						// course title
						data.title = element.attr("content");
						System.out.println("title - " +data.title);
					}else if(element.attr("property").equals("og:description")){
						// course intro
						data.intro = element.attr("content");
						System.out.println("intro - " +data.intro);
					}else if(element.attr("property").equals("article:published_time")){
						// course published time
						data.date = element.attr("content");
						System.out.println("date - " +data.date);
					}
								
				}
							
				Element head = document.getElementById("course-info-page");
				// course-id
				data.id = head.attr("data-course-id");
				System.out.println(data.id);
				System.out.println(head);
				Element course_summary = document.getElementById("content");
				System.out.println(course_summary);
				//Element course_summary = head.getElementById("course-summary-area");
				//System.out.println("summary- " + course_summary.get(0).text() );
				
//				
//				for(Element element : course_summary){
//					
//					String attr = element.attr("data-field").toString();
//					System.out.println("course_summary- " + attr);
//					
//					if( attr.equals("school")){
//						data.school = element.select("a").text();
//						System.out.println("course_school- " + data.school);
//						
//						
//					}else if( attr.equals("subject")){
//						data.subject = element.select("a").text();
//						System.out.println("course_subject- " + data.subject);
//						
//					}else if (attr.equals("level")){
//						data.level = element.select("a").text();
//						System.out.println("course_level - " + data.level);
//					}
//					
//				}
				
				Elements course_about = document.getElementById("course-about-area").select("div.content-grouping");
				
				data.course_index = course_about.get(0).select("span.ct-weight-stars").get(0).attr("data-course");
				
				System.out.println("course_index - " + data.course_index);
				
				data.description = course_about.get(0).select("div.see-more-content").text();
				
				System.out.println("course description - " + data.description);
				
				System.out.println("What you will learn - " + course_about.get(1).children().get(1).text());
				

				// making dom elements
				org.w3c.dom.Element course_info = newCreatedDocument.createElement("Course");
								
				root.appendChild(course_info);
				{
					
					// index
					org.w3c.dom.Element course_id = newCreatedDocument
							.createElement("course_id");
					course_id.appendChild(newCreatedDocument.createTextNode(data.id));
					course_info.appendChild(course_id);
					
					// title
					org.w3c.dom.Element course_title = newCreatedDocument.createElement("title");
					course_title.appendChild(newCreatedDocument.createTextNode(data.title));
					course_info.appendChild(course_title);
					
					// provider
					org.w3c.dom.Element course_provider = newCreatedDocument.createElement("provider");
					course_provider.appendChild(newCreatedDocument.createTextNode(data.provider));
					course_info.appendChild(course_provider);
					
					// rating
					org.w3c.dom.Element course_rating = newCreatedDocument.createElement("rating");
					course_rating.appendChild(newCreatedDocument.createTextNode(data.ratingValue));
					course_info.appendChild(course_rating);
					
					// instructor 
					org.w3c.dom.Element course_instructor = newCreatedDocument.createElement("instructor");
					course_instructor.appendChild(newCreatedDocument.createTextNode(data.instructors));
					course_info.appendChild(course_instructor);
					
					// school
					org.w3c.dom.Element course_school = newCreatedDocument.createElement("school");
					course_school.appendChild(newCreatedDocument.createTextNode(data.school));
					course_info.appendChild(course_school);
					
					
					// description
					org.w3c.dom.Element course_description = newCreatedDocument.createElement("description");
					course_description.appendChild(newCreatedDocument.createTextNode(data.description));
					course_info.appendChild(course_description);
					
					// url
					org.w3c.dom.Element course_url = newCreatedDocument.createElement("url");
					course_url.appendChild(newCreatedDocument.createTextNode(data.url));
					course_info.appendChild(course_url);
					
										
				}
			}
		//}).start();
	//}

	
}

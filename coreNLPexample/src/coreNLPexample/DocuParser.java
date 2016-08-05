package coreNLPexample;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.opencsv.CSVReader;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class DocuParser {
	
	ArrayList<HashMap<String, Integer>> DocumentTermMap;
	private CSVReader mReader;
	public ArrayList<Course> courseList;
	public Properties props;
	StanfordCoreNLP pipeline;
	
	private HashMap<Integer, String> term_map = null;
	
	
	DocuParser(){
		ArrayList<HashMap<String, Integer>> DocumentTermMap = null;
		courseList = null;
		mReader = null;
		
	    props = new Properties();
	    
	    
	    /* The "annotators" property key tells the pipeline which entities should be initiated with our
	    pipeline object, See http://nlp.stanford.edu/software/corenlp.shtml for a complete reference 
	    to the "annotators" values you can set here and what they will contribute to the analyzing process  */
	    props.put("annotators", "tokenize, ssplit, pos, lemma, parse, ner, dcoref");
	    pipeline = new StanfordCoreNLP(props);
		
		
	}
	
	
	
	public void dataLoad() throws IOException{
		
		final String inputFile = "./data/Integrated_Course_withDescption_20160712"; 
		
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
//			String provier = nextLine[1];
//			String subject = nextLine[2];
//			String school = nextLine[3];
//			String language = nextLine[4];
			String url = nextLine[5];
			String description = nextLine[6];
						
				
			Course temp = new Course();
			
			temp.title = title;
			temp.url = url;
			temp.description = description;
			
			courseList.add(temp);
			
			temp = null;
										
		}
		
		
		for(int i=0; i<courseList.size(); i++){
			courseList.get(i).TermFreq = new HashMap<String, Integer>();
			System.out.println(courseList.get(i).title);
//			System.out.println(courseList.get(i).description);
						
		}
		System.out.println("size of courseList - " + courseList.size());
		
		mReader.close();		
		
	}
	
	public void loadDocumetTermMatrixFile() throws IOException{
		
		final String inputFile = "./data/dtm_tfidf_MOOC";
		
		try{
			mReader = new CSVReader(new FileReader(inputFile + ".csv"));
		}catch (FileNotFoundException e){
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		System.out.println(inputFile + " file open succeed");
		String[] nextLine = null;
		
		boolean tag = true;
		
		term_map = new HashMap<Integer, String>();
	
		courseList = new ArrayList<Course>();
		
				
		
		while( (nextLine = mReader.readNext()) != null ){
			
			if(tag){
				
				for(int i=1; i < nextLine.length; i++){
					//System.out.println(i + " - " + nextLine[i]);
					term_map.put(i, nextLine[i].replaceAll("\n", ""));
				}
				tag = false;
				continue;
				
			}
			
			Course temp_course = new Course();
			temp_course.termList = new ArrayList<Term>();
			temp_course.url = new String();
			
						
			// clean temp course object
			temp_course.clean();
			// assign course title to temp data
			temp_course.url = nextLine[0];
							
			// adding keyword to temp data
			for(int i=1; i < nextLine.length; i++){
				if(Float.valueOf(nextLine[i])!= 0){
					temp_course.termList.add(new Term( term_map.get(i), Float.valueOf(nextLine[i])));
					
				}
			}
			
			// Descendant sort by TF-IDF value of each term
			Collections.sort(temp_course.termList, new Comparator<Term>() {
				public int compare(Term term2, Term term1)
			    {
					return term1.termValue.compareTo(term2.termValue);
		        }
				
			});
			
			temp_course.keywordList = new ArrayList<String>();
			// add ONLY 8 keywords to keywordList
			for(int i=0; i<8; i++){
								
				if(temp_course.termList.isEmpty())
					System.out.println(temp_course.url);
				else
//					System.out.println(temp_course.termList.get(i).term);
					continue;
			}
			/*
			// add course - terms data to courseList.
			//System.out.println(temp_course.title);
			for(int i=0; i<courseList.size(); i++){
				String temp_url = courseList.get(i).url;
				if(temp_url.equals(temp_course.url)){
					courseList.get(i).keywordList = new ArrayList<String>();
					courseList.get(i).keywordList = temp_course.keywordList;
				}
				
			}
			*/
			
			
//			courseList.add(temp_course);
			
						
		}
		
		
		
		
		for(int i=0; i < courseList.size(); i++){
			System.out.println("title - " + courseList.get(i).title );
			System.out.println("term size\t" + courseList.get(i).termList.size());
//			
//			try{
//				for(int j=0; j< 30; j++){
//					System.out.println(courseList.get(i).Terms.get(j).term + " - " + courseList.get(i).Terms.get(j).termValue);
//				}
//			}catch(Exception e){
//				System.out.println("less than top30");
//			}
//			
		}

		mReader.close();
		
		
		
		
	}
	
	public void writeConnectionData(String filename){
		String name = "./"+filename + ".txt";
		
		try{
	    	////////////////////////////////////////////////////////////////
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "UTF-8"));
	    	String s = "url%Title%Keyword%Values";
	    	      
	    	out.write(s);
	    	out.newLine();
	    	
	    	for(int i=0; i<courseList.size(); i++){
	    		s = courseList.get(i).url + "%" + courseList.get(i).title + "%";
	    		// only file write with Top8 keywords
	    		for(int j=0; j<8; j++){
		    			String s_1 = courseList.get(i).termList.get(j).term + "%" + courseList.get(i).termList.get(j).termValue.toString();
		    			
		    			out.write(s+s_1);
		    			out.newLine();	    			
		    		}
	    		
	    	}
	    	out.close();
	    	   
    	}catch (IOException e){
    		System.err.println(e); 
    	    System.exit(1);
    	}
		
	}
	
	
	
	
	public void descriptionParserWithCoreNLP(){
		
		for(int i=0; i<10; i++){	
//		for(int i=0; i<courseList.size(); i++){
			
			this.getLemmatization(i, courseList.get(i).description);
		}
		
	}
	
	
	
	public void makeDocumentTermMatrix(){
		for(int i=0; i<courseList.size(); i++){
			System.out.println(courseList.get(i).title);
			courseList.get(i).TermFreq.forEach((k, v)->{
				
				System.out.println("Term : " + k + " Freq : " + v);
				
			});
		}
	}
	

	
	private void getLemmatization(int index, String description) {
		
		String text = description;

	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	
	    // run all Annotators on this text
	    pipeline.annotate(document);

	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	    for(CoreMap sentence: sentences) {
	    	// traversing the words in the current sentence
	    	// a CoreLabel is a CoreMap with additional token-specific methods
	    	for (CoreLabel token: sentence.get(TokensAnnotation.class)) {

	    		// Extracting the Lemma
	    		String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
	    		
	    		if(lemma.length() > 2){
	    			if(!courseList.get(index).TermFreq.containsKey(lemma)){
		    			courseList.get(index).TermFreq.put(lemma, 1);
		    		}else{
		    			int increasedPreviousValue = courseList.get(index).TermFreq.get(lemma)+1;
		    			courseList.get(index).TermFreq.replace(lemma, increasedPreviousValue);
		    		}
	    		}
	    		
	   	  
	    	}

	    }
	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * This function is for a reference of CoreNLP (don't modify)
	 */

	public void ORIGIN_descriptionParserWithCoreNLP(){
		    
	    
	    /* Next we can add customized annotation and trained data 
	    I will elaborate on training data in my next blog chapter, for now you can comment those lines */
		//    pipeline.addAnnotator(new RegexNERAnnotator("some RegexNer structured file"));
		//    pipeline.addAnnotator(new TokensRegexAnnotator("some tokenRegex structured file"));
	    
		// read some text from the file..
		//    File inputFile = new File("src/test/resources/sample-content.txt");
		//    String text = Files.toString(inputFile, Charset.forName("UTF-8"));
	    String text = courseList.get(0).description;

	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);
	
	    // run all Annotators on this text
	    // Finally we use the pipeline to annotate the document we created
	    pipeline.annotate(document);

    
	    /* now that we have the document (wrapping our inputText) annotated we can extract the
	    annotated sentences from it, Annotated sentences are represent by a CoreMap Object */
	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

    
	    /* Next we can go over the annotated sentences and extract the annotated words,
	    Using the CoreLabel Object */
	    for(CoreMap sentence: sentences) {
	    	System.out.println(sentence);
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	    	  
	    	  // Using the CoreLabel object we can start retrieving NLP annotation data
	    	  
	    	  // Extracting the Text Entity; this is the text of the token 
	    	  String word = token.getString(TextAnnotation.class);
	    	     	      	       
	    	  // Extracting Part of Speech; this is the POS tag of the token
	    	  String pos = token.get(PartOfSpeechAnnotation.class);
	    	  // this is the NER label of the token
	    	  String ner = token.get(NamedEntityTagAnnotation.class);
	    	  
	    	  // Extracting the Lemma
	    	  String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
	    	  
	    	  
	          /* There are more annotation that are available for extracting 
	          (depending on which "annotators" you initiated with the pipeline properties", 
	          examine the token, sentence and document objects to find any relevant annotation 
	          you might need */
	        
	    	  System.out.println("word: " + word + ";POS: " + pos + ";NER:" + ner + ";LEMMA:" + lemma);
	      }

	      // this is the parse tree of the current sentence
	      Tree tree = sentence.get(TreeAnnotation.class);
	      System.out.println("parse tree:\n" + tree);
	
	      // this is the Stanford dependency graph of the current sentence
	      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	      System.out.println("dependency graph:\n" + dependencies);
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    Map<Integer, CorefChain> graph = 
	        document.get(CorefChainAnnotation.class);
   
	}
	
	

}

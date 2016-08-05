package coreNLPexample;
import java.util.ArrayList;
import java.util.HashMap;

public class Course {
	
	String url;		// courseURL
	String title;
	String description;
	
	HashMap<String, Integer> TermFreq;
	
	ArrayList<Term> termList;
	ArrayList<String> keywordList;
	
	public void clean(){
		this.termList.clear();
		this.url = null;
		this.title = null;
		this.description = null;
	}

		
	
}

class Term{
	String term;
	Float termValue;
	Term(String mTerm, float mTermValue){
		this.term = mTerm;
		this.termValue = mTermValue;
	}
}



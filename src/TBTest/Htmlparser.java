package TBTest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Htmlparser {
	Document doc;
	public Htmlparser(String html){
		doc = Jsoup.parse(html); 
	}
	public String getTitle(){
		return doc.title();
	}
	public String getText(){
		return doc.text();
	}
}

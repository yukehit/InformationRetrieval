import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.edu.hit.mitlab.informationretrieval.*;

/**
 * @author yk
 * @version 1.0
 */

public class example {
	public static List<String> getWords(String str, Analyzer analyzer) {
		List<String> result = new ArrayList<String>();
		TokenStream stream = null;
		try {
			stream = analyzer.tokenStream("content", new StringReader(str));
			CharTermAttribute attr = stream
					.addAttribute(CharTermAttribute.class);
			stream.reset();
			while (stream.incrementToken()) {
				result.add(attr.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static void create() throws Exception {
		Index ir = new Index();
		File file = new File("D:\\code\\InformationRetrieval\\luceneIndex");
		for (File f : file.listFiles())
			f.delete();
		ir.initIndexWriter("D:\\code\\InformationRetrieval\\luceneIndex",
				Index.CHINESE);
		ir.addIndexs(new readDocs()
				.read("D:\\code\\InformationRetrieval\\luceneData"));
		System.out.println("total docs num = " + ir.getTotalDocsNum());
		ir.closeIndexWriter();
		
	}

	public static void deleteTest() throws Exception {
		System.out.println("--------------delete test start-------------------");
		System.out.println("--------------delete keyword ลฃถู-------------------");
		
		create();
		String keyWord = "ลฃถู";
		Search search = new Search();
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		search.search("contents",keyWord,  100);
		Index ir = new Index();
		File file = new File("D:\\code\\InformationRetrieval\\luceneIndex");
		ir.initIndexWriter("D:\\code\\InformationRetrieval\\luceneIndex",
				Index.CHINESE);
		ir.deleteIndex("contents", keyWord);
		
		ir.closeIndexWriter();
		
		Index ir2 = new Index();
		ir2.initIndexWriter("D:\\code\\InformationRetrieval\\luceneIndex",
				Index.CHINESE);
		System.out.println("total docs  = " + ir2.getTotalDocsNum());
		System.out.println("--------------after delete-------------------");
		
		
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		search.search("contents",keyWord,  100);
		
		
		System.out.println("--------------delete test end-------------------");
	}
	public static void updateTest() throws Exception{
		System.out.println("--------------update test start-------------------");
		create();
		String keyWord = "ลฃถู";
		Search search = new Search();
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		search.search("contents",keyWord,  100);
		Index ir = new Index();
		File file = new File("D:\\code\\InformationRetrieval\\luceneIndex");
		ir.initIndexWriter("D:\\code\\InformationRetrieval\\luceneIndex",
				Index.CHINESE);
		ir.updateIndex("contents", keyWord, new Document());
		ir.closeIndexWriter();
		
		System.out.println("--------------after update-------------------");
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		search.search("path", "D:\\code\\InformationRetrieval\\luceneData\\1.txt" , 100);
		search.search("contents",keyWord,  100);
		search.termQuery("path","code", 100);
		System.out.println("total docs  = " + ir.getTotalDocsNum());
		ir.closeIndexWriter();
		System.out.println("--------------delete test end-------------------");
	}
	public static void multiPhraseTest() throws IOException{
		 Search search = new Search();
		 search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		 System.out.println("----------------------term query test ------------------");
		 search.termQuery("contents", "ลฃถู", 100);
		 System.out.println();
	}
	public static void main(String[] args) throws Exception {
//		deleteTest();
		create();
		 Search search = new Search();
		 search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		 search.search("contents","ลฃถู", 100);
		 

	}
}

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;







import javax.sql.rowset.serial.SerialArray;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import cn.edu.hit.mitlab.informationretrieval.*;
import cn.edu.hit.mitlab.informationretrieval.BooleanQueryClause.Occur;
import cn.edu.hit.mitlab.informationretrieval.Search.SortFieldType;

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

	public static void printDocInfo(Search search, TopDocs td) throws IOException{
		for(ScoreDoc i: td.scoreDocs){
			Document doc = search.getDocument(i.doc);
			System.out.println(doc.getField("path"));
		}
	}
	public static void queryTest() throws IOException, ParseException{
		Search search = new Search();
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		printDocInfo(search, search.query("contents", "Å£¶Ù", 100));
		
		System.out.println("--------------------termQuery-----------------");
		search.termQuery("contents", "Å£¶Ù", 100);
		
		System.out.println("--------------------booleanQuery-----------------");
		search.booleanQuery(100, new BooleanQueryClause[]{new BooleanQueryClause("contents", "Å£¶Ù", Occur.MUST),
				new BooleanQueryClause("contents", "À®°È", Occur.MUST_NOT)});
		
		System.out.println("--------------------fuzzyQuery-----------------");
		search.fuzzyQuery("contents", "Å£¶Ù", 100,1);
		
		System.out.println("--------------------regexpQuery-----------------");
		search.regexpQuery("contents", "Å£.*", 100);
		
		System.out.println("--------------------prefixQuery-----------------");
		search.prefixQuery("contents", "Å£", 100);
		
		System.out.println("--------------------phraseQuery-----------------");
		search.phraseQuery("contents", new String[]{"Å£¶Ù", "À®°È","Ôç³¿"}, 100, 100);
		
		System.out.println("--------------------multiphraseQuery-----------------");
		search.multiPhraseQuery("contents", new String[]{"Å£¶Ù","À®°È","Ôç³¿"}, 100,100);
		
//		
		System.out.println("--------------------termQuery using addTermRangeFilter-----------------");
		search.addTermRangeFilter("time", "0day", "9day", true, true);
		search.termQuery("contents", "Å£¶Ù", 100);
		
		System.out.println("--------------------termQuery using addNumericRangeFilter-----------------");
		search.clearFilter();
		search.addNumericRangeFilter("num", 2, 10, true, true);
//		search.clearFilter();
		search.termQuery("contents", "Å£¶Ù", 100);
//		search.addTermRangeFilter("", lowerTerm, upperTerm, includeLower, includeUpper);
		
		System.out.println("--------------------termQuery using addNumericRangeFilter-----------------");
		search.clearFilter();
		search.regexpQuery("contents", "µØ.*", 100);
		search.addQueryFilter();
		search.termQuery("contents", "Å£¶Ù", 100);
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
		System.out.println("--------------delete keyword Å£¶Ù-------------------");
		
		create();
		String keyWord = "Å£¶Ù";
		Search search = new Search();
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		search.query("contents",keyWord,  100);
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
		search.query("contents",keyWord,  100);
		
		System.out.println("--------------delete test end-------------------");
	}
	
	public static void updateTest() throws Exception{
		System.out.println("--------------update test start-------------------");
		create();
		String keyWord = "Å£¶Ù";
		Search search = new Search();
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		search.query("contents",keyWord,  100);
		Index ir = new Index();
		File file = new File("D:\\code\\InformationRetrieval\\luceneIndex");
		ir.initIndexWriter("D:\\code\\InformationRetrieval\\luceneIndex",
				Index.CHINESE);
		ir.updateIndex("contents", keyWord, new Document());
		ir.closeIndexWriter();
		
		System.out.println("--------------after update-------------------");
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
		search.query("path", "D:\\code\\InformationRetrieval\\luceneData\\1.txt" , 100);
		search.query("contents",keyWord,  100);
		search.termQuery("path","code", 100);
		System.out.println("total docs  = " + ir.getTotalDocsNum());
		ir.closeIndexWriter();
		System.out.println("--------------delete test end-------------------");
	}
	public static void sortTest() throws IOException{
		Search search = new Search();
		search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
//		search.setSort("path", SortFieldType.STRING, false);
//		search.setSort("path", SortFieldType.DOC, false);
//		search.setSort("num", SortFieldType.LONG, false);
//		search.setSort("path", SortFieldType.SCORE, true);
		printDocInfo(search, search.termQuery("contents", "Ò»¸ö", 100));
		
	}
	public static void main(String[] args) throws Exception {
		sortTest();
//		deleteTest();
//		create();
//		 Search search = new Search();
//		 search.loadIndex("D:\\code\\InformationRetrieval\\luceneIndex");
//		 search.search("contents","Å£¶Ù", 100);
//		 queryTest();
//		Index ir = new Index();
//		ir.initIndexWriter("D:\\code\\InformationRetrieval\\luceneIndex",
//				Index.CHINESE);
//		ir.deleteAll();
//		ir.commit();
//		ir.closeIndexWriter();
		
	}
}

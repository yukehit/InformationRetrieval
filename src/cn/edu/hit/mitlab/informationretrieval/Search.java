package cn.edu.hit.mitlab.informationretrieval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiPhraseQuery;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.RegexpQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeFilter;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

import cn.edu.hit.mitlab.informationretrieval.BooleanQueryClause.Occur;


/**
 * @time 2015.4
 * @author yk
 * @version 1.0
 */
public class Search {
	private DirectoryReader[] indexReader = null;
	private IndexSearcher indexSearcher = null;
	private Filter filter = null;
	private Sort sort = null;
	private Query query = null;
	private MultiReader multiReader = null;
	
	public enum SortFieldType{
		DOUBLE, STRING, DOC, LONG, SCORE;
	}

	public int getMaxDoc(){
		return multiReader.maxDoc();
	}
	
	public int getNumDeletedDocs(){
		return multiReader.numDeletedDocs();
	}
	
	public int getNumDocs(){
		return multiReader.numDocs();
	}
	
	/**
	 * @param field
	 * @param type
	 * @param reverse true for ASC, false for DESC 
	 * @return true if set successfully, else false
	 */
	public boolean setSort(String field, SortFieldType type, boolean reverse){
		SortField sf = null;
		if(type == SortFieldType.DOC){
			sf = new SortField(field, SortField.Type.DOC, reverse);
		}else if(type == SortFieldType.DOUBLE){
			sf = new SortField(field, SortField.Type.DOUBLE, reverse);
		}else if(type == SortFieldType.LONG){
			sf = new SortField(field, SortField.Type.LONG, reverse);
		}else if(type == SortFieldType.SCORE){
			sf = new SortField(field, SortField.Type.SCORE, reverse);
		}else if(type == SortFieldType.STRING){
			sf = new SortField(field, SortField.Type.STRING, reverse);
		}
		sort = new Sort(sf);
		return true;
	}

	/**
	 * @param fields
	 * @param types
	 * @param reverses reverse true for ASC, false for DESC 
	 * @return true if set successfully, else false
	 */
	public boolean setSort(String[] fields, SortFieldType[] types, boolean[] reverses){
		if((fields.length == types.length) && (fields.length == reverses.length)){
			SortField[] sfs = new SortField[fields.length];
			for (int i = 0; i < fields.length; i++) {
				if(types[i] == SortFieldType.DOC){
					sfs[i] = new SortField(fields[i], SortField.Type.DOC, reverses[i]);
				}else if(types[i] == SortFieldType.DOUBLE){
					sfs[i] = new SortField(fields[i], SortField.Type.DOUBLE, reverses[i]);
				}else if(types[i] == SortFieldType.LONG){
					sfs[i] = new SortField(fields[i], SortField.Type.LONG, reverses[i]);
				}else if(types[i] == SortFieldType.SCORE){
					sfs[i] = new SortField(fields[i], SortField.Type.SCORE, reverses[i]);
				}else if(types[i] == SortFieldType.STRING){
					sfs[i] = new SortField(fields[i], SortField.Type.STRING, reverses[i]);
				}
			}
			sort = new Sort(sfs);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * remove sort
	 */
	public void clearSort(){
		sort = null;
	}
	
	/**
	 * @param field
	 * @param min
	 * @param max 
	 * @param minInclusive include min?
	 * @param maxInclusive include max?
	 */
	public void addNumericRangeFilter(String field, double min, double max, boolean minInclusive, boolean maxInclusive){
		filter = NumericRangeFilter.newDoubleRange(field, min, max, minInclusive, maxInclusive);
	}
	
	/**
	 * @param field
	 * @param min
	 * @param max
	 * @param minInclusive include min?
	 * @param maxInclusive include max?
	 */
	public void addNumericRangeFilter(String field, long min, long max, boolean minInclusive, boolean maxInclusive){
		filter = NumericRangeFilter.newLongRange(field, min, max, minInclusive, maxInclusive);
	}
	
	/**
	 * @param field
	 * @param lowerTerm
	 * @param upperTerm
	 * @param includeLower
	 * @param includeUpper
	 */
	public void addTermRangeFilter(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper){
		filter = TermRangeFilter.newStringRange(field, lowerTerm, upperTerm, includeLower, includeUpper);
	}
	
	/**
	 * add QueryFilter, search from the result of the last query
	 */
	public void addQueryFilter(){
		filter = new QueryWrapperFilter(query);
	}
	
	public void clearFilter(){
		filter = null;
	}
	
	public void loadIndex(String... indexDirPaths) throws IOException {
		if((indexReader != null) && (indexReader.length > 0)){
			for(DirectoryReader dr: indexReader)
				dr.close();
		}
		if(multiReader != null)
			multiReader.close();
		
		indexReader = new DirectoryReader[indexDirPaths.length];
		
		for(int i = 0; i < indexDirPaths.length; i++){
			File indexDir = new File(indexDirPaths[i]);
			if (!indexDir.exists()) {
				System.out
						.println("Directory " + indexDirPaths[i] + " does not exist!");
			}
			Directory directory = MMapDirectory.open(indexDir);
			indexReader[i] = DirectoryReader.open(directory);
		}
		
		multiReader = new MultiReader(indexReader);
		indexSearcher = new IndexSearcher(multiReader);
//		indexSearcher.collectionStatistics(arg0) 
	}
	protected Analyzer getIKAnalyzer() {
		Configuration cfg = new Configuration() {

			@Override
			public boolean useSmart() {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public void setUseSmart(boolean arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public String getQuantifierDicionary() {
				// TODO Auto-generated method stub
				return "org/wltea/analyzer/dic/quantifier.dic";
			}

			@Override
			public String getMainDictionary() {
				// TODO Auto-generated method stub
				return "org/wltea/analyzer/dic/main2012.dic";
			}

			@Override
			public List<String> getExtStopWordDictionarys() {
				// TODO Auto-generated method stub
				ArrayList<String> dics = new ArrayList<String>();
				dics.add("org/wltea/analyzer/dic/chinese_stopword.dic");
				return dics;
			}

			@Override
			public List<String> getExtDictionarys() {
				// TODO Auto-generated method stub
				return null;
			}
		};
		Dictionary.initial(cfg);
		return new IKAnalyzer(true);
	}
	/**
	 * @param field
	 * @param keyWord
	 * @param n top n docs
	 * @throws IOException
	 */
	public TopDocs termQuery(String field, String keyWord, int n)
			throws IOException {
		if (indexSearcher == null) {
			System.err.println("lucene index hasn't be initialized!");
			return null;
		}
		query = new TermQuery(new Term(field, keyWord));
		return doQuery(n);
	}
	
	public TopDocs multiFieldQuery(String[] fields, String keyWord, int n) throws IOException, ParseException{
		if (indexSearcher == null) {
			System.err.println("lucene index hasn't be initialized!");
			return null;
		}
		MultiFieldQueryParser mfqp = new MultiFieldQueryParser(fields, getIKAnalyzer());
		query = mfqp.parse(keyWord);
		return doQuery(n);
	}
	
	/**
	 * @refer refer to http://blog.csdn.net/wzhg0508/article/details/11107647
	 * @param n
	 * @param bqcs
	 * @return
	 * @throws IOException
	 */
	public TopDocs booleanQuery(int n, BooleanQueryClause... bqcs) throws IOException{
		BooleanQuery bq = new BooleanQuery();
		for(BooleanQueryClause bqc: bqcs){
			TermQuery tq = new TermQuery(new Term(bqc.getField(), bqc.getKeyWord()));
			if(bqc.getOccur() == Occur.MUST){
				bq.add(tq, org.apache.lucene.search.BooleanClause.Occur.MUST);
			}else if(bqc.getOccur() == Occur.MUST_NOT){
				bq.add(tq, org.apache.lucene.search.BooleanClause.Occur.MUST_NOT);
			}else if(bqc.getOccur() == Occur.SHOULD){
				bq.add(tq, org.apache.lucene.search.BooleanClause.Occur.SHOULD);
			}
		}
		query = bq;
		return doQuery(n);
	}
	
	public TopDocs prefixQuery(String field, String keyWord, int n) throws IOException{
		PrefixQuery prefixquery=new PrefixQuery(new Term(field, keyWord));
		query = prefixquery;
		return doQuery(n);
	}
	
	
	/**
	 * @param terms
	 * @param n 
	 * @param slop Sets the number of other words permitted between words in query phrase.
	 * @return 
	 * @throws IOException
	 */
	public TopDocs phraseQuery(String field, String[] keyWords, int n, int slop)
			throws IOException {
		if (indexSearcher == null) {
			System.err.println("lucene index hasn't be initialized!");
			return null;
		}
		PhraseQuery pq = new PhraseQuery();
		for(String s: keyWords){
			pq.add(new Term(field, s));
		}
		pq.setSlop(slop);
		
		query = pq;
		
		return doQuery(n);
	}
	
	
	
	/**
	 * @param field
	 * @param keyWords first keyword is the prefix
	 * @param n
	 * @return
	 * @throws IOException
	 */
	public TopDocs multiPhraseQuery(String field, String[] keyWords, int n, int slop)
			throws IOException {
		if (indexSearcher == null) {
			System.err.println("lucene index hasn't be initialized!");
			return null;
		}
		MultiPhraseQuery mpq = new MultiPhraseQuery();
		mpq.add(new Term(field, keyWords[0]));
		
		Term[] terms = new Term[keyWords.length-1];
		for(int i = 1; i < keyWords.length; i++){
			terms[i-1] = new Term(field, keyWords[i]);
		}
		
		mpq.add(terms);
		mpq.setSlop(slop);
		
		query = mpq;
		return doQuery(n);
	}
	
	public TopDocs regexpQuery(String field, String regexp, int n)
			throws IOException {
		if (indexSearcher == null) {
			System.err.println("lucene index hasn't be initialized!");
			return null;
		}
		RegexpQuery rq = new RegexpQuery(new Term(field, regexp));
		query = rq;
		
		return doQuery(n);
	}

	/**
	 * reate a new FuzzyQuery that will match terms with an edit distance of at
	 * most maxEdits to term. If a prefixLength > 0 is specified, a common
	 * prefix of that length is also required.
	 * 
	 * @param field
	 * @param keyWord
	 * @param n
	 * @return
	 * @throws IOException
	 */
	public TopDocs fuzzyQuery(String field, String keyWord, int n, int maxEdits)
			throws IOException {
		if (indexSearcher == null) {
			System.err.println("lucene index hasn't be initialized!");
			return null;
		}
		
		FuzzyQuery fq =  new FuzzyQuery(new Term(field, keyWord), maxEdits);
		
		query = fq;
		return doQuery(n);
	}

	/**
	 * @param keyWord
	 * @throws IOException
	 * @throws ParseException 
	 */
	public TopDocs query(String fieldName, String keyWord, int n) throws IOException, ParseException {
		if (indexSearcher == null) {
			System.err.println("lucene index hasn't be initialized!");
			return null;
		}
		QueryParser parse = new QueryParser(fieldName, getIKAnalyzer());
//		Term term = new Term("contents", keyWord.toLowerCase());
//		TermQuery luceneQuery = new TermQuery(term);
		// Hits hits = searcher.search(luceneQuery);
		Query luceneQuery = parse.parse(keyWord);
		query = luceneQuery;
		return doQuery(n);
//		TopScoreDocCollector res = TopScoreDocCollector.create(n,true);
//		for(int i=0;i<scoreDocs.length;i++){  
//            int docid=scoreDocs[i].doc;  
//            Document document=indexSearcher.doc(docid);  
//            System.out.println("============文件【"+(i+1)+"】=========");  
//            String content=document.get("contents");  
//              
//            //高亮展示 SimpleHTMLFormatter //  
//            SimpleHTMLFormatter  formatter=new SimpleHTMLFormatter("<font color='red'>", "</font>");  
//            Highlighter highlighter=new Highlighter(formatter, new QueryScorer(luceneQuery));  
//            highlighter.setTextFragmenter(new SimpleFragmenter(content.length()));   
//              
//            if(!"".equals(content)){  
//                TokenStream tokenstream=new IKAnalyzer().tokenStream(content, new StringReader(content));  
//                String highLightText;
//				try {
//					highLightText = highlighter.getBestFragment(tokenstream,content);
//					System.out.println("高亮显示第 " + (i + 1) + " 条检索结果如下所示：");    
//	                System.out.println(highLightText);   
//	                /*End:结束关键字高亮*/  
//	                System.out.println("文件内容:"+content);                  
//	                System.out.println("匹配相关度："+scoreDocs[i].score);  
//				} catch (InvalidTokenOffsetsException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}    
//                
//            }  
//        }  
    }
	
	private TopDocs doQuery(int n) throws IOException{
		TopDocs topDocs;
		if(sort != null){
			topDocs = indexSearcher.search(query,filter, n, sort);
		}else{
			topDocs = indexSearcher.search(query,filter, n);
		}
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//		if (scoreDocs == null || scoreDocs.length == 0) {
//			System.out.println("not found");
//		}
//		System.out.println(topDocs.getMaxScore());
//		for (int i = 0; i < scoreDocs.length; i++) {
//			Document document = indexSearcher.doc(scoreDocs[i].doc);
//			System.out.println("File: " + document.get("path") + " " + scoreDocs[i].score);
//		}
		return topDocs;
	}
	
	/**
	 * @param docNum docNum 
	 * @return return the document docNum
	 * @throws IOException
	 */
	public cn.edu.hit.mitlab.informationretrieval.Document getDocument(int docNum) throws IOException{
		cn.edu.hit.mitlab.informationretrieval.Document doc = new cn.edu.hit.mitlab.informationretrieval.Document();
		Document document = indexSearcher.doc(docNum);
		for(IndexableField indexfield: document){
			doc.fields.put(indexfield.name(), document.get(indexfield.name()));
		}

		return doc;
	}
}

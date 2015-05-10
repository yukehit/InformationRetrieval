package cn.edu.hit.mitlab.informationretrieval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.dic.Dictionary;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * @author yk
 * @version 1.0
 */
public class Index {
	private IndexWriter indexWriter = null;
	public static final int CHINESE = 1;
	public static final int ENGLISH = 2;
	Log log;

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
	
	public int getTotalDocsNum(){
		return indexWriter.numDocs();
	}
	/**
	 * @param indexDirPath
	 * @throws IOException
	 */
	public void initIndexWriter(String indexDirPath, int langage)
			throws IOException {
		File indexDir = new File(indexDirPath);
		log = LogFactory.getLog(this.getClass());
		if (!indexDir.exists())
			indexDir.mkdirs();
		Analyzer luceneAnalyzer = null;
		if (langage == ENGLISH)
			luceneAnalyzer = new StandardAnalyzer();
		else if (langage == CHINESE) {
			luceneAnalyzer = getIKAnalyzer();
		} else {
			System.err.println("language not supporteded!");
			return;
		}
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_4_10_4, luceneAnalyzer);
		indexWriter = new IndexWriter(FSDirectory.open(indexDir),
				indexWriterConfig);
	}

	/**
	 * @throws IOException
	 */
	public void closeIndexWriter() throws IOException {
		indexWriter.close();
	}

	public void addIndex(
			cn.edu.hit.mitlab.informationretrieval.Document docements)
			throws Exception {
		if (indexWriter == null) {
			System.err.println("the indexWriter has not be initialized");
		}
		Document document = new Document();
		for (Entry<String, String> e : docements.fields.entrySet()) {
			document.add(new TextField(e.getKey(), e.getValue(), Store.YES));
		}
		log.info("add 1 document");
		indexWriter.addDocument(document);
		indexWriter.commit();
	}

	public void addIndexs(
			ArrayList<cn.edu.hit.mitlab.informationretrieval.Document> documents)
			throws Exception {
		if (indexWriter == null) {
			System.err.println("the indexWriter has not be initialized");
		}

		for (cn.edu.hit.mitlab.informationretrieval.Document df : documents) {
			Document document = new Document();

			for (Entry<String, String> e : df.fields.entrySet()) {
				document.add(new TextField(e.getKey(), e.getValue(), Store.YES));
			}

			indexWriter.addDocument(document);
		}
		log.info("add " + documents.size() + " documents");
		indexWriter.commit();
	}

	public void deleteIndex(String fieldName, String keyWord) throws Exception {
		indexWriter.deleteDocuments(new Term(fieldName, keyWord));
		log.info("Delete keyWord" + keyWord + " in field " + fieldName);
	}

	public void deleteAll() throws IOException{
		indexWriter.deleteAll();
		log.info("Delete ALL");
	}
	
	public void commit() throws IOException{
		indexWriter.commit();
	}
	public void updateIndex(String fieldName, String keyWord,
			cn.edu.hit.mitlab.informationretrieval.Document df)
			throws Exception {
		Document doc = new Document();
		for (Entry<String, String> e : df.fields.entrySet()) {
			doc.add(new TextField(e.getKey(), e.getValue(), Store.YES));
		}
		indexWriter.updateDocument(new Term(fieldName, keyWord), doc);
	}
}

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MultiThreadSearchTest {
  public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
    //ÿ���̶߳���5������Ŀ¼�в�ѯ����������5���̵߳Ĳ�ѯ�����һ��
    //multiThreadAndMultiReaderSearch();
	  IndexReader reader = IndexReader.open(FSDirectory.open(new File("luceneIndex")));
    MultiReader mr = new MultiReader(reader);
    IndexSearcher indexSearcher = new IndexSearcher(mr);
    TermQuery tq = new TermQuery(new Term("contents", "����"));
    TopDocs topDocs = indexSearcher.search(tq, 100);
	
	ScoreDoc[] scoreDocs = topDocs.scoreDocs;
	if (scoreDocs == null || scoreDocs.length == 0) {
		System.out.println("not found");
	}
	for (int i = 0; i < scoreDocs.length; i++) {
		Document document = indexSearcher.doc(scoreDocs[i].doc);
		System.out.println("File: " + document.get("contents"));
	}
    //������Ŀ¼��ѯ(�Ѷ������Ŀ¼����һ������Ŀ¼)
//	/ multiThreadAndMultiReaderSearch();
  }
  
  /**
   * ������Ŀ¼��ѯ
   * @throws InterruptedException
   * @throws ExecutionException
   * @throws IOException
   */
//  public static void multiReaderSearch()  throws InterruptedException, ExecutionException, IOException {
//    Directory directory1 = FSDirectory.open("C:/lucenedir1");
//  
////    Directory directory2 = openFSDirectory("C:/lucenedir2");
////    Directory directory3 = LuceneUtils.openFSDirectory("C:/lucenedir3");
////    Directory directory4 = LuceneUtils.openFSDirectory("C:/lucenedir4");
////    Directory directory5 = LuceneUtils.openFSDirectory("C:/lucenedir5");
////    IndexReader reader1 = DirectoryReader.open(directory1);
////    IndexReader reader2 = DirectoryReader.open(directory2);
////    IndexReader reader3 = DirectoryReader.open(directory3);
////    IndexReader reader4 = DirectoryReader.open(directory4);
////    IndexReader reader5 = DirectoryReader.open(directory5);
//    MultiReader multiReader = new MultiReader(reader1,reader2,reader3,reader4,reader5);
//    
//    IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher(multiReader);
//    Query query = new TermQuery(new Term("contents","volatile"));
//    List<Document> list = LuceneUtils.query(indexSearcher, query);
//    if(null == list || list.size() <= 0) {
//      System.out.println("No results.");
//      return;
//    }
//    for(Document doc : list) {
//      String path = doc.get("path");
//      //String content = doc.get("contents");
//      System.out.println("path:" + path);
//      //System.out.println("contents:" + content);
//    }
//  }
  
  /**
   * ������Ŀ¼�Ҷ��̲߳�ѯ���첽�ռ���ѯ���
   * @throws InterruptedException
   * @throws ExecutionException
   * @throws IOException
   */
  public static void multiThreadAndMultiReaderSearch()  throws InterruptedException, ExecutionException, IOException {
    int count = 1;
    String[] indexDirPaths = new String[]{"luceneIndex","E:\\sougoucorpus\\luceneIndex\\index2"
			,"E:\\ssougoucorpus\\luceneIndex\\index3","E:\\sougoucorpus\\luceneIndex\\index4"
			,"E:\\sougoucorpus\\luceneIndex\\index5","E:\\sougoucorpus\\luceneIndex\\index6"
			,"E:\\sougoucorpus\\luceneIndex\\index7","E:\\sougoucorpus\\luceneIndex\\index8"
			,"E:\\sougoucorpus\\luceneIndex\\index9","E:\\sougoucorpus\\luceneIndex\\index10"};
    ExecutorService pool = Executors.newFixedThreadPool(count);
    Directory dirs[] = new Directory[count];
    IndexReader readers[] = new IndexReader[count];
    for(int i = 0; i < count; i++){
    	dirs[i] = FSDirectory.open(new File(indexDirPaths[i]));
    	readers[i] = IndexReader.open(dirs[i]);
    }
    MultiReader multiReader = new MultiReader(readers[0]);
//    MultiReader multiReader = new MultiReader(readers[0],readers[1],readers[2],readers[3],readers[4],readers[5]
//    		,readers[6],readers[7],readers[8],readers[9]);
    
    final IndexSearcher indexSearcher = new IndexSearcher(multiReader, pool);
    final Query query = new TermQuery(new Term("CONTENT","����"));
    List<Future<List<Document>>> futures = new ArrayList<Future<List<Document>>>(count);
    for (int i = 0; i < count; i++) {
      futures.add(pool.submit(new Callable<List<Document>>() {
        public List<Document> call() throws Exception {
          return (List<Document>) indexSearcher.search(query, 1000000);
        }
      }));
    }
    System.out.println("1");
    int t = 0;
    //ͨ��Future�첽��ȡ�߳�ִ�к󷵻صĽ��
    for (Future<List<Document>> future : futures) {
      List<Document> list = future.get();
      if(null == list || list.size() <= 0) {
        t++;
        continue;
      }
      System.out.println(list.size()+ "found");
      System.out.println("");
    }
    //�ͷ��̳߳���Դ
    pool.shutdown();
    
    if(t == count) {
      System.out.println("No results.");
    }else{
    	System.out.println("get");
    }
  }
}
package TBTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import cn.edu.hit.mitlab.informationretrieval.Search;

public class TBSearchTest {
	static String encoding="UTF8";
    static File file=new File("TBSearchTest/main2012.dic");
    static BufferedReader bufferedReader; 
    static int warmUpTimes = 100;
    static int testTimes = 500;
    
    static ArrayList<String> keyWords = new ArrayList<String>();
    
    public static void getRandomKeyWords() throws IOException{
    	int count = 0;
    	while(count < warmUpTimes+testTimes){
    		 Random random = new Random();
    		 int s = random.nextInt(100000);
    		 String line;
    		 if((line = readLineN(s)) != null){
    			 keyWords.add(line);
    			 count ++;
    		 }
    	}
    }
    public static void searchTest() throws IOException{
    	Search search = new Search();
		String[] indexDirPaths = new String[]{"E:\\sougoucorpus\\luceneIndex\\index1","E:\\sougoucorpus\\luceneIndex\\index2"
					,"E:\\sougoucorpus\\luceneIndex\\index3","E:\\sougoucorpus\\luceneIndex\\index4"
					,"E:\\sougoucorpus\\luceneIndex\\index5","E:\\sougoucorpus\\luceneIndex\\index6"
					,"E:\\sougoucorpus\\luceneIndex\\index7","E:\\sougoucorpus\\luceneIndex\\index8"
					,"E:\\sougoucorpus\\luceneIndex\\index9","E:\\sougoucorpus\\luceneIndex\\index10"};
		search.loadIndex(indexDirPaths);
		
		getRandomKeyWords();
		long totalQueryTime = 0;
		int hitKeyWords = 0;
		System.out.println("Start warming up with " + warmUpTimes + " keyWords");
		for(int i = 0; i < warmUpTimes; i++){
			search.termQuery("CONTENTS", keyWords.get(i), 10000000);
		}
		
		System.out.println("Start searching with " + testTimes + " keyWords for test");
		for (int i = 0; i < testTimes; i++) {
			long startMili = System.currentTimeMillis();
			int hit = search.termQuery("CONTENTS", keyWords.get(i+warmUpTimes), 10000000).totalHits;
			long endMili = System.currentTimeMillis();
			if(hit > 0){
				totalQueryTime += endMili - startMili;
				hitKeyWords ++;
				System.out.println("keyword:" + keyWords.get(i+warmUpTimes) + 
						" " + hit + " found");
			}
		}
		
		System.out.println("Total keywords with hits is " + hitKeyWords);
		System.out.println("average query time is : " + totalQueryTime/hitKeyWords + "ms");
		
    }
    public static String readLineN(int n) throws IOException{
    	String line = null;
    	InputStreamReader read = new InputStreamReader(
		        new FileInputStream(file),encoding);
    	bufferedReader = new BufferedReader(read);
    	for(int i = 0; i < n; i++){
    		line = bufferedReader.readLine();
    		if(line == null)
    			break;
    	}
    	bufferedReader.close();
    	read.close();
    	return line;
    }
	public static void main(String[] args) throws IOException {
		System.out.println(file.isHidden());
//		searchTest();
		getRandomKeyWords();
		System.out.println(keyWords);
	}
}

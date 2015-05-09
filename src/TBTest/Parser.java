package TBTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.hit.mitlab.informationretrieval.Document;



public class Parser {
	// 鍖归厤琛ㄨ揪寮�
	private String docPatternString = "(?s)<DOC>(.*?)</DOC>";
	private String noPatternString = "<DOCNO>(.*?)</DOCNO>";
	private String urlPatternString = "(?s)<URL>(.*?)</URL>(.+)";
//	private String titlePatternString = "<contenttitle>(.*?)</contenttitle>";
//	private String contentPatternString = "<content>(.*?)</content>";
	private Pattern docPattern = Pattern.compile(docPatternString);
	private Pattern noPattern = Pattern.compile(noPatternString);
	private Pattern urlPattern = Pattern.compile(urlPatternString);
//	private Pattern titlePattern = Pattern.compile(titlePatternString);
//	private Pattern contentPattern = Pattern.compile(contentPatternString);
	public  DOC getDocBean(String rawText, DOC doc) {
		int num = 0;
		String no = null;
		String url = null;
		String title = null;
		String content = null;
		String html = null;
		String docMatchString = "";
		Matcher docMatcher = docPattern.matcher(rawText);
		int end = 0;
		while (docMatcher.find()) {
			docMatchString = docMatcher.group(1);
//			System.out.println(docMatchString);
			Matcher noMatcher = noPattern.matcher(docMatchString);
			Matcher urlMatcher = urlPattern.matcher(docMatchString);
			if (noMatcher.find() && urlMatcher.find()){
//				title = reProcess(titleMatcher.group());
//				content = reProcess(contentMatcher.group());
//				docbean.setTitle(title);
//				docbean.setContent(content);
//				System.out.println("num = " +num);
				url = urlMatcher.group(1);
				no = noMatcher.group(1);
				html = urlMatcher.group(2);
				Document df = new Document();
				
				if(html != null){
					Htmlparser hp = new Htmlparser(html);
					title = hp.getTitle();
					content = hp.getText();
				}else{
					title = "";
					content = "";
				}
				
				df.addField("DOCNO", no);
				df.addField("URL", url);
				if(title.trim().length()>0)
				df.addField("TITLE", title);
				if(content.trim().length()>0)
				df.addField("CONTENTS", content);
				doc.appendDf(df);
//				System.out.println("url="+url);
//				System.out.println("no="+no);
//				System.out.println("title="+title);
//				System.out.println("content="+content);
				num++;
				
			}
			end = docMatcher.end();
		}
		doc.setSB(rawText.substring(end));
//		System.out.println(doc.getSB());
//		System.out.println("txt contains " +num+ " docs!");
		return doc;
	}

	/**
	 * 澶勭悊鏂囦欢鐩綍涓嬬殑鎵�湁鎸囧畾鏂囦欢.灏嗚繖浜沝oc瀵煎叆鍒版暟鎹簱涓�
	 * 
	 * @param fileDir
	 * @throws IOException 
	 */
	public void processor(String fileDir) throws IOException {
			this.getDocBean(new ReadTxtByNio(fileDir,1111120).read("GB2312"), new DOC());
	}


	public static void main(String args[]) throws IOException {
		System.out.println(Integer.MAX_VALUE);;
//		resultToFile("Sogou.txt");
		new Parser().processor("D:\\sougoucorpus\\SogouT.033.txt");
	}
}

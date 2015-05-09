package TBTest;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.edu.hit.mitlab.informationretrieval.Index;

public class TBtest {
	public static void main(String[] args) throws IOException {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				DOC doc = new DOC();
				String rawText;
				ReadTxtByNio rtb;
				try {
					rtb = new ReadTxtByNio("D:\\sougoucorpus\\SogouT.033.txt",1024*1024* 64);
					Index ir = new Index();
					ir.initIndexWriter(
							"D:\\code\\InformationRetrieval\\luceneIndex",
							Index.CHINESE);
					Parser p = new Parser();
					while((rawText = rtb.read("GBK")) != null){
						
						doc = p.getDocBean(doc.getSB().append(rawText).toString(), doc);
//						System.out.println(doc.getDfs().size());
						ir.addIndexs(doc.getDfs());
				ir.closeIndexWriter();
						doc.clearDF();
						break;
					}
					rtb.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
//		String html = "<html><head><title> 开源中国社区 </title></head>"
//
//		+ "<body><p> 这里是 jsoup 项目的相关文章 </body></html>";
//		Htmlparser hp = new Htmlparser(html);
//		System.out.println(hp.getText());
// Document doc = Jsoup.parse(html); 
// System.out.println(doc.text());
//		new Parser().processor("D:\\sougoucorpus\\SogouT.033.txt");;
	}
}

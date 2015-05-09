package TBTest;

import java.util.ArrayList;

import cn.edu.hit.mitlab.informationretrieval.Document;


public class DOC {
	public ArrayList<Document> dfs = new ArrayList<Document>();
	public StringBuffer sb = new StringBuffer();
	public void clearDF(){
		dfs.clear();
	}
	public void appendDf(Document df){
		dfs.add(df);
	}
	public ArrayList<Document> getDfs(){
		return dfs;
	}
	public void setSB(String s){
		sb = new StringBuffer(s);
	}
	public StringBuffer getSB(){
		return sb;
	}
}

package cn.edu.hit.mitlab.informationretrieval;

import java.util.ArrayList;


public class BooleanQueryClause {
	private String field = null;
	private String keyWord = null;
	private Occur occur = null;
	
	public enum Occur {
		MUST,SHOULD,MUST_NOT;
	}
	
	public BooleanQueryClause(String field, String keyWord, Occur occur){
		this.field = field;
		this.keyWord = keyWord;
		this.occur = occur;
	}
	
	public String getField(){
		return field;
	}
	
	public String getKeyWord(){
		return keyWord;
	}
	
	public Occur getOccur(){
		return occur;
	}
}

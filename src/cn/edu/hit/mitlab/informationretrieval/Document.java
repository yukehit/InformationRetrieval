package cn.edu.hit.mitlab.informationretrieval;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yk
 * @version 1.0
 */
public class Document {
	public Map<String, String> fields;
	public Document(){
		fields = new HashMap<String, String>();
	}
	
	/**
	 * @param fieldName document fieldname
	 * @param text text of this field
	 */
	public void addField(String fieldName, String text){
		fields.put(fieldName, text);
	}
}

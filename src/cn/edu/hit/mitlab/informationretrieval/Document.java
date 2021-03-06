package cn.edu.hit.mitlab.informationretrieval;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yk
 * @version 1.0
 */
public class Document {
	protected Map<String, String> fields;
	protected Map<String, StoredType> storedTypes;
	
	/**
	 * StoredType
	 */
	public enum StoredType{
		NUMERIC_DOUBLE, NUMERIC_LONG, STRING_UN_TOKENIZED, STRING_TOKENIZED;
	}
	
	public Document(){
		fields = new HashMap<String, String>();
		storedTypes = new HashMap<String, Document.StoredType>();
	}
	
	/**
	 * @param fieldName document fieldname
	 * @param text text of this field
	 * @param storedType store type, see StoredType
	 */
	public void addField(String fieldName, String text, StoredType storedType){
		fields.put(fieldName, text);
		storedTypes.put(fieldName, storedType);
	}
	
	/**
	 * @param fieldName
	 * @return the value of field
	 */
	public String getField(String fieldName){
		return fields.get(fieldName);
	}
}

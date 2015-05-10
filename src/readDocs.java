import java.io.File;
import java.util.ArrayList;

import cn.edu.hit.mitlab.informationretrieval.Document.StoredType;




public class readDocs {
	public ArrayList<cn.edu.hit.mitlab.informationretrieval.Document> read(String dataDirPath){
		ArrayList<cn.edu.hit.mitlab.informationretrieval.Document> df = new ArrayList<cn.edu.hit.mitlab.informationretrieval.Document>();
		File dataDir = new File(dataDirPath);
		File[] dataFiles = dataDir.listFiles();
		int j = 0;
		for (int i = 0; i < dataFiles.length; i++) {
			if (dataFiles[i].isFile()
					&& dataFiles[i].getName().endsWith(".txt")) {
				cn.edu.hit.mitlab.informationretrieval.Document d = new cn.edu.hit.mitlab.informationretrieval.Document();
				d.addField("path", dataFiles[i].getAbsolutePath(),StoredType.STRING_UN_TOKENIZED);
				d.addField("contents", LoadTxt.readToString(dataFiles[i].getAbsolutePath()),StoredType.STRING_TOKENIZED);
				d.addField("num", dataFiles[i].getName().replace(".txt", ""), StoredType.NUMERIC_LONG);
				d.addField("time", dataFiles[i].getName().replace(".txt", "")+"day", StoredType.STRING_UN_TOKENIZED);
				df.add(d);
			}
		}
		return df;
		
	}
}

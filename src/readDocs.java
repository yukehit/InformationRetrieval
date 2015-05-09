import java.io.File;
import java.util.ArrayList;




public class readDocs {
	public ArrayList<cn.edu.hit.mitlab.informationretrieval.Document> read(String dataDirPath){
		ArrayList<cn.edu.hit.mitlab.informationretrieval.Document> df = new ArrayList<cn.edu.hit.mitlab.informationretrieval.Document>();
		File dataDir = new File(dataDirPath);
		File[] dataFiles = dataDir.listFiles();
		for (int i = 0; i < dataFiles.length; i++) {
			if (dataFiles[i].isFile()
					&& dataFiles[i].getName().endsWith(".txt")) {
				cn.edu.hit.mitlab.informationretrieval.Document d = new cn.edu.hit.mitlab.informationretrieval.Document();
				d.addField("path", dataFiles[i].getAbsolutePath());
				d.addField("contents", LoadTxt.readToString(dataFiles[i].getAbsolutePath()));
				df.add(d);
			}
		}
		return df;
		
	}
}

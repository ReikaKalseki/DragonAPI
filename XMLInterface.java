package Reika.DragonAPI;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XMLInterface {

	private Document doc;

	public XMLInterface(Class root, String path) {
		String filepath = root.getResource(path).getPath();
		File xml = new File(filepath);
		if (!xml.exists()) {
			throw new RuntimeException("XML file does not exist at "+filepath+"!");
		}
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        doc = builder.parse(xml);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("XML file failed to load!");
		}
	}

	public Object getValueAtNode() {
		return null;
	}

	public NodeList getElementsByName(String name) {
		return doc.getElementsByTagName(name);
	}

}

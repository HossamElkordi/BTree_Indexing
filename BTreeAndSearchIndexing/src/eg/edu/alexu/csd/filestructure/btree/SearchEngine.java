package eg.edu.alexu.csd.filestructure.btree;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SearchEngine implements ISearchEngine{

private IBTree<String, String> btree;
	
	public SearchEngine(int minDegree) {
		btree = new BTree<String, String>(minDegree);
	}

	public void indexWebPage(String filePath) {
		File file = new File(filePath);
		readAllFiles(file, true);
	}

	public void indexDirectory(String directoryPath) {
		File file = new File(directoryPath);
		readAllFiles(file, true);
	}

	public void deleteWebPage(String filePath) {
		File file = new File(filePath);
		readAllFiles(file, false);
	}

	public List<ISearchResult> searchByWordWithRanking(String word) {
		return null;
	}

	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
		return null;
	}

	private void readAllFiles(File res, boolean ID) {
		if(res.isDirectory()) {
			for (File file : res.listFiles()) {
				if(file.isDirectory()) {
					readAllFiles(file, ID);
				}else {
					if(ID)
						indexInTree(file);
					else
						delFromTree(file);
				}
			}
		}else {
			if(ID)
				indexInTree(res);
			else
				delFromTree(res);
		}
	}
	
	private void delFromTree(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			document.getDocumentElement().normalize();
			NodeList nodes = document.getElementsByTagName("doc");
			for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					btree.delete(e.getAttribute("id"));
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

	private void indexInTree(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(file);
			document.getDocumentElement().normalize();
			NodeList nodes = document.getElementsByTagName("doc");
			for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					Element e = (Element) n;
					btree.insert(e.getAttribute("id"), e.getTextContent().trim());
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

}

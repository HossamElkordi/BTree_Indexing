package eg.edu.alexu.csd.filestructure.btree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SearchEngine implements ISearchEngine{

	private final IBTree<String, String> btree;
	private List<ISearchResult>answer;
	private Map<Integer, String> rkPairs;

	public SearchEngine(int minDegree) {
		btree = new BTree<String, String>(minDegree);
	}

	public void indexWebPage(String filePath) {
		if(filePath==null||filePath.equals(""))
			throw new RuntimeErrorException(null);
		File file = new File(filePath);
		readAllFiles(file, true);
	}

	public void indexDirectory(String directoryPath) {
		if(directoryPath==null||directoryPath.equals(""))
			throw new RuntimeErrorException(null);
		File file = new File(directoryPath);
		readAllFiles(file, true);
	}

	public void deleteWebPage(String filePath) {
		if(filePath==null||filePath.equals(""))
			throw new RuntimeErrorException(null);
		File file = new File(filePath);
		readAllFiles(file, false);
	}

	public List<ISearchResult> searchByWordWithRanking(String word) {
		if(word==null)
            throw new RuntimeErrorException(null);
        if(word.trim().equals(""))
            return new ArrayList<>();
        answer=new ArrayList<ISearchResult>();
        rkPairs = new HashMap<Integer, String>();
        traverseSingleWord(word.toLowerCase().trim(),btree.getRoot());
        putInAnswer();
        return answer;
//		if(word==null)
//			throw new RuntimeErrorException(null);
//		if(word.trim().equals(""))
//			return new ArrayList<>();
//		answer=new ArrayList<ISearchResult>();
//		Traversal(word.trim().toLowerCase(),btree.getRoot());
//		Collections.sort(answer, new Comparator<ISearchResult>() {
//			@Override
//			public int compare(ISearchResult o1, ISearchResult o2) {
//				return Integer.parseInt(o1.getId()) - Integer.parseInt(o2.getId());
//			}
//		});
//		return answer;
	}
	
	private void traverseSingleWord(String word, IBTreeNode<String, String> root) {
		if(root == null) return;
		for (int i = 0; i < root.getKeys().size(); i++) {
			visitKey(word, root.getValues().get(i), root.getKeys().get(i));
		}
		if(!root.isLeaf()) {
			for(IBTreeNode<String, String> child : root.getChildren()) {
				traverseSingleWord(word, child);
			}
		}
	}
	
	private void traverseMulWord(IBTreeNode<String, String> root, List<String> permutations) {
		if(root == null) return;
		int min ;
		String[] words;
		for (int i = 0; i < root.getKeys().size(); i++) {
			min = 0;
			for (String word : permutations) {
				words = root.getValues().get(i).split("\\b" + word + "\\b");
				if(words.length > 1) {
					if ((min <= 0) || (words.length-1 < min)) min = words.length-1;
				}
			}
			if(min > 0) storeResult(root.getKeys().get(i), min);
		}
		if(!root.isLeaf()) {
			for(IBTreeNode<String, String> child : root.getChildren()) {
				traverseMulWord(child, permutations);
			}
		}
	}
	
	private void visitKey(String word, String text, String key) {
		String[] words = text.split("\\b" + word + "\\b");
		if(words.length > 1) storeResult(key, words.length-1);
	}
	
	private void storeResult(String id, int rank) {
		String key = rkPairs.get(rank);
		if((key == null) || (id.compareTo(key) < 0)) {
			rkPairs.put(rank, id);
		}
	}
	
	private void putInAnswer() {
		for (Map.Entry<Integer, String> entry : rkPairs.entrySet()) {
			answer.add(new SearchResult(entry.getValue(), entry.getKey()));
		}
        /*Collections.sort(answer, new Comparator<ISearchResult>() {
			@Override
			public int compare(ISearchResult o1, ISearchResult o2) {
				return o1.getId().compareTo(o2.getId());
			}
		});*/
	}

	/*private void Traversal(String word,IBTreeNode<String, String> root)
	{
		if(root==null)
			return;
		for(int i=0;i<root.getNumOfKeys();++i)
		{
			String text=root.getValues().get(i).trim();
			String[] Occurrence =text.split("\\W+");
			int count=0;
			for(String str: Occurrence)
			{
				if(word.equals(str.trim().toLowerCase()))
					++count;
			}
			if(count!=0)
				answer.add(new SearchResult(root.getKeys().get(i),count));
		}
		if(root.getChildren()!=null)
		{
			for(IBTreeNode<String, String> child:root.getChildren())
				Traversal(word, child);
		}
	}*/

	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence) {
		if(sentence==null)
            throw new RuntimeErrorException(null);
        if(sentence.trim().equals(""))
            return new ArrayList<>();
        List<String> permutations = permuteWords(sentence.trim().toLowerCase());
        answer=new ArrayList<ISearchResult>();
        rkPairs = new HashMap<Integer, String>();
        traverseMulWord(btree.getRoot(), permutations);
        putInAnswer();
        return answer;
//		if(sentence==null)
//			throw new RuntimeErrorException(null);
//		if(sentence.trim().equals(""))
//			return new ArrayList<>();
//		sentence=sentence.trim();
//		String[] split = sentence.split("\\W+");
//		answer = searchByWordWithRanking(split[0]);
//		for(int i = 1; i < split.length; i++) {
//			String str = split[i];
//			List<ISearchResult> EachWord = searchByWordWithRanking(str);
//
//			List<ISearchResult> temp = new ArrayList<>();
//			for(ISearchResult list1 : answer) {
//				for(ISearchResult list2 : EachWord) {
//					if(list1.getId().equals(list2.getId())) {
//						temp.add(new SearchResult(list1.getId(), Math.min(list1.getRank(), list2.getRank())));
//					}
//				}
//			}
//			answer = temp;
//		}
//		return answer;
	}
	
	public static List<String> permuteWords(String s) {
		String[] ss = s.split("\\W+");
        boolean[] used = new boolean[ss.length];
        String res = "";
        List<String> List = new ArrayList<String>();
        for(int i = 0; i < ss.length; i++) {
        	permute(ss, used, res, 0, List, i+1);
        }
        return List;
    }

	private static void permute(String[] ss, boolean[] used, String res, int level, List<String> List, int size) {
        if ((level == ss.length && !res.equals("")) || (level == size && !res.equals("")))
        {
            List.add(res.trim());
            return;
        }
        for (int i = 0; i < ss.length; i++)
        {
            if (used[i]) continue;
            used[i] = true;
            permute(ss, used, res + " " + ss[i], level + 1, List, size);
            used[i] = false;
        }
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
					btree.insert(e.getAttribute("id"), e.getTextContent().trim().toLowerCase());
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

}

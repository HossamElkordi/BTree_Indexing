package eg.edu.alexu.csd.filestructure.btree;

import java.util.List;

public interface ISearchEngine {

	/*
	 * Given a file path that contains one or more Wikipedia
	 * documents in the same XML format of the provided sample data.
	 * It is required to index these document content to be
	 * able to search through them later.
	 */
	public void indexWebPage(String filePath);
	/*
	 * Given a root directory, it is required to index all
	 * the files in the given directory/sub directories.
	 * The files will be in the same format as described 
	 * in the previous function.
	 * It is required to index these document content to be
	 * able to search through them later.
	 */
	public void indexDirectory(String directoryPath);
	/*
	 * Given a file path that contains one or more Wikipedia
	 * documents in the same XML format as described in the previous function.
	 * It is required to delete these documents IDs from the 
	 * BTree index if they were indexed before.
	 */
	public void deleteWebPage(String filePath);
	/*
	 * Given a search query of one word, it is required to return a list
	 * of SearchResult interface that contains the documents' IDs where the given 
	 * word appears in along with each documents' rank.
	 * The rank of Wikipedia document is the frequency of the given search 
	 * word in this document.
	 * Search word are not case sensitive.
	 */
	public List<ISearchResult> searchByWordWithRanking(String word);
	/*
	 * Similar to the previous function but the search query will consist of
	 * multiple words.
	 * You are required to return the Wikipedia documents that have this set of words
	 * in ANY order.
	 * Search words are not case sensitive.
	 * The rank of a document is the minimum frequency of the given 
	 * words appearance in this document.
	 */
	public List<ISearchResult> searchByMultipleWordWithRanking(String sentence);
}

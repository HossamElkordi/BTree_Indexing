package eg.edu.alexu.csd.filestructure.btree;

public interface ISearchResult {

	/*
	 * Return the document's ID, which is an attribute
	 * provided with each Wikipedia document.
	 */
	public String getId();
	public void setId(String id);
	/*
	 * Return the frequency of the word in the given document.
	 */
	public int getRank();
	public void setRank(int rank);

}

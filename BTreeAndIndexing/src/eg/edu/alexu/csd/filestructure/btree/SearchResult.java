package eg.edu.alexu.csd.filestructure.btree;

public class SearchResult implements ISearchResult {
	
	private String id;
	private int rank;
	
	public SearchResult(String id, int rank) {
		this.id = id;
		this.rank = rank;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getRank() {
		return this.rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

}

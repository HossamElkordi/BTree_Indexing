package eg.edu.alexu.csd.filestructure.btree;

import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {

	public int getNumOfKeys() {
		return 0;
	}

	public void setNumOfKeys(int numOfKeys) {
		
	}

	public boolean isLeaf() {
		return false;
	}

	public void setLeaf(boolean isLeaf) {
		
	}

	public List<K> getKeys() {
		return null;
	}

	public void setKeys(List<K> keys) {
		
	}

	public List<V> getValues() {
		return null;
	}

	public void setValues(List<V> values) {
		
	}

	public List<IBTreeNode<K, V>> getChildren() {
		return null;
	}

	public void setChildren(List<IBTreeNode<K, V>> children) {
		
	}

}

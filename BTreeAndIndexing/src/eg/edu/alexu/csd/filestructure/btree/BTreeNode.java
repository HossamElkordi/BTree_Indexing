package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.List;

public class BTreeNode<K extends Comparable<K>, V> implements IBTreeNode<K, V> {
	
	private int numOfKeys = 0;
	private boolean leaf = false;
	
	private List<K> keys;
	private List<V> values;
	private List<IBTreeNode<K, V>> children;
	
	public BTreeNode(boolean leaf) {
		this.leaf = leaf;
		keys = new ArrayList<K>();
		values = new ArrayList<V>();
		if(!leaf) children = new ArrayList<IBTreeNode<K, V>>();
	}

	public int getNumOfKeys() {
		return this.numOfKeys;
	}

	public void setNumOfKeys(int numOfKeys) {
		this.numOfKeys = numOfKeys;
	}

	public boolean isLeaf() {
		return this.leaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.leaf = isLeaf;
	}

	public List<K> getKeys() {
		return this.keys;
	}

	public void setKeys(List<K> keys) {
		this.keys = keys;
	}

	public List<V> getValues() {
		return this.values;
	}

	public void setValues(List<V> values) {
		this.values = values;
	}

	public List<IBTreeNode<K, V>> getChildren() {
		return this.children;
	}

	public void setChildren(List<IBTreeNode<K, V>> children) {
		this.children = children;
	}

}

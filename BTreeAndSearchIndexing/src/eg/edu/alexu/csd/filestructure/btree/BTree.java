package eg.edu.alexu.csd.filestructure.btree;

import javax.management.RuntimeErrorException;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
	
	private int minDegree = 0;
	private IBTreeNode<K, V> root;
	
	public BTree(int minDegree) {
		this.minDegree = minDegree;
		if(this.minDegree < 2) throw new RuntimeErrorException(null);
	}

	public int getMinimumDegree() {
		return this.minDegree;
	}

	public IBTreeNode<K, V> getRoot() {
		return this.root;
	}

	public void insert(K key, V value) {
		if((key == null) || (value == null)) throw new RuntimeErrorException(null);
		
		if(root == null) {
			
			root = new BTreeNode<K, V>(true);
			root.getKeys().add(key);
			root.getValues().add(value);
			root.setNumOfKeys(1);
			
		}else {
			insertHelper(key, value, root, null);
		}
	}

	private void insertHelper(K key, V value, IBTreeNode<K, V> node, IBTreeNode<K, V> parent) {
		if(node.isLeaf()) {
			if(recursiveSearch(key, node) != null) return;
			// put the (key, value) at the end of the list and swap them until the right place
			node.getKeys().add(key);
			node.getValues().add(value);			
			int i = node.getNumOfKeys();
			while((i > 0) && (node.getKeys().get(i).compareTo(node.getKeys().get(i-1)) < 0)) {
				swapKeyValue(node, i, i - 1);
				--i;
			}
			node.setNumOfKeys(node.getNumOfKeys() + 1);
			
			if(node.getNumOfKeys() > ((2 * this.minDegree) - 1)) {
				fixUpInsert(node, parent);
			}
		}else {
			int index = indexOfKey(key, node);
			insertHelper(key, value, node.getChildren().get(index), node);
			if(node.getNumOfKeys() > ((2 * this.minDegree) - 1)) {
				fixUpInsert(node, parent);
			}
		}
	}
	
	private void fixUpInsert(IBTreeNode<K, V> node, IBTreeNode<K, V> parent) {
		int median = node.getNumOfKeys() / 2;
		IBTreeNode<K, V> splitted = new BTreeNode<K, V>(node.isLeaf());
		if(parent == null) {
			// node is root (split root case)
			IBTreeNode<K, V> newRoot = new BTreeNode<K, V>(false);
			split(node, splitted, newRoot, median);
			root = newRoot;
		}else {
			split(node, splitted, parent, median);
		}
	}
	
	private void split(IBTreeNode<K, V> node, IBTreeNode<K, V> splitted, IBTreeNode<K, V> parent, int median) {
		for(int i = median + 1; i < node.getNumOfKeys(); i++) {
			splitted.getKeys().add(node.getKeys().get(i));
			splitted.getValues().add(node.getValues().get(i));
			splitted.setNumOfKeys(splitted.getNumOfKeys() + 1);
		}
		
		if(!node.isLeaf()) {
			for(int i = median; i < (node.getNumOfKeys() + 1); i++) {
				splitted.getChildren().add(node.getChildren().get(i));
			}
		}
		
		node.setNumOfKeys(node.getNumOfKeys() - splitted.getNumOfKeys());
		
		parent.getKeys().add(node.getKeys().get(median));
		parent.getValues().add(node.getValues().get(median));
		parent.getChildren().add(splitted);
		int i = parent.getNumOfKeys();
		int j = i + 1;
		while((i > 0) && (parent.getKeys().get(i).compareTo(parent.getKeys().get(i-1)) < 0)) {
			swapKeyValue(parent, i, i - 1);
			IBTreeNode<K, V> tempChild = parent.getChildren().get(j - 1);
			parent.getChildren().set(j - 1, parent.getChildren().get(j));
			parent.getChildren().set(j, tempChild);
			--i;
			--j;
		}
		if(parent.getChildren().size() == 1) {
			parent.getChildren().add(0, node);
		}
		node.setNumOfKeys(node.getNumOfKeys() - 1);
		parent.setNumOfKeys(parent.getNumOfKeys() + 1);
		
		
		node.getKeys().removeAll(splitted.getKeys());
		node.getValues().removeAll(splitted.getValues());
		if(!node.isLeaf()) node.getChildren().removeAll(splitted.getChildren());
		
		node.getKeys().remove(node.getKeys().size() - 1);
		node.getValues().remove(node.getValues().size() - 1);
		
	}
	
	public V search(K key) {
		if(key == null) throw new RuntimeErrorException(null);
		IBTreeNode<K, V> node = root;
		return recursiveSearch(key, node);
	}

	public boolean delete(K key) {
		if(key == null) throw new RuntimeErrorException(null);
		return false;
	}

	/*
	 * search for the given key in the given node.
	 * if found -> return its index
	 * if not found -> return the index of the first key greater than the given key
	 */
	private int indexOfKey(K key, IBTreeNode<K, V> node) {
		int i = 0;
		while((i < node.getNumOfKeys()) && (key.compareTo(node.getKeys().get(i)) > 0)) {
			++i;
		}
		return i;
	}
	
	private V recursiveSearch(K key, IBTreeNode<K, V> node) {
		int index = indexOfKey(key, node);
		try {
			if(key.equals(node.getKeys().get(index))) {
				return node.getValues().get(index);
			}else {
				return (node.isLeaf()) ? null : recursiveSearch(key, node.getChildren().get(index));
			}
		} catch (Exception e) {
			return (node.isLeaf()) ? null : recursiveSearch(key, node.getChildren().get(index));
		}
	}
	
	private void swapKeyValue(IBTreeNode<K, V> node, int i, int j) {
		K tempKey = node.getKeys().get(i);
		node.getKeys().set(i, node.getKeys().get(j));
		node.getKeys().set(j, tempKey);
		
		V tempValue = node.getValues().get(i);
		node.getValues().set(i, node.getValues().get(j));
		node.getValues().set(j, tempValue);
	}
}

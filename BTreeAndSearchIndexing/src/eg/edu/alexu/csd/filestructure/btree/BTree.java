package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.List;

import javax.management.RuntimeErrorException;

public class BTree<K extends Comparable<K>, V> implements IBTree<K, V> {
	
	private int minDegree = 0;
	private IBTreeNode<K, V> root;
	private boolean delFlag = true;
	
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
			insertProactive(key, value, root);
//			insertReactiveHelper(key, value, root, null);
		}
	}

//	======================== Reactive Insert ========================
	
//	private void insertReactiveHelper(K key, V value, IBTreeNode<K, V> node, IBTreeNode<K, V> parent) {
//		if(recursiveSearch(key, node) != null) return;
//		if(node.isLeaf()) {
//			// put the (key, value) at the end of the list and swap them until the right place
//			node.getKeys().add(key);
//			node.getValues().add(value);			
//			int i = node.getNumOfKeys();
//			while((i > 0) && (node.getKeys().get(i).compareTo(node.getKeys().get(i-1)) < 0)) {
//				swapKeyValue(node, i, i - 1);
//				--i;
//			}
//			node.setNumOfKeys(node.getNumOfKeys() + 1);
//			
//			if(node.getNumOfKeys() > ((2 * this.minDegree) - 1)) {
//				fixUpInsert(node, parent);
//			}
//		}else {
//			int index = indexOfKey(key, node);
//			insertReactiveHelper(key, value, node.getChildren().get(index), node);
//			if(node.getNumOfKeys() > ((2 * this.minDegree) - 1)) {
//				fixUpInsert(node, parent);
//			}
//		}
//	}
	
//	======================== End Reactive Insert ========================
	
//	======================== Proactive Insert ========================
	
	private void insertProactive(K key, V value, IBTreeNode<K, V> root) {
		if(root.getNumOfKeys() == ((2 * this.minDegree) - 1)) {
			fixUpInsert(root, null);
			if(key.compareTo(this.root.getKeys().get(0)) < 0) {
				insertProactiveHelper(key, value, this.root.getChildren().get(0), root);
			}else {
				insertProactiveHelper(key, value, this.root.getChildren().get(1), root);
			}
		}else {
			insertProactiveHelper(key, value, root, null);
		}
	}
	
	private void insertProactiveHelper(K key, V value, IBTreeNode<K, V> node, IBTreeNode<K, V> parent) {
		if(recursiveSearch(key, node) != null) return;
		if(node.isLeaf()) {
			// put the (key, value) at the end of the list and swap them until the right place
			node.getKeys().add(key);
			node.getValues().add(value);			
			int i = node.getNumOfKeys();
			while((i > 0) && (node.getKeys().get(i).compareTo(node.getKeys().get(i-1)) < 0)) {
				swapKeyValue(node, i, i - 1);
				--i;
			}
			node.setNumOfKeys(node.getNumOfKeys() + 1);
		}else {
			int index = indexOfKey(key, node);
			IBTreeNode<K, V> toGoNext = node.getChildren().get(index);
			if(toGoNext.getNumOfKeys() == ((2 * this.minDegree) - 1)) {
				fixUpInsert(toGoNext, node);
			}
			insertProactiveHelper(key, value, node.getChildren().get(indexOfKey(key, node)), node);
		}
	}
	
//  ======================== End Proactive Insert ========================
	
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
		int k = median + 1;
		for(int i = median + 1; i < node.getNumOfKeys(); i++) {
			splitted.getKeys().add(node.getKeys().remove(k));
			splitted.getValues().add(node.getValues().remove(k));
			splitted.setNumOfKeys(splitted.getNumOfKeys() + 1);
		}
		
		if(!node.isLeaf()) {
			for(int i = median + 1; i < (node.getNumOfKeys() + 1); i++) {
				splitted.getChildren().add(node.getChildren().remove(k));
			}
		}
		
		node.setNumOfKeys(node.getNumOfKeys() - splitted.getNumOfKeys());
		
		parent.getKeys().add(node.getKeys().remove(median));
		parent.getValues().add(node.getValues().remove(median));
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
	}
	
	public V search(K key) {
		if(key == null) throw new RuntimeErrorException(null);
		IBTreeNode<K, V> node = root;
		return recursiveSearch(key, node);
	}

	public boolean delete(K key) {
		if (key == null) {
			throw new RuntimeErrorException(null);
		}

		else if (getRoot() == null) {
			return false;
		}
		remove(root, key);
		if (root.getNumOfKeys() == 0)
		{
			if (root.isLeaf())
				root = null;
			else
				root = root.getChildren().get(0);
		}
		if (delFlag) {
			return true;
		}
		else {
			return false;
		}
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

	private void remove(IBTreeNode<K, V> root, K key) {
		int index = findKey(root, key);
		if (!(index < root.getNumOfKeys() && root.getKeys().get(index).compareTo(key) == 0)) {
			if (root.isLeaf())
			{
				delFlag = false;
				return;
			}
			boolean flag = ( (index== root.getNumOfKeys())? true : false );
			if (   root.getChildren().get(index).getNumOfKeys() < getMinimumDegree())
				fill(root, index);
			if (flag && index > root.getNumOfKeys())
				remove(root.getChildren().get(index - 1), key);
			else
				remove(root.getChildren().get(index), key);
		}
		else{
			if (root.isLeaf())
				removeFromLeaf(root, index);
			else
				removeFromNonLeaf(root, index);
		}
		return;
	}

	private int findKey(IBTreeNode<K, V> node, K k)	{
		int index=0;
		while (index< node.getNumOfKeys() && node.getKeys().get(index).compareTo(k) < 0)
			++index;
		return index;
	}
	private void removeFromNonLeaf(IBTreeNode<K, V> root2, int index) {
		K k = root2.getKeys().get(index);
		if  (root2.getChildren().get(index+1).getNumOfKeys() >= getMinimumDegree()){
			IBTreeNode<K, V> succ = getSuccessor(root2, index);
			root2.getKeys().set(index, succ.getKeys().get(0));
			root2.getValues().set(index, succ.getValues().get(0));
			remove(root2.getChildren().get(index+1), succ.getKeys().get(0));
		}
		else if (root2.getChildren().get(index).getNumOfKeys() >= getMinimumDegree()){
			IBTreeNode<K, V> pred = getPredecessor(root2, index);
			root2.getKeys().set(index, pred.getKeys().get(pred.getNumOfKeys()-1));
			root2.getValues().set(index, pred.getValues().get(pred.getNumOfKeys()-1));
			remove(root2.getChildren().get(index), pred.getKeys().get(pred.getNumOfKeys()-1));
		}
		else{merge(root2, index);
			remove(root2.getChildren().get(index), k);
		}
		return;

	}
	private IBTreeNode<K, V> getSuccessor(IBTreeNode<K, V> root2, int idx) {IBTreeNode<K, V> successor = root2.getChildren().get(idx+1);
		while (!successor.isLeaf())
			successor = successor.getChildren().get(0);
		// Return the last key of the leaf
		return  successor;
	}
	private void merge(IBTreeNode<K, V> root2, int idx) {
		IBTreeNode<K,V> child = root2.getChildren().get(idx);
		IBTreeNode<K,V> sibling = root2.getChildren().get(idx+1);
		child.getKeys().add(root2.getKeys().get(idx));
		child.getValues().add(root2.getValues().get(idx));
		child.getKeys().addAll(sibling.getKeys());
		child.getValues().addAll(sibling.getValues());
		if (!child.isLeaf()) {
			child.getChildren().addAll(sibling.getChildren());
		}
		root2.getKeys().remove(idx);
		root2.getValues().remove(idx);
		root2.getChildren().remove(idx+1);
		root2.setNumOfKeys(root2.getKeys().size());
		child.setNumOfKeys(child.getKeys().size());
		return;
	}
	private void removeFromLeaf(IBTreeNode<K, V> root2, int idx) {
		root2.getKeys().remove(idx);
		root2.getValues().remove(idx);
		root2.setNumOfKeys(root2.getKeys().size());
	}
	private void borrowFromPrev(IBTreeNode<K, V> root2, int index) {
		IBTreeNode<K,V> child = root2.getChildren().get(index);
		IBTreeNode<K,V> sibling = root2.getChildren().get(index-1);
		child.getKeys().add(0, root2.getKeys().get(index - 1));
		child.getValues().add(0, root2.getValues().get(index - 1));
		root2.getKeys().remove(index-1);
		root2.getValues().remove(index-1);
		if(!child.isLeaf()) {
			child.getChildren().add(0, sibling.getChildren().get(sibling.getKeys().size()));
			sibling.getChildren().remove(sibling.getKeys().size());
		}
		root2.getKeys().add(index-1, sibling.getKeys().get(sibling.getKeys().size() - 1));
		root2.getValues().add(index-1, sibling.getValues().get(sibling.getKeys().size() - 1));
		sibling.getKeys().remove(sibling.getKeys().size() - 1);
		sibling.getValues().remove(sibling.getKeys().size() - 1);
		sibling.setNumOfKeys(sibling.getKeys().size());
		child.setNumOfKeys(child.getKeys().size());
		return;
	}
	private void fill(IBTreeNode<K, V> root2, int index) {if (index!= root2.getNumOfKeys() && root2.getChildren().get(index+1).getNumOfKeys() >= getMinimumDegree()){borrowFromNext(root2, index);}
		else if (index != 0 && root2.getChildren().get(index -1).getNumOfKeys() >= getMinimumDegree()){borrowFromPrev(root2, index);}
		else{if (index != root2.getNumOfKeys()){merge(root2, index);}
			else{merge(root2, index-1);}
		}
		return;
	}
	private void borrowFromNext(IBTreeNode<K, V> root2, int index) {IBTreeNode<K,V> child = root2.getChildren().get(index);
		IBTreeNode<K,V> sibling = root2.getChildren().get(index+1);
		child.getKeys().add(root2.getKeys().get(index));
		child.getValues().add(root2.getValues().get(index));
		root2.getKeys().remove(index);
		root2.getValues().remove(index);
		if(!child.isLeaf()) {child.getChildren().add(sibling.getChildren().get(0));
			sibling.getChildren().remove(0);
		}
		root2.getKeys().add(index, sibling.getKeys().get(0));
		root2.getValues().add(index, sibling.getValues().get(0));
		sibling.getKeys().remove(0);
		sibling.getValues().remove(0);
		sibling.setNumOfKeys(sibling.getKeys().size());
		child.setNumOfKeys(child.getKeys().size());
		return;

	}
	private IBTreeNode<K, V> getPredecessor(IBTreeNode<K, V> root2, int index) {
		IBTreeNode<K, V> pred = root2.getChildren().get(index);
		do{if(pred.isLeaf()){continue;}
			pred = pred.getChildren().get(pred.getNumOfKeys());}while(!pred.isLeaf());
		return  pred;
	}

}

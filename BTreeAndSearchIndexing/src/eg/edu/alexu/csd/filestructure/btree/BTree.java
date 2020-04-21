package eg.edu.alexu.csd.filestructure.btree;

import java.util.ArrayList;
import java.util.List;

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
		if(key == null) throw new RuntimeErrorException(null);
        if(root==null) return false;
        
        List<IBTreeNode<K,V>> ParentAndChild=GetParentNode(key,this.root);
        if(ParentAndChild==null)
        {
            int i=0;
            for(;i<root.getNumOfKeys();++i)
            {
                if(root.getKeys().get(i).compareTo(key)==0)
                    break;
            }
            if(i==root.getNumOfKeys())
                return false;
            ParentAndChild=new ArrayList<>();
            ParentAndChild.add(this.root);
        }
        if(ParentAndChild.size()==2&&ParentAndChild.get(1).isLeaf()
                ||ParentAndChild.size()==1&&ParentAndChild.get(0).isLeaf())
        {
 
        }
        else
        {
            IBTreeNode<K,V>node;
            if(ParentAndChild.size()==2)
                node=ParentAndChild.get(1);
            else
                node=ParentAndChild.get(0);
            int index=indexOfKey(key,node);
            IBTreeNode<K,V>preceding=node.getChildren().get(index);
            IBTreeNode<K,V>successor=node.getChildren().get(index+1);
            if(preceding.getKeys().size()>this.minDegree)
            {
                node.getKeys().set(index,preceding.getKeys().get(preceding.getKeys().size()-1));
                node.getValues().set(index,preceding.getValues().get(preceding.getValues().size()-1));
                preceding.getKeys().remove(preceding.getKeys().size()-1);
                preceding.getValues().remove(preceding.getValues().size()-1);
            }
            else if(successor.getKeys().size()>this.minDegree)
            {
                node.getKeys().set(index,successor.getKeys().get(0));
                node.getValues().set(index,successor.getValues().get(0));
                successor.getKeys().remove(0);
                successor.getValues().remove(0);
            }
            else if(node.getNumOfKeys()>minDegree)
            {
                node.getKeys().set(index,successor.getKeys().get(0));
                node.getValues().set(index,successor.getValues().get(0));
                successor.getKeys().remove(0);
                successor.getValues().remove(0);
                List<IBTreeNode<K,V>>children=node.getChildren();
                int childIndex=children.indexOf(preceding);
                children.remove(childIndex+1);
                children.remove(childIndex);
                for(K keys:successor.getKeys())
                    preceding.getKeys().add(keys);
                for(V values:successor.getValues())
                    preceding.getValues().add(values);
                children.add(childIndex, preceding);
                node.setChildren(children);
            }
        }
        return true;

	}
	
	//returns a List where first element is the parent of the required deleted key as the first element
    //and the second element is the child node that contains the key itself
    private List<IBTreeNode<K,V>>GetParentNode(K key, IBTreeNode<K,V> root)
    {
        List<IBTreeNode<K,V>>answer=new ArrayList<>();
        if(root.getKeys().get(0).compareTo(key)>0&&root.getChildren()!=null)
        {
            for(K temp:root.getChildren().get(0).getKeys())
            {
                if(temp.compareTo(key)==0)
                {
                    answer.add(root);
                    answer.add(root.getChildren().get(0));
                    return answer;
                }
            }
            return GetParentNode(key,root.getChildren().get(0));
        }
        if(root.getKeys().get(root.getNumOfKeys()-1).compareTo(key)<0&&root.getChildren()!=null)
        {
            for(K temp:root.getChildren().get(root.getNumOfKeys()).getKeys())
            {
                if(temp.compareTo(key)==0) {
                    answer.add(root);
                    answer.add(root.getChildren().get(root.getNumOfKeys()));
                }
            }
            return GetParentNode(key,root.getChildren().get(root.getNumOfKeys()));
        }
        for(int i=0;i<root.getNumOfKeys()-1;++i)
        {
            if(root.getKeys().get(i).compareTo(key)<0&&root.getKeys().get(i+1).compareTo(key)>0&&root.getChildren()!=null)
            {
                for(K temp:root.getChildren().get(i+1).getKeys())
                {
                    if(temp.compareTo(key)==0) {
                        answer.add(root);
                        answer.add(root.getChildren().get(i+1));
                        return answer;
                    }
                }
                return GetParentNode(key,root.getChildren().get(i+1));
            }
        }
        return null;
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

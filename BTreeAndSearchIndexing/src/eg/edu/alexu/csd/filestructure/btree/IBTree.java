package eg.edu.alexu.csd.filestructure.btree;

public interface IBTree<K extends Comparable<K>, V> {

	public int getMinimumDegree();
	public IBTreeNode<K, V> getRoot();
	/*
	 * Insert the given key in the tree.
	 * If the key already exists in the tree ignore the call of this method.
	 */
	public void insert(K key, V value);
	public V search(K key);
	/*
	 * Delete the node with the given key from the tree.
	 */
	public boolean delete(K key);

}

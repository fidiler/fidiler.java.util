public class RedBlackTree<K extends Comparable<K>, V> {
    static private class Node<K extends Comparable<K>, V> {
        private static final boolean RED = true;
        private static final boolean BLACK = false;

        private K key;
        private V value;
        private Node<K, V> left, right;
        private boolean color;

        Node(K key, V value, Node<K, V> left, Node<K, V> right, boolean color) {
            this.key = key;
            this.value = value;
            this.left = left;
            this.right = right;
            this.color = color;
        }

        Node(K key, V value) {
            this(key, value, null, null, BLACK);
        }
    }

    private int size;
    private Node<K, V> root;

    RedBlackTree() {
        size = 0;
        root = null;
    }

    public void add(K k, V v) {

    }

    private Node<K, V> add(Node<K, V> node, K k, V v) {
        if (node == null) {
            size++;
            return new Node<K, V>(k, v);
        }

        if (k.compareTo(node.key) < 0) {
            node = add(node.left, k, v);
        } else if (k.compareTo(node.key) > 0) {
            node = add(node.right, k, v);
        }

        if (isRed(node.left) && isRed(node.left.left)) {

        }


        return node;
    }

    /**CLL**/
    private Node<K, V> rotateRight(Node<K, V> node) {
        Node<K, V> x = node.left;
        node.left = x.right;
        x.color = node.color;
        node.color = Node.BLACK;
        x.right = node;

    }

    /**isRed 判断节点是否是红节点**/
    private boolean isRed(Node<K, V> node) {
        return node.color == Node.RED;
    }
}

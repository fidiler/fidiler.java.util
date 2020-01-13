public class RedBlackTree<K extends Comparable<K>, V> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    static private class Node<K extends Comparable<K>, V> {
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
            this(key, value, null, null, RED);
        }
    }

    private int size;
    private Node<K, V> root;

    RedBlackTree() {
        size = 0;
        root = null;
    }

    public void add(K k, V v) {
        root = add(root, k, v);
        root.color = BLACK;
    }

    private Node<K, V> add(Node<K, V> node, K k, V v) {
        if (node == null) {
            size++;
            return new Node<K, V>(k, v);
        }

        if (k.compareTo(node.key) < 0) {
            node.left = add(node.left, k, v);
        } else if (k.compareTo(node.key) > 0) {
            node.right = add(node.right, k, v);
        }

        // 右节点为红色（在右侧出现3-node）对node进行左旋转修复
        if (!isRed(node.left) && isRed(node.right)) {
            node = rotateLeft(node);
        }

        // 左节点是RED节点, 左节点的左节点也是RED节点,
        // 当插入元素在左节点（左节点是RED节点）或者经过右旋转修复才会出现这种情况
        // 此时node.left.left是一个3-node节点,对node进行右旋转修复
        if (isRed(node.left) && isRed(node.left.left)) {
            node = rotateRight(node);
        }

        // 左右节点都是RED节点, 此时node相当于是一个4-node
        // 对4-node的处理是进行颜色翻转
        if (isRed(node.left) && isRed(node.right)) {
            flipColor(node);
        }

        return node;
    }

    private Node<K, V> rotateLeft(Node<K, V> node) {
        Node<K, V> root = node.right;
        node.right = root.left;
        root.left = node;
        root.color = node.color;
        node.color = RED;
        return root;
    }

    private Node<K, V> rotateRight(Node<K, V> node) {
        Node<K, V> root = node.left;
        node.left = root.right;
        root.right = node;
        root.color = node.color;
        node.color = RED;
        return root;
    }

    private void flipColor(Node<K, V> node) {
        node.left.color = node.right.color = BLACK;
        node.color = RED;
    }

    /**
     * isRed 判断节点是否是红节点
     **/
    private boolean isRed(Node<K, V> node) {
        return node != null && node.color == RED;
    }

    public static void main(String[] args) {
        RedBlackTree<String, Integer> rbt = new RedBlackTree<>();
        rbt.add("S", 1);
        rbt.add("E", 1);
        rbt.add("A", 1);
        rbt.add("R", 1);
        rbt.add("C", 1);
        rbt.add("H", 1);
        rbt.add("X", 1);
        rbt.add("M", 1);
        rbt.add("P", 1);
        rbt.add("L", 1);
    }
}

public class TwoThreeTree<K extends Comparable<K>, V> {

    // 2-3 node define:
    // 2-node: they hold two links (left and right) and one key
    // 3-node: they hold three links (left, middle, right) and two keys
    static private class Node<K extends Comparable<K>, V> {
        public K leftKey, middleKey, rightKey;
        public V leftValue, middleValue, rightValue;
        public Node<K, V> left, middleLeft, middleRight, right;

        private Node(K leftKey, V leftValue, K middleKey, V middleValue, K rightKey, V rightValue,
                     Node<K, V> left, Node<K, V> middleLeft, Node<K, V> middleRight, Node<K, V> right) {

            this.leftKey = leftKey;
            this.leftValue = leftValue;
            this.middleKey = middleKey;
            this.middleValue = middleValue;
            this.rightKey = rightKey;
            this.rightValue = rightValue;
            this.left = left;
            this.middleLeft = middleLeft;
            this.middleRight = middleRight;
            this.right = right;
        }

        private Node(K leftKey, V leftValue, K middleKey, V middleValue, K rightKey, V rightValue, Node<K, V> left, Node<K, V> middle, Node<K, V> right) {
            this.leftKey = leftKey;
            this.leftValue = leftValue;
            this.middleKey = middleKey;
            this.middleValue = middleValue;
            this.rightKey = rightKey;
            this.rightValue = rightValue;
            this.left = left;
            this.middleLeft = middle;
            this.right = right;
        }

        Node(K leftKey, V leftValue, K middleKey, V middleValue, K rightKey, V rightValue) {
            this(leftKey, leftValue, middleKey, middleValue, rightKey, rightValue, null, null, null);
        }

        /**
         * leftKey, null
         * /      |     \
         * left   null   right
         *
         * @param leftKey
         * @param leftValue
         * @param left
         * @param right
         */
        Node(K leftKey, V leftValue, Node<K, V> left, Node<K, V> right) {
            this(leftKey, leftValue, null, null, null, null, left, null, right);
        }

        /**
         * k:v
         * /    \
         * null  null
         *
         * @param leftKey
         * @param leftValue
         */
        Node(K leftKey, V leftValue) {
            this(leftKey, leftValue, null, null, null, null, null, null, null);
        }
    }

    private int size;

    private Node<K, V> root;

    TwoThreeTree() {
        this.size = 0;
        this.root = null;
    }

    public V get(K key) {
        return search(root, key);
    }

    private V search(Node<K, V> node, K key) {
        if (node == null) {
            return null;
        }

        if (isTwoNode(node)) {
            if (key.compareTo(node.leftKey) < 0) {
                return search(node.left, key);
            } else if (key.compareTo(node.leftKey) > 0) {
                return search(node.right, key);
            }

            return node.leftValue;

        } else {

            if (key.compareTo(node.leftKey) < 0) {
                return search(node.left, key);
            } else if (key.compareTo(node.rightKey) > 0) {
                return search(node.right, key);
            } else if (key.compareTo(node.leftKey) > 0 && key.compareTo(node.rightKey) < 0) {
                return search(node.middleLeft, key);
            }

            if (node.leftKey == key) {
                return node.leftValue;
            }

            return node.rightValue;
        }
    }

    public void add(K key, V value) {
        root = add(root, key, value);
        // 分解根节点
        if (isFourNode(root)) {
            Node<K, V> left = new Node<K, V>(root.leftKey, root.leftValue, root.left, root.middleLeft);
            Node<K, V> right = new Node<K, V>(root.rightKey, root.rightValue, root.middleRight, root.right);
            root = new Node<K, V>(root.middleKey, root.middleValue, left, right);
        }
    }

    private Node<K, V> add(Node<K, V> node, K key, V value) {
        if (node == null) {
            size++;
            return new Node<>(key, value);
        }

        if (isLeaf(node)) {
            merge(node, key, value);
            size++;
            return node;
        } else {
            // 非叶子节点2节点
            if (isTwoNode(node)) {
                // key < node.leftKey
                if (key.compareTo(node.leftKey) < 0) {
                    Node<K, V> newRoot = add(node.left, key, value);

                    // 添加后返回的新节点是 2-node 或者 3-node 直接挂接在树上
                    if (isTwoNode(newRoot) || isThreeNode(newRoot)) {
                        node.left = newRoot;
                        return node;
                    }

                    // 添加后返回的新节点是一个临时的 4-node, 当前根节点是一个2-node,
                    if (isFourNode(newRoot) && isTwoNode(node)) {
                        return split42NodeLeft(newRoot, node);
                    }

                    // 添加后返回的新节点是一个临时的 4-node, 当前根节点是一个3-node,
                    if (isFourNode(newRoot) && isThreeNode(node)) {
                        return split43NodeLeft(newRoot, node);
                    }
                }

                // key > node.leftKey
                if (key.compareTo(node.leftKey) > 0) {
                    Node<K, V> newRoot = add(node.right, key, value);

                    // 添加后返回的新节点是 2-node 或者 3-node 直接挂接在树上
                    if (isTwoNode(newRoot) || isThreeNode(newRoot)) {
                        node.right = newRoot;
                        return node;
                    }

                    // 添加后返回的新节点是一个临时的 4-node, 当前根节点是一个2-node,
                    if (isFourNode(newRoot) && isTwoNode(node)) {
                        return split42NodeRight(newRoot, node);
                    }

                    // 添加后返回的新节点是一个临时的 4-node, 当前根节点是一个3-node,
                    if (isFourNode(newRoot) && isThreeNode(node)) {
                        return split43NodeRight(newRoot, node);
                    }
                }

            } else if (isThreeNode(node)) {

                // node.left < key < node.right
                if (key.compareTo(node.leftKey) > 0 && key.compareTo(node.rightKey) < 0) {
                    Node<K, V> newRoot = add(node.middleLeft, key, value);

                    if (isTwoNode(newRoot) || isThreeNode(newRoot)) {
                        node.middleLeft = newRoot;
                        return node;
                    }

                    if (isFourNode(newRoot) && isThreeNode(node)) {
                        return split43NodeMiddle(newRoot, node);
                    }
                }

                // key < node.left
                if (key.compareTo(node.leftKey) < 0) {
                    Node<K, V> newRoot = add(node.left, key, value);

                    if (isTwoNode(newRoot) || isThreeNode(newRoot)) {
                        node.left = newRoot;
                        return node;
                    }

                    // 添加后返回的新节点是一个临时的 4-node, 当前根节点是一个3-node,
                    if (isFourNode(newRoot) && isThreeNode(node)) {
                        return split43NodeLeft(newRoot, node);
                    }
                }

                // key > node.right
                if (key.compareTo(node.rightKey) > 0) {
                    Node<K, V> newRoot = add(node.right, key, value);

                    if (isTwoNode(newRoot) || isThreeNode(newRoot)) {
                        node.right = newRoot;
                        return node;
                    }

                    // 添加后返回的新节点是一个临时的 4-node, 当前根节点是一个3-node,
                    if (isFourNode(newRoot) && isThreeNode(node)) {
                        return split43NodeRight(newRoot, node);
                    }
                }
            }
        }
        return node;
    }

    public void remove(K key) {

    }

    private Node<K, V> remove(Node<K, V> node, K key) {
        return null;
    }

    /**
     * removeMin 从2-3树中删除最小节点
     */
    public void removeMin() {

    }

    private Node<K, V> removeMin(Node<K, V> node) {
        if (node == null) {
            return null;
        }

        if (isTwoNode(node) && isTwoNode(node.left) && isTwoNode(node.right)) {
            // 合并为一个4-node
            node = new Node<K, V>(node.left.leftKey, node.left.leftValue,
                    node.leftKey, node.leftValue, node.right.leftKey, node.right.leftValue,
                    node.left.left, node.left.right, node.right.left, node.right.right);

            // 向下变换
            node.left = removeMin(node.left);

        } else {

            if (!isTwoNode(node.left)) {
                node.left = removeMin(node.left);

            } else {

                // 父节点是2-node 右边兄弟节点不是2-node
                if (isTwoNode(node) && isThreeNode(node.right)) {
                    // 构造新的左节点, 新的左节点是一个3-node
                    // 将右节点合并过来
                    Node<K, V> left = new Node<K, V>(node.left.leftKey, node.left.leftValue, null, null, node.leftKey, node.leftValue,
                            node.left.left, node.left.right, node.right.left);

                    // 构造新的右节点, 新的右节点是一个2-node
                    Node<K, V> right = new Node<K, V>(node.right.rightKey, node.right.rightValue, null, null, null, null,
                            node.right.middleLeft, null, node.right.right);

                    // 替换根节点为右节点中最小键
                    node.leftKey = node.right.leftKey;
                    node.leftValue = node.right.leftValue;

                    node.left = left;
                    node.right = right;

                    // 向下变换
                    node.left = removeMin(node.left);
                }

                // 父节点是3-node
                if (isThreeNode(node)) {
                    // 中间兄弟节点不是2-node
                    if (!isTwoNode(node.middleLeft)) {



                        // 右边兄弟节点不是2-node
                    } else if (!isTwoNode(node.right)) {

                        // 所有兄弟节点都是2-node
                    } else {

                    }
                }

                // 父节点是4-node
                if (isFourNode(node)) {

                }
            }
        }






        // 根节点是2-node
        if (isTwoNode(node)) {

            // case1: 左右节点也是2-node
            if (isTwoNode(node.left) && isTwoNode(node.right)) {
                // 合并为一个4-node
                node = new Node<K, V>(node.left.leftKey, node.left.leftValue,
                        node.leftKey, node.leftValue, node.right.leftKey, node.right.leftValue,
                        node.left.left, node.left.right, node.right.left, node.right.right);

                // 向下变换
                node.left = removeMin(node.left);

                // case2: 左节点是2-node, 右节点不是2-node
            } else if (isTwoNode(node.left) && !isTwoNode(node.right)) {

                K rootKey = node.leftKey;
                V rootValue = node.leftValue;

                // 替换根节点为右节点中最小键
                node.leftKey = node.right.leftKey;
                node.leftValue = node.right.leftValue;

                // 从右节点中删除这个最小键
                node.right.leftKey = node.right.rightKey;
                node.right.leftValue = node.right.rightValue;
                node.right.rightKey = null;
                node.right.rightValue = null;

                // 原根节点和左子节点合并
                node.left.rightKey = rootKey;
                node.left.rightValue = rootValue;

                // 向下变换
                node.left = removeMin(node.left);

                // case3: 左节点不是2-node
            } else {

                // 向下变换
                node.left = removeMin(node.left);
            }


            // 根节点不是2-node
        } else {


            // 根节点是3-node
            if (isThreeNode(node)) {
                // case1: 左子节点是2-node, 中间节点和右节点是2-node
                if (isTwoNode(node.left) && isTwoNode(node.middleLeft) && isTwoNode(node.right)) {

                }

                // case2: 左子节点是2-node, 中间是2-node节点, 右节点不是2-node
                if (isTwoNode(node.left) && isTwoNode(node.middleLeft) && !isTwoNode(node.right)) {

                }

                // case3:

            }


            // case2: 左子节点是2-节点而它的兄弟节点不是2-节点
            if (isTwoNode(node.left) && !isTwoNode(node.right)) {

            }

        }


        if (!isTwoNode(node.left)) {

        }

        if (node.left != null) {
            node.left = removeMin(node.left);

        }
    }

    private Node<K, V> split42NodeLeft(Node<K, V> node4, Node<K, V> node2) {
        // 分离4-node
        node4 = split4Node(node4);

        // 合并2-node和分离后的4-node为3-node
        merge42Node(node2, node4);

        // 调整3-node的子节点
        node2.left = node4.left;
        node2.middleLeft = node4.right;
        return node2;
    }

    private Node<K, V> split42NodeRight(Node<K, V> node4, Node<K, V> node2) {
        // 分离4-node
        node4 = split4Node(node4);

        // 合并2-node和分离后的4-node为3-node
        merge42Node(node2, node4);

        // 调整3-node的子节点
        node2.middleLeft = node4.left;
        node2.right = node4.right;
        return node2;
    }

    private Node<K, V> split43NodeLeft(Node<K, V> node4, Node<K, V> node3) {
        node4 = split4Node(node4);

        merge43Node(node4, node3);

        // 调整4-node的子节点
        node3.left = node4.left;
        node3.middleRight = node3.middleLeft;
        node3.middleLeft = node4.right;
        return node3;
    }

    private Node<K, V> split43NodeMiddle(Node<K, V> node4, Node<K, V> node3) {
        // 让4-node中键和3-node合并成为4-node
        node4 = split4Node(node4);

        merge43Node(node4, node3);

        // 调整4-node的子节点
        node3.middleLeft = node4.left;
        node3.middleRight = node4.right;
        return node3;
    }

    private Node<K, V> split43NodeRight(Node<K, V> node4, Node<K, V> node3) {
        node4 = split4Node(node4);

        merge43Node(node4, node3);

        // 调整4-node的子节点
        node3.middleRight = node4.left;
        node3.right = node4.right;
        return node3;
    }

    // splitFourNode 分离4-node为一个2-node
    private Node<K, V> split4Node(Node<K, V> node4) {
        K parentKey, leftKey, rightKey;
        V parentValue, leftValue, rightValue;

        if (node4.middleKey.compareTo(node4.leftKey) < 0) {
            parentKey = node4.leftKey;
            parentValue = node4.leftValue;
            leftKey = node4.middleKey;
            leftValue = node4.middleValue;
            rightKey = node4.rightKey;
            rightValue = node4.rightValue;

        } else if (node4.middleKey.compareTo(node4.rightKey) > 0) {
            parentKey = node4.rightKey;
            parentValue = node4.rightValue;
            leftKey = node4.leftKey;
            leftValue = node4.leftValue;
            rightKey = node4.middleKey;
            rightValue = node4.middleValue;
        } else {
            parentKey = node4.middleKey;
            parentValue = node4.middleValue;
            leftKey = node4.leftKey;
            leftValue = node4.leftValue;
            rightKey = node4.rightKey;
            rightValue = node4.rightValue;
        }

        return new Node<K, V>(parentKey, parentValue,
                new Node<K, V>(leftKey, leftValue, node4.left, node4.middleLeft),
                new Node<K, V>(rightKey, rightValue, node4.middleRight, node4.right));
    }

    // merge42Node 合并2-node和4-node为一个3-node
    private void merge42Node(Node<K, V> node2, Node<K, V> node4) {
        if (node4.leftKey.compareTo(node2.leftKey) < 0) { // key < node.key1
            node2.rightKey = node2.leftKey;
            node2.rightValue = node2.leftValue;
            node2.leftKey = node4.leftKey;
            node2.leftValue = node4.leftValue;
        } else if (node4.leftKey.compareTo(node2.leftKey) > 0) { // key > node.key1
            node2.rightKey = node4.leftKey;
            node2.rightValue = node4.leftValue;
        }
    }

    // merge43Node 合并一个4-node和一个3-node为一个4-node
    private void merge43Node(Node<K, V> node4, Node<K, V> node3) {
        if (node4.leftKey.compareTo(node3.leftKey) > 0 && node4.leftKey.compareTo(node3.rightKey) < 0) {
            node3.middleKey = node4.leftKey;
            node3.middleValue = node4.leftValue;
        } else if (node4.leftKey.compareTo(node3.leftKey) < 0) {
            node3.middleKey = node3.leftKey;
            node3.middleValue = node3.leftValue;
            node3.leftKey = node4.leftKey;
            node3.leftValue = node4.leftValue;
        } else if (node4.leftKey.compareTo(node3.rightKey) > 0) {
            node3.middleKey = node3.rightKey;
            node3.middleValue = node3.rightValue;
            node3.rightKey = node4.leftKey;
            node3.rightValue = node4.leftValue;
        }
    }

    private void merge(Node<K, V> node, K key, V value) {
        if (isTwoNode(node)) {
            if (key.compareTo(node.leftKey) < 0) { // key < node.key1
                node.rightKey = node.leftKey;
                node.rightValue = node.leftValue;
                node.leftKey = key;
                node.leftValue = value;
            } else if (key.compareTo(node.leftKey) > 0) { // key > node.key1
                node.rightKey = key;
                node.rightValue = value;
            }
        } else if (isThreeNode(node)) {
            if (key.compareTo(node.leftKey) > 0 && key.compareTo(node.rightKey) < 0) {
                node.middleKey = key;
                node.middleValue = value;
            } else if (key.compareTo(node.leftKey) < 0) {
                node.middleKey = node.leftKey;
                node.middleValue = node.leftValue;
                node.leftKey = key;
                node.leftValue = value;
            } else if (key.compareTo(node.rightKey) > 0) {
                node.middleKey = node.rightKey;
                node.middleValue = node.rightValue;
                node.rightKey = key;
                node.rightValue = value;
            }
        }
    }

    // isLeaf 判断节点是否为叶子节点
    private boolean isLeaf(Node<K, V> node) {
        return node.left == null && node.right == null;
    }

    // isTwoNode 判断节点是否是2-node
    private boolean isTwoNode(Node<K, V> node) {
        return node.leftKey != null && node.rightKey == null && node.middleKey == null;
    }

    // isThreeNde 判断节点是否是3-node
    private boolean isThreeNode(Node<K, V> node) {
        return node.leftKey != null && node.rightKey != null && node.middleKey == null;
    }

    // isFourNode 判断节点是否是4-node
    private boolean isFourNode(Node<K, V> node) {
        return node.leftKey != null && node.rightKey != null && node.middleKey != null;
    }

    public boolean is23Tree() {
        return is23Tree(root);
    }

    private boolean is23Tree(Node<K, V> node) {
        if (node != null) {
            if (isTwoNode(node)) {
                int leftHeight = leftHeight(node.left, 0);
                int rightHeight = rightHeight(node.right, 0);


                if (leftHeight != rightHeight) {
                    return false;
                }

                return is23Tree(node.left) && is23Tree(node.right);
            } else {
                return is23Tree(node.left) && is23Tree(node.middleLeft) && is23Tree(node.right);
            }
        }

        return true;
    }

    private int leftHeight(Node<K, V> node, int height) {
        if (node == null) {
            return height;
        }

        return leftHeight(node.left, height + 1);
    }

    private int rightHeight(Node<K, V> node, int height) {
        if (node == null) {
            return height;
        }
        return rightHeight(node.right, height + 1);
    }

    public int size() {
        return size;
    }

    public static void main(String[] args) {
        TwoThreeTree<Integer, Integer> tree23 = new TwoThreeTree<>();
        tree23.add(18, 18);
        tree23.add(20, 20);
        tree23.add(10, 10);
        tree23.add(15, 15);
        tree23.add(6, 6);
        tree23.add(7, 7);
        tree23.add(8, 8);
        tree23.add(25, 25);
        tree23.add(40, 40);
        tree23.add(28, 28);
        tree23.add(66, 66);
        tree23.add(32, 32);
        tree23.add(51, 51);
        tree23.add(17, 17);
        tree23.add(23, 23);
        tree23.add(13, 13);
        tree23.add(24, 1);
        System.out.println(tree23.size());
    }
}

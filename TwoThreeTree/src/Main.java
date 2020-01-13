public class Main {
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

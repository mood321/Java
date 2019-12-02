package algorithm.advanced05;

/**
 * @author mood321
 * @date 2019/12/3 0:14
 * @email 371428187@qq.com
 */
public class Code_05_Max_EOR {

    public static int getEOR1(int[] arr) {       // 暴力
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            for (int start = 0; start <= i; start++) {
                int res = 0;
                for (int k = start; k < start; k++) {
                    res ^= arr[k];
                }
                max = Math.max(max, res);
            }
        }
        return max;
    }

    public static int getEOR2(int[] arr) {     // dp
        int max = Integer.MIN_VALUE;
        int dp[]= new int[arr.length];
        int eor = 0;
        for (int i = 0; i < arr.length; i++) {

            eor^=arr[i];
            max = Math.max(max, eor);
            for (int start =1; start <= i; start++) {
                   int newEor=eor^dp[start-1];
                    max=Math.max(newEor,max);
            }
            dp[i]=eor;
        }
        return max;
    }

    public static class Node {
        public Node[] nexts = new Node[2];
    }

    public static class NumTrie {
        public Node head = new Node();

        public void add(int num) {
            Node cur = head;
            for (int move = 31; move >= 0; move--) {
                int path = ((num >> move) & 1);    // 得到 0,1 
                cur.nexts[path] = cur.nexts[path] == null ? new Node() : cur.nexts[path];   // 没有就新建 
                cur = cur.nexts[path];     // 来的下一个
            }
        }

        public int maxXor(int num) {    //  num 是 0-i  的异或结果  选出一个最优返回
            Node cur = head;
            int res = 0;
            for (int move = 31; move >= 0; move--) {
                int path = (num >> move) & 1;
                int best = move == 31 ? path : (path ^ 1);      // 在符号位时  和它本身一致 最大  其他位不一致最大
                best = cur.nexts[best] != null ? best : (best ^ 1);    // 上面选出期待的值 如果没有 就走有的那条路
                res |= (path ^ best) << move;    // 把选出来的值 左移到相应的位  用| 填到res 里面
                cur = cur.nexts[best];  // 来到下一位
            }
            return res;
        }

    }

    public static int maxXorSubarray(int[] arr) {      // 前缀树
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        int eor = 0;
        NumTrie numTrie = new NumTrie();
        numTrie.add(0);
        for (int i = 0; i < arr.length; i++) {
            eor ^= arr[i];
            max = Math.max(max, numTrie.maxXor(eor));
            numTrie.add(eor);
        }
        return max;
    }

    // for test
    public static int comparator(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            int eor = 0;
            for (int j = i; j < arr.length; j++) {
                eor ^= arr[j];
                max = Math.max(max, eor);
            }
        }
        return max;
    }

    // for test
    public static int[] generateRandomArray(int maxSize, int maxValue) {
        int[] arr = new int[(int) ((maxSize + 1) * Math.random())];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (int) ((maxValue + 1) * Math.random()) - (int) (maxValue * Math.random());
        }
        return arr;
    }

    // for test
    public static void printArray(int[] arr) {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    // for test
    public static void main(String[] args) {
        int testTime = 500000;
        int maxSize = 30;
        int maxValue = 50;
        boolean succeed = true;
        for (int i = 0; i < testTime; i++) {
            int[] arr = generateRandomArray(maxSize, maxValue);
            int res = maxXorSubarray(arr);
            int comp = comparator(arr);
            if (res != comp) {
                succeed = false;
                printArray(arr);
                System.out.println(res);
                System.out.println(comp);
                break;
            }
        }
        System.out.println(succeed ? "Nice!" : "Fucking fucked!");
    }
}
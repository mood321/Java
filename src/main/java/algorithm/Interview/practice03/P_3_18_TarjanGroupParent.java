package algorithm.Interview.practice03;

/**
 * @author mood321
 * @date 2020/5/26 0:30
 * @email 371428187@qq.com
 */
public class P_3_18_TarjanGroupParent {
    public  static class Node{
        public  int value;
        public Node left;
        public Node right;

        public Node(int data)
        {
            this.value=data;
        }
    }

    /**
     * 包装类
     */
    public static class Query{
          Node o1;
          Node o2;

        public Query(Node o1, Node o2) {
            this.o1 = o1;
            this.o2 = o2;
        }
    }

    
}
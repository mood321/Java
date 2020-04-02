package algorithm.Interview.practice02;

/**
 * @author mood321
 * @date 2020/4/3 0:35
 * @email 371428187@qq.com
 */
public class P_2_16_RemoveNodeWired {
    public static  class Node {
        Integer data;
        Node next;

        public Node(Integer data) {
            this.data = data;
        }
    }

    public  static  void removeNodeEired(Node node){
        if(node ==null){
            return;
        }
        Node next = node.next;
        if(next ==null){ // 这种方法不能删除最后一个节点
                throw new RuntimeException("can not delete last node");
        }
        node.data=next.data;
        node.next=next.next;

    }
}
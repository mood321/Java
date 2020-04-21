package algorithm.Interview.practice03;

import java.util.HashMap;

/**
 * @author mood321
 * @date 2020/4/22 0:11
 * @email 371428187@qq.com
 */
public class P_3_5_MaxLength {
    public static class Node {

        Integer data;
        Node left;
        Node right;

        public Node(Integer value) {
            data = value;
        }
    }
    //
    public static int  getMaxLength(Node head,int num){
       if(head ==null) {
           return 0;
       }

        HashMap<Integer, Integer> map = new HashMap<>();  //key代表出现过的累加和，value代表这个累加和上次在哪一层出现的
        map.put(0, 0);   //一定要有，代表累加和为0的最早在0层就出现
        int max = preOrder(head, 0, num, map, 0, 1);
        System.out.println(max);
        return max;
    }

    private static int preOrder(Node head, int preSum, int sum, HashMap<Integer, Integer> map, int maxLength, int level) {
          if(head ==null){
              return maxLength;
          }
          int curSum=preSum+head.data;
          if(!map.containsKey(curSum)){
              map.put(curSum,level);
          }
           if(map.containsKey(curSum-sum)){
               maxLength=Math.max(maxLength,level-map.get(curSum-sum));
           }
        maxLength = preOrder(head.left, curSum, sum, map, maxLength, level + 1);
        maxLength = preOrder(head.right, curSum, sum, map, maxLength, level + 1);
         if( map.get(curSum)==level){      // map 里面存的都是上面节点到cur的 累计和,  但到这儿,如果相等 和累加和就没关系了
                                            // 需要处理
             map.remove(curSum);
         }
         return maxLength;
    }

    /**
     *  数组实现 
     */
      public static  int max_length=0;
    public static void maxLength(int[][] nodes, int root, int target, HashMap<Integer, Integer> map, int preSum, int level){
        if(root == 0)
            return;
        int curSum = preSum + nodes[root-1][3];
        //当之前还没有出现过累加和为curSum的时候，加入map
        if(!map.containsKey(curSum)){
            map.put(curSum, level);
        }
        //比如说level为5时，curSum = 13,target = 5，那么就要看之前有没有和为8的出现
        //发现了level为2时，累加和为8
        //那么这里的max_length就可以试着更新为5-2 = 3.
        //之所以这么说，是因为有个关系一直存在，target = curSum - preSum.这是我们追求的
        //我们往map里加的都是preSum,所以就找curSum - target，使得追求的东西被实现
        if(map.containsKey(curSum - target)){
            max_length = Math.max(max_length, level - map.get(curSum - target));
        }
        //继续查找左右子树
        maxLength(nodes, nodes[root-1][1], target, map, curSum, level+1);
        maxLength(nodes, nodes[root-1][2], target, map, curSum, level+1);
//我们在map中存放的是上层所有的和，以及他们对应的level，
// 而如果在map中发现了curSum，且它的level不是上层，而且是当前这一层，那么说明curSum这个累加和的记录是在遍历到cur的时候加上去的
//那么就需要将这个记录删除掉，免得后面使用它。因为它并不是代表的上层的多少个数的和值。

        if(map.get(curSum) == level)
            map.remove(curSum);
    }
}
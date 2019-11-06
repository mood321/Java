package algorithm.basic05;

import java.util.HashMap;
import java.util.List;

/**
 * @Created by mood321
 * @Date 2019/11/7 0007
 * @Description TODO
 */
public class Code_04_UnionFind {

    public static class UnionFindSet<K> {
        public HashMap<K, K> fatherMap; // 本身元素和父节点
        public HashMap<K, Integer> sizeMap;//节点 和节点个数

        public UnionFindSet() {
            fatherMap = new HashMap<K, K>();
            sizeMap = new HashMap<K, Integer>();
        }

        public void makeSets(List<K> nodes) {
            fatherMap.clear();
            sizeMap.clear();
            for (K node : nodes) {
                fatherMap.put(node, node);
                sizeMap.put(node, 1);
            }
        }

        private K findHead(K node) {
            K father = fatherMap.get(node);
            if (father != node) {
                father = findHead(father);
            }
            fatherMap.put(node, father);
            return father;
        }

        public boolean isSameSet(K a, K b) {
            return findHead(a) == findHead(b);
        }

        public void union(K a, K b) {
            if (a == null || b == null) {
                return;
            }
            K aHead = findHead(a);
            K bHead = findHead(b);
            if (aHead != bHead) {
                int aSetSize= sizeMap.get(aHead);
                int bSetSize = sizeMap.get(bHead);
                if (aSetSize <= bSetSize) {
                    fatherMap.put(aHead, bHead);
                    sizeMap.put(bHead, aSetSize + bSetSize);
                } else {
                    fatherMap.put(bHead, aHead);
                    sizeMap.put(aHead, aSetSize + bSetSize);
                }
            }
        }

    }
}

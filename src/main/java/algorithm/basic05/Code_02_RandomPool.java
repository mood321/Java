package algorithm.basic05;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @Created by mood321
 * @Date 2019/11/6 0006
 * @Description TODO
 */
public class Code_02_RandomPool {

    public static class Pool<K> {
        private HashMap<K, Integer> keyIndexMap;
        private HashMap<Integer, K> indexKeyMap;
        private int size;

        public Pool() {
            this.keyIndexMap = new HashMap<K, Integer>();
            this.indexKeyMap = new HashMap<Integer, K>();
            this.size = 0;
        }

        public void insert(K key) {
            if(!keyIndexMap.containsKey(key)){
                keyIndexMap.put(key,this.size);
                indexKeyMap.put(this.size++,key);
            }
        }

        /**
         *  这里删除要 考虑他原来size的问题
         * @param key
         */
        public void delete(K key) {
            if(this.keyIndexMap.containsKey(key)){
                Integer oldIndex = this.keyIndexMap.get(key);// 要删除的下标
                K k = this.indexKeyMap.get(--this.size);// 原来最后最后一个值
                keyIndexMap.put(k,oldIndex);//
                indexKeyMap.put(oldIndex,k);//
                keyIndexMap.remove(key); //

            }

        }

        public K getRandom() {
            if (this.size == 0) {
                return null;
            }
            int randomIndex = (int) (Math.random() * this.size); // 0 ~ size -1
            return this.indexKeyMap.get(randomIndex);
        }

    }

    public static void main(String[] args) {
        Pool<String> pool = new Pool<String>();
        pool.insert("1");
        pool.insert("2");
        pool.insert("3");
        pool.delete("1");
        System.out.println(pool.keyIndexMap.get("1"));
        pool.insert("1");
        System.out.println(pool.keyIndexMap.get("1"));
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());
        System.out.println(pool.getRandom());


    }
}

package algorithm.basic07;

/**
 * @Created by mood321
 * @Date 2019/11/8 0008
 * @Description TODO
 */
public class Code_01_TrieTree {

    public static class TrieNode {
        public int path;// 拥有节点个数
        public int end;//尾节点个数
        public TrieNode[] nexts;// 子节点 因为字符字母 可以用数组 也可以用map

        public TrieNode() {
            nexts = new TrieNode[26];
        }

    }

    public static class Trie {
        private TrieNode root;

        public Trie() {
            root = new TrieNode();
        }

        // 添加
        public void insert(String word) {
            if (word == null)
                return;
            char[] chars = word.toCharArray();
            TrieNode node = root;
            for (int i = 0; i < chars.length; i++) {
                int i1 = chars[i] - 'a';
                if (node.nexts[i1] == null)
                    node.nexts[i1] = new TrieNode();
                node = node.nexts[i1];
                node.path++;
            }
            node.end++;
        }

        // 查找
        public int search(String word) {
            if(word==null)
                return 0;
            char[] chars = word.toCharArray();
            int index=0;
            TrieNode node = this.root;
            for (int i = 0; i < chars.length; i++) {
                index=chars[i]-'a';
                if(node.nexts[index]==null)
                    return 0;
                node=node.nexts[index];
            }
            return node.end;
        }
        public void delete(String word) {
            if(search(word)>0){
                char[] chars = word.toCharArray();
                TrieNode node = this.root;
                int index=0;
                for (int i = 0; i < chars.length; i++) {
                    index=chars[i]-'a';
                    if(0==node.nexts[index].path) {
                        node.nexts[index] = null;
                        return;
                    }
                    node=node.nexts[index];

                }
            node.end--;
            }
        }
        public int prefixNumber(String pre) {
            if(pre==null)
                return  0;
            char[] chars = pre.toCharArray();
            TrieNode node = this.root;
            int index=0;
            for (int i = 0; i < chars.length; i++) {
                index = chars[i] - 'a';

                if (node.nexts[index]==null) {
                    return 0;
                }
                node=node.nexts[index];
            }
            return node.path;
        }
    }
    public static void main(String[] args) {
        Trie trie = new Trie();
        System.out.println(trie.search("zuo"));
        trie.insert("zuo");
        System.out.println(trie.search("zuo"));
        trie.delete("zuo");
        System.out.println(trie.search("zuo"));
        trie.insert("zuo");
        trie.insert("zuo");
        trie.delete("zuo");
        System.out.println(trie.search("zuo"));
        trie.delete("zuo");
        System.out.println(trie.search("zuo"));
        trie.insert("zuoa");
        trie.insert("zuoac");
        trie.insert("zuoab");
        trie.insert("zuoad");
        trie.delete("zuoa");
        System.out.println(trie.search("zuoa"));
        System.out.println(trie.prefixNumber("zuo"));

    }
}

package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/8 0:32
 * @email 371428187@qq.com
 */
public class P_4_6_Hanoi {
    public static void hanoi1(int n) {
        if (n > 0) {
            func("from", "mid", "to", n);
        }
    }

    private static void func(String from, String mid, String to, int n) {
        if (n == 1) {
            System.out.println("move from " + from + " To " + to);
        } else {                           // 这是个递归
            func(from,to,mid,n-1); // 从左 移动一个到中
            func(from,mid,to,1); // 把from 最下层的  一过去
            func(mid,from,to,n-1); //  这儿要移到 有上,层级方便计算
        }
    }
    // 进阶汉诺塔    可以移到中 不可以右
    public static void Hanoi02(int num) {
        if (num > 0) {
            System.out.println(func2(num, num, "from", "mid", "to"));
        }
    }

    private static int func2(int h, int index, String from, String mid, String to) {
        // 这种放方法 毫无疑问 上面的 集中不能集中在 中  而是集中右
        /////////////////////////////////////////////////////////////
        //
        //
        //                                  1
        //                                  2
        //              4                   3
        //             ---                -----                -----
        ////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        //
        //                                                       1
        //                                                       2
        //              4                                        3
        //             ---                -----                -----
        ////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////
        //
        //              1
        //              2
        //              3                                         4
        //             ---                -----                -----
        ////////////////////////////////////////////////////////////////
        // 过度应该是这样的
        if (h == 1) {
            if ("mid".equals(from)) {
                System.out.println(index + " 从 " + from + "  移动到了 " + to);
                return 1;
            } else {
                System.out.println(index + " 从 " + from + "  移动到了 " + mid); // 无法左右移  必须进过中间
                System.out.println(index + " 从 " + mid + "  移动到了 " + to);
                return 2;
            }
        } else {
            if ("mid".equals(from) || "mid".equals(to)) { // 如果 中间移两边  或者两边移中间 随便移
                // String newTo = "mid".equals(from) ? to : mid;
                int p1 = func2(h - 1, index - 1, from, mid, to);   // 1 上面的都去 右
                int p2 = func2(1, index, from, to, mid);// 这个参数 to..   2 自己移过去
                int p3 = func2(h - 1, index - 1, to, from, mid); // 3 移到自己上面
                return p1 + p2 + p3;
            } else {  //  这就是上面   1
                int p1 = func2(h - 1, index - 1, from, mid, to);   //   现将上面的 移动到to
                System.out.println(index + " 从 " + from + "  移动到了 " + mid);   // 自己来到中间
                int p2 = func2(h - 1, index - 1, to, mid, from);    // 将移到to上的  移到from上面
                System.out.println(index + " 从 " + mid + "  移动到了 " + to);    // 自己来到to
                int p3 = func2(h - 1, index - 1, from, mid, to);            // 把移动from的 n-1 移到自己上面

                return p1 + p2 + p3 + 2;
               /*  func2(h - 1, index - 1, from, to, mid);   //
                 func2(h - 1, index - 1, mid, from, to);   //
                 System.out.println(index + " 从 " + from + "  移动到了 " + mid);
                 func2(h - 1, index - 1, to, from, mid);
                 func2(h - 1, index - 1, mid, to, from);
                 System.out.println(index + " 从 " + mid + "  移动到了 " + to);
                 func2(h - 1, index - 1, from, to, mid);
                 func2(h - 1, index - 1, mid, from, to);*/
            }
        }
    }

}
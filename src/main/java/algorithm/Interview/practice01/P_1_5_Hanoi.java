package algorithm.Interview.practice01;


import java.util.Stack;

/**
 * @author mood321
 * @date 2019/12/25 22:28
 * @email 371428187@qq.com
 */
public class P_1_5_Hanoi {
    // 普通汉诺塔    可以移到中 可以右
    public static void Hanoi01(int num) {
        if (num > 0) {
            func(num, num, "from", "mid", "to");
        }
    }

    private static void func(int h, int index, String from, String mid, String to) {      //
        if (h == 1) {
            System.out.println(index + " 从 " + from + "  移动到了 " + to);
        } else {
            func(h - 1, index - 1, from, to, mid);      // 思路 先将上面的无论多少个  从from 移动到中间
            func(1, index, from, mid, to);   // 把自己移动到右  这也可以直接输出
            func(h - 1, index - 1, mid, from, to);     // 把移动到中间的  移动自己上面去
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

    public static enum Action {
        No, L2M, M2L, M2R, R2M
    }

    public static int hanoiProblem2(int num) {
        Stack<Integer> ls = new Stack<Integer>();
        Stack<Integer> ms = new Stack<Integer>();
        Stack<Integer> rs = new Stack<Integer>();
        ls.push(Integer.MAX_VALUE);
        ms.push(Integer.MAX_VALUE);
        rs.push(Integer.MAX_VALUE);
        for (int i = num; i > 0; i--) {
            ls.push(i);
        }
        Action[] preAction = {Action.No};
        int step = 0;
        while (rs.size() != num + 1) {
            step += fStack2Stack(preAction, Action.M2L, Action.L2M, ls, ms);
            step += fStack2Stack(preAction, Action.L2M, Action.M2L, ms, ls);
            step += fStack2Stack(preAction, Action.R2M, Action.M2R, ms, rs);
            step += fStack2Stack(preAction, Action.M2R, Action.R2M, rs, ms);
        }
        return step;
    }

    public static int fStack2Stack(Action[] preAction, Action noAction, Action curAction,
                                   Stack<Integer> fStack, Stack<Integer> tStack) {
        if (preAction[0] == noAction || fStack.peek() >= tStack.peek()) {
            return 0;
        }
        preAction[0] = curAction;
        tStack.push(fStack.pop());
        return 1;
    }

    public static void main(String[] args) {
        // Hanoi01(3);
        Hanoi02(3);
        System.out.println(hanoiProblem2(3));
    }
}
package algorithm.basic08;

/**
 * @author mood321
 * @date 2019/11/12 1:23
 * @email 371428187@qq.com
 *
 *          move 1 from left to right
 *          move 2 from left to mid
 *          move 1 from right to mid
 *          move 3 from left to right
 *          move 1 from mid to left
 *          move 2 from mid to right
 *          move 1 from left to right
 */
public class Code_02_Hanoi {


    public static void hanoi(int n) {
        if (n > 0) {
            func(n, n, "left", "mid", "right");
        }
    }

    public static void func(int rest, int down, String from, String help, String to) {
        if (rest == 1) {
            System.out.println("move " + down + " from " + from + " to " + to);
        } else {
            func(rest - 1, down - 1, from, to, help);
            func(1, down, from, help, to);
            func(rest - 1, down - 1, help, from, to);
        }
    }

    public static void moveLeftToRight(int N) {
        if (N == 1) {
            System.out.println("move 1 from left to right");
        }
        moveLeftToMid(N - 1);
        System.out.println("move " + N + "from left to right");
        moveMidToRight(N - 1);
    }

    public static void moveRightToLeft(int N) {

    }

    public static void moveLeftToMid(int N) {
        if (N == 1) {
            System.out.println("move 1 from left to mid");
        }
        moveLeftToRight(N - 1);
        System.out.println("move " + N + "from left to mid");
        moveRightToMid(N - 1);
    }

    public static void moveMidToLeft(int N) {

    }

    public static void moveRightToMid(int N) {

    }

    public static void moveMidToRight(int N) {
        if (N == 1) {
            System.out.println("move 1 from mid to right");
        }
        moveMidToLeft(N - 1);
        System.out.println("move " + N + "from mid to right");
        moveLeftToRight(N - 1);
    }

    public static void main(String[] args) {
        int n = 3;
        hanoi(n);
    }

}
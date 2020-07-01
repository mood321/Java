package algorithm.Interview.practice04;

/**
 * @author mood321
 * @date 2020/7/2 0:31
 * @email 371428187@qq.com
 */
public class CoinsMethodS {
    /**
     * 经典递归
     */
    public static int conins(int[] arr, int aim) {
        if (arr == null || arr.length == 0 || aim < 0) {
            return 0;
        }
        return porcess(arr, 0, aim);
    }

    private static int porcess(int[] arr, int index, int aim) {
        int res = 0;
        if (index == arr.length) {
            res = aim == 0 ? 1 : 0;
        } else {
            for (int i = 0; arr[index] * i < aim; i++) { // 推测能拿几次
                res += porcess(arr, index + 1, aim - (arr[index] * i));
            }
        }
        return res;
    }

    // 记忆搜索

    /**
     * 存下来  已经求出来的  值的最大,有就直接用,没有再去算
     */
    public static int conins2(int[] arr, int aim) {
        if (arr == null || arr.length == 0 || aim < 0) {
            return 0;
        }
        int[][] map = new int[arr.length][aim + 1];
        return porcess2(arr, 0, aim, map);
    }

    private static int porcess2(int[] arr, int index, int aim, int[][] map) {
        int res = 0;
        if (index == arr.length) {
            res = aim == 0 ? 1 : 0;
        } else {
            int mapV = 0;

            for (int i = 0; arr[index] * i < aim; i++) { // 推测能拿几次
                mapV = map[index + 1][aim - arr[index] * i];
                if (mapV != 0) {
                    res += mapV == -1 ? 0 : mapV;
                } else
                    res += porcess(arr, index + 1, aim - (arr[index] * i));
            }
        }
        map[index][aim] = res == 0 ? -1 : res;
        return res;

    }


}
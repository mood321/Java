package algorithm.advanced01;


import com.sun.deploy.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mood321
 * @date 2019/11/17 23:31
 * @email 371428187@qq.com
 */
public class Manachar {
    public static void main(String[] args) {

        System.out.println(getMaxLcpslength("abcd123321"));
    }

    public static int getMaxLcpslength(String str) {
        if (str == null || str.length() == 0)
            return 0;

        char[] manachar = getManachar(str);
        int[] pArr = new int[manachar.length];
        int R = -1;       //
        int intdex = -1;
        int maxLength = Integer.MIN_VALUE;
        for (int i = 0; i < manachar.length; i++) {
            pArr[i] = R > i ? Math.min(pArr[2 * intdex - i], R) : 1; // 区分 是否需要暴力扩
            while (i + pArr[i] < manachar.length && i - pArr[i] > -1) {
                if (manachar[i + pArr[i]] == manachar[i - pArr[i]]) {
                    pArr[i]++;
                } else {
                    break;
                }
                if (i + pArr[i] > R) {
                    R = i + pArr[i];
                    intdex = i;
                }

            }
            maxLength = Math.max(maxLength, pArr[i]);
        }
        return maxLength - 1;
    }

    private static char[] getManachar(String str) {
        char[] chars = str.toCharArray();
        StringBuilder stringBuilder = new StringBuilder("#");
        for (char aChar : chars) {
            stringBuilder.append(aChar).append("#");
        }
        return stringBuilder.toString().toCharArray();

    }


}
package algorithm.basic07;

import java.util.Arrays;
import java.util.Comparator;

/**
 * @author mood321
 * @date 2019/11/11 23:17
 * @email 371428187@qq.com
 */
public class Code_06_BestArrange {
    public  static  class Program {
        int start;
        int end;
        public  Program(int start,int end){
            this.start=start;
            this.end=end;
        }
    }

    public  static  class    ProgramComparator implements Comparator<Program>{
        @Override
        public int compare(Program o1, Program o2) {      // 按照每个节目的结束时间排序  最先结束的在最前面
            return o1.end-o2.end;
        }
    }
    public static int bestArrange(Program[] programs, int start) {
        Arrays.sort(programs,new ProgramComparator());            // 排序

        int res=0;
        for (int i = 1; i < programs.length; i++) {
            if(start<=programs[i].start){           // 开始时间在   上一次结束之后
                res++;                               // 次数加
                start=programs[i].end;               // 重新给上一次 结束时间 赋值
            }                                         // 不满足的 都是 开始时间在上一次的结束时间之前
        }
        return res;
    }
}
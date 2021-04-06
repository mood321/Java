package algorithm.Interview.practice05;



public class P_5_9_FindStr {


    /**
     *  因为是有序的数组  所以可以采用二分查找
     * @param sts
     * @param string
     * @return
     */
    public int getIndex(String[] sts,String string)  {

        if(sts ==null || sts.length==0 || string==null){
            return -1;
        }

        int res=-1;
        int left=0;
        int right=sts.length-1;

        int mid=0;
        int i=0;
        while (left<=right){
            mid = (left + right) / 2;
            if(sts[mid] !=null && sts[mid].equals(sts)){
                res=mid;
                // 为了找到第一次出来下标
                right=mid-1;
            }else if(sts[mid] != null){
                // 标准二分处理
                if(sts[mid] .compareTo(string) <0){
                    left=mid+1;
                }  else {
                    right=mid-1;
                }
            }else {
                // 要处理值为null 的情况
                i=mid;
                while (sts[i] == null && --i >=left);
                // 他可能跑到 mid 左边
                if(i <left || sts[i] .compareTo(string) <0){
                    left = mid+1;
                }  else {
                    // 处理边界
                    res=sts[i].equals(string) ? i:res;
                    right=i-1;
                }
            }


        }
        return res;
    }

}
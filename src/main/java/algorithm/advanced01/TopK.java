package algorithm.advanced01;

/**
 * @author mood321
 * @date 2019/11/18 22:34
 * @email 371428187@qq.com
 * @desc 通过BFPRT 找到 第K小的数
 * 
 */
public class TopK {

    /*public static  int getMidian(int[] arr){

        if(arr==null || arr.length==0)
            return 0;

        if(arr.length <5){
            
        }
        
    }*/
    public static int getMinKth(int[] arr, int k){
        if(arr == null || arr.length == 0 || k < 0 || k >= arr.length){
            return Integer.MIN_VALUE;
        }

        //返回从小到大，位于 k-1 位置的数字，就是第 k 大的数
        int res = bfrpt(arr, 0, arr.length - 1, k - 1);
        System.out.println(res);
        return res;
    }

    // 在 left,right 范围上，找到从小到大排序为 p 的数，即为第 p+1 小的数
    public static int bfrpt(int[] arr, int left, int right, int p){
        if(left == right){
            return arr[left];
        }

        // bfrpt算法：选择中位数数组中的中位数来作为基准划分原数组，可以每次确定甩掉 3N/10 的数据量
        int num = medianOfMedians(arr, left, right);
        int[] index = partition(arr, left, right, num);
        if(p >= index[0] && p <= index[1]){
            return arr[p];
        }else if(p < index[0]){
            return bfrpt(arr, left, index[0] - 1, p);
        }else{
            return bfrpt(arr, index[1] + 1, right, p);
        }
    }

    // 根据数num作为基准对数组arr上left到right的范围进行划分（快排/荷兰国旗）
    public static int[] partition(int[] arr, int left, int right, int num){
        int less = left - 1;
        int more = right + 1;
        int cur = left;
        while(cur < more){
            if(arr[cur] < num){
                swap(arr, ++less, cur++);
            }else if(arr[cur] > num){
                swap(arr, --more, cur);
            }else{
                cur++;
            }
        }
        return new int[]{less + 1, more - 1};
    }

    // 求中位数数组中的中位数
    public static int medianOfMedians(int[] arr, int left, int right){
        int num = right - left + 1;
        int offset = num % 5 == 0 ? 0 : 1;
        int[] mArr = new int[num / 5 + offset];  // 中位数数组
        int index = 0;
        for(int i = left; i < right; i = i + 5){
            // 从1开始，而不是从0开始
            mArr[index++] = getMedian(arr, i, Math.min(right, i + 4));
        }
        return bfrpt(mArr, 0, mArr.length - 1, mArr.length / 2);
    }

    public static int getMedian(int[] arr, int left, int right){
        insertSort(arr, left, right);
        return arr[(left + right) / 2];
    }

    // 因为只对5个数排序，所以选择插入排序
    public static void insertSort(int[] arr, int left, int right){
        for(int i = left + 1; i <= right; i++){
            // 在前面的有序数组中找到自己的位置
            for(int j = i; j > left; j--){
                if(arr[j - 1] > arr[j]){
                    swap(arr, j - 1, j);
                }else{
                    break;
                }
            }
        }
    }

    public static void swap(int[] arr, int i, int j){
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] arr = {6, 9, 1, 3, 1, 2, 2, 5, 6, 1, 3, 5, 9, 7, 2, 5, 6, 1, 9};
        getMinKth(arr, 5);
    }

}
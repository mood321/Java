package algorithm.Interview.practice08;

/**
 * 令奇数小标为奇数  偶数下标为偶数
 */
public class P_8_14_Swap {

    public void modify(int[] arr){
        if(arr== null || arr.length ==0){
            return;
        }

        // 0 为偶数
        int enev=0;

        int odd=1;

        int length= arr.length-1;

        while(enev <= length || odd <= length){
            
            if((arr[length] & 1) ==0)   {
                swap(arr,length,enev);
                enev+=2;
            }else {
                swap(arr,length,odd);
                odd+=2;
            }
        }

    }

    private void swap(int[] arr, int length, int enev) {
        arr[enev]= arr[length]+arr[enev]- (arr[length]=arr[enev]);
    }
}

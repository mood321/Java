package Test;

import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

@AllArgsConstructor
public class ExcelTest
{
    static Map<String,List<Object>> st = new HashMap<>();
    static Map<String,List<Object>> ss = new HashMap<>();
    static  int  size=0;
    public static void main(String[] args)  throws FileNotFoundException{
       /* List<List<Object>> read =
                new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            ExcelReader reader = ExcelUtil.getReader("F://545分表.xlsx",i);
            read.addAll( reader.read(0));
        }*/
        ExcelReader reader = ExcelUtil.getReader("F://444分表.xlsx",0);
        List<List<Object>> read = reader.read(0);
        print(read.get(0));

        //明细文件
       ExcelReader reader1 = ExcelUtil.getReader("F://收支明细20220506115416_RYBQDT.xlsx");
        List<List<Object>> read1 = reader1.read(0);
        print(read1.get(0));
        for (int i = 0; i < read.size(); i++) {
                eq(read.get(i),read1);
        }

        w(ss.values());
        System.out.println(""+size);
        System.out.println(""+ss.values().size());

        
    }

    private static void eq(List<Object> objects, List<List<Object>> read1) {
        int i=0;
        String jt="";
        if(objects.get(3) !=null ){
            jt= objects.get(3).toString();
        }
        String jm = objects.get(8).toString();
        String jb = objects.get(9).toString();
        String okey = objects.get(2).toString() + jt + objects.get(4).toString() + jm + jb;
        for (List<Object> oo : read1) {
            String ot="";
            if(oo.get(0) !=null ){
                ot= oo.get(0).toString();
            }
            boolean b = Objects.equals(objects.get(2), oo.get(3).toString())
                    && Objects.equals(jt, ot)
                    &&  Objects.equals(objects.get(4).toString(), oo.get(1).toString());


            if(b && oo.get(4).toString().contains(jm) && oo.get(7).toString().contains(jb)){
                if(!ss.containsKey(okey) && !st.containsKey(okey) ){
                    st.put(okey,objects);
                } else if(st.containsKey(okey) ) {
                    st.remove(okey) ;
                    ss.put(okey,objects);
                }   else {
                    System.out.println("已有两次");
                    size++;
                }
                /*if(!st.containsKey(okey) ){
                    st.put(okey,objects);
                } else if(ss.containsKey(okey)) {
                    System.out.println("已有两次");
                    size++;
                }   else {
                    st.remove(okey) ;
                    ss.put(okey,objects);
                }*/
            }

        }

    }

    private static void print(List<Object> objects) {
        for (int i = 0; i < objects.size(); i++) {
            System.out.println("i:  "+i+"  == "+objects.get(i));
        }
        System.out.println(" -----------  ---------- ");
    }

    private static void w(Collection<List<Object>> read) throws FileNotFoundException{
        ExcelWriter writer= ExcelUtil.getWriter(true);
        writer.write(read);
        File file = new File("F:分页44-sheet1.xlsx");
        FileOutputStream out = new FileOutputStream(file);
        writer.flush(out, true);
        writer.close();
        IoUtil.close(out);
    }
}

package imghand;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import java.io.File;
import java.io.IOException;

/**
 * @author mood321
 * @date 2020/5/13 21:28
 * @email 371428187@qq.com
 */
public class Tess4JTest {
    public static void main(String[] args){

        String path = "D://Java//Tess4J";		//我的图片路径

        File file = new File(path + "//photo.jpg");
        ITesseract instance = new Tesseract();

        /**
         *  获取项目根路径，例如： D:
         */
        File directory = new File(path);
        String courseFile = null;
        try {
            courseFile = directory.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //设置训练库的位置
        instance.setDatapath(courseFile + "//tessdata");

        instance.setLanguage("eng");//chi_sim ：简体中文， eng	根据需求选择语言库
        String result = null;
        try {
            long startTime = System.currentTimeMillis();
            result =  instance.doOCR(file);
            long endTime = System.currentTimeMillis();
            System.out.println("Time is：" + (endTime - startTime) + " 毫秒");
        } catch (TesseractException e) {
            e.printStackTrace();
        }

        System.out.println("result: ");
        System.out.println(result);
    }

}
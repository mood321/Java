package Test;

/**
 * @author mood321
 * @date 2021/9/1 22:00
 * @email 371428187@qq.com
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class DocUtil {
    public static String doc2String(File file) throws IOException {
        String result=new String("");
        if(file.getName().toLowerCase().endsWith("docx")){
            return  result=docString(new FileInputStream(file));
        }    else {
            result=doc2String(new FileInputStream(file));

        }
        return result.replace("","");

    }

    /**
     * 读取doc文件内容
     *
     * @param fs
     *            想要读取的文件对象
     * @return 返回文件内容
     * @throws IOException
     */
    public static String doc2String(FileInputStream fs) throws IOException {
        StringBuilder result = new StringBuilder();
        WordExtractor re = new WordExtractor(fs);
        result.append(re.getText());
        re.close();
        return result.toString();
    }
    public static String docString(FileInputStream fis) throws IOException {
        XWPFDocument xdoc = new XWPFDocument(fis);
        XWPFWordExtractor extractor = new XWPFWordExtractor(xdoc);
        String doc1 = extractor.getText();
        fis.close();
        return doc1;
    }

    public static void main(String[] args) {
        File file = new File("");
        try {
            System.out.printf(file.getPath());
            System.out.printf(file.getName());
            File file1 = new File("D:\\2.md");
            String s = doc2String(file);
            System.out.println(s);
            FileOutputStream fos = new FileOutputStream(file1)  ;
            doc2String(file);
            fos.write(s.getBytes());
            fos.close();
            System.out.println("1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
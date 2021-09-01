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

public class DocUtil {
    /**
     * 读取doc文件内容
     *
     * @param file
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

    public static String doc2String(File file) throws IOException {
        return doc2String(new FileInputStream(file));
    }

    public static void main(String[] args) {
        File file = new File("");
        try {
            System.out.printf(file.getPath());
            System.out.printf(file.getName());
            /*File file1 = new File("D:\\1.md");
            FileOutputStream fos = new FileOutputStream(file1);
            fos.write(doc2String(file).getBytes());
            fos.close();
            System.out.println("1");*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
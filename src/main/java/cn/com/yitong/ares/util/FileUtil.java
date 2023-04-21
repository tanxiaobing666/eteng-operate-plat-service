package cn.com.yitong.ares.util;


import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileInputStream;
import javax.imageio.ImageIO;

public class FileUtil {

    public static int[] getImgWH(String imgFilePath) {
        File imgFile = new File(imgFilePath);
        return getImgWH(imgFile);
    }

    public static int[] getImgWH(File imgFile) {
        int[] resultIntArray = null;
        if (!imgFile.exists()) {
            System.err.println("图片文件不存在");
            return resultIntArray;
        } else {
            FileInputStream is = null;

            try {
                is = new FileInputStream(imgFile);
                BufferedImage src = ImageIO.read(is);
                resultIntArray = new int[]{src.getWidth((ImageObserver)null), src.getHeight((ImageObserver)null)};
            } catch (Exception var12) {
                var12.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception var11) {
                    var11.printStackTrace();
                }

            }

            return resultIntArray;
        }
    }


}

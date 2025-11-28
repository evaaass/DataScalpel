package EquiSurface;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CommonMethod {

    public void append2File(String strFile, String strJson){
        File f=new File(strFile);//新建一个文件对象，如果不存在则创建一个该文件
        FileWriter fw;
        try {
            fw = new FileWriter(f);
            fw.write(strJson);//将字符串写入到指定的路径下的文件中
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

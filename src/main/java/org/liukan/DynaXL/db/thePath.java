package org.liukan.DynaXL.db;

import java.io.File;  
import java.net.URL;  
import java.net.URLDecoder;  
  
public class thePath {  
    public static String getPath() { 
    	boolean jar=false;
        URL url = thePath.class.getProtectionDomain().getCodeSource().getLocation();  
        String filePath = null;  
        try {  
            filePath = URLDecoder.decode(url.getPath(), "utf-8");// 转化为utf-8编码  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        if (filePath.endsWith(".jar")) {// 可执行jar包运行的结果里包含".jar"  
            // 截取路径中的jar包名  
            filePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);
            
            jar=true;
        }  
        //System.out.println(filePath);
        //filePath = filePath.substring(0, filePath.lastIndexOf("/")-1 );
        //filePath = filePath.substring(0, filePath.lastIndexOf("/"));
        File file = new File(filePath);  
        //System.out.println(filePath);
        // /If this abstract pathname is already absolute, then the pathname  
        // string is simply returned as if by the getPath method. If this  
        // abstract pathname is the empty abstract pathname then the pathname  
        // string of the current user directory, which is named by the system  
        // property user.dir, is returned.  
        filePath = file.getAbsolutePath();//得到windows下的正确路径         
        return filePath;  
    }  
}  
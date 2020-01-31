package com.tools.automation.support;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileOperation
{
    /**
     * 将上传的文件存储到指定位置
     * @param targetsInputFile
     * @param storePath
     */
    public static void storeFile(MultipartFile targetsInputFile,String storePath)
    {
        //获取文件名
        String fileName=targetsInputFile.getOriginalFilename();
        //获取文件的后缀名
        String suffixName=fileName.substring(fileName.lastIndexOf("."));
        //设置文件存储路径，这里写死了，后期可以配置虚拟路径
        String filePath=storePath;
        String path=filePath+fileName;

        File dest=new File(path);
        //检测是否存在目录
        if(!dest.getParentFile().exists())
        {
            dest.getParentFile().mkdir();  //getParentFile的作用是新建/xx/a.txt，否则则是/xx/a.txt/，会无法创建
        }

        try
        {
            targetsInputFile.transferTo(dest);
            System.out.println("写入成功");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

package com.tools.automation.controller;

import com.tools.automation.support.HttpsUtils;
import com.tools.automation.support.UniversalScan;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class VulScanController
{
    private HttpsUtils httpsUtils=new HttpsUtils();
    private OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    @GetMapping("/universalScan")
    public String universalScan()
    {
        return "universalScan";
    }

    @PostMapping("/universalScan")
    public String universalScan(
            @RequestParam(value = "target",required = false) String target,
            @RequestParam(value = "targetsInputFile",required = false) MultipartFile targetsInputFile,
            @RequestParam(value = "thinkphp5_rce",required = false) String thinkphp5_rce)
    {
        //判断目标提交方式:单个目标验证
        if(target==null||target=="")
        {
            //判断复选框选择情况
            if(thinkphp5_rce=="selected")
            {
                UniversalScan universalScan=new UniversalScan();
                Boolean result=universalScan.thinkPHP_RCE(okHttpClient,target);
                System.out.println(result);
            }
        }
        //判读目标提交方式：从txt文件中读取目标进行验证
        if(targetsInputFile.isEmpty())
        {
            System.out.println("上传的文件内容为空");
            return "universalScan";
        }
        else
        {
            //获取文件名
            String fileName=targetsInputFile.getOriginalFilename();
            //获取文件的后缀名
            String suffixName=fileName.substring(fileName.lastIndexOf("."));
            //设置文件存储路径
            String filePath=Class.class.getClass().getResource("/").getPath();
            String path=filePath+fileName;
            System.out.println(filePath);
            /*
            File dest=new File(path);
            //检测是否存在目录
            if(!dest.getParentFile().exists())
            {
                dest.getParentFile().mkdir();  //新建文件夹
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

             */

        }






        return "universalScan";
    }


}

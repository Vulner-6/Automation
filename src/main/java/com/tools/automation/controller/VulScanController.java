package com.tools.automation.controller;

import com.tools.automation.support.FileOperation;
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
        //读取文件内容，进行批量验证
        else
        {
            FileOperation.storeFile(targetsInputFile,"E:\\Programming\\Projects\\automation\\uploadFiles\\");
        }






        return "universalScan";
    }


}

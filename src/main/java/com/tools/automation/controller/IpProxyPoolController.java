package com.tools.automation.controller;

import com.tools.automation.mapper.IpProxyPoolMapper;
import com.tools.automation.model.IpProxyPool;
import com.tools.automation.support.GetIpProxy;
import com.tools.automation.support.HttpsUtils;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Controller
public class IpProxyPoolController
{
    private HttpsUtils httpsUtils=new HttpsUtils();
    //初始化一个OkHttpClient对象。
    private OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    @Autowired
    private GetIpProxy getIpProxy;

    @Autowired
    private IpProxyPoolMapper ipProxyPoolMapper;

    @GetMapping("ipProxyPool")
    public String ipProxyPool()
    {
        return "ipProxyPool";
    }

    //爬取新的一批IP
    @GetMapping("getNewIpProxy")
    public String getNewIpProxy(Model model)
    {
        //关闭HTTP代理，使用本机IP
        ArrayList<IpProxyPool> ipProxyPoolArrayList =this.getIpProxy.getKuaiDaiLi(this.okHttpClient);
        model.addAttribute("ipProxyArrayList",ipProxyPoolArrayList);
        //插入获取到的ip代理信息到数据库
        for(IpProxyPool ipProxyPool:ipProxyPoolArrayList)
        {
            this.ipProxyPoolMapper.insert(ipProxyPool);
        }
        //获取数据库中可用IP的总数
        Integer sum=this.ipProxyPoolMapper.ipProxyPoolCount();
        model.addAttribute("sum",sum);
        return "ipProxyPool";
    }

    //检验数据库中代理IP的可用性
    @GetMapping("/checkIpProxy")
    public String checkIpProxy(Model model)
    {
        //使用代理挨个访问百度，超时超过5秒的就删除，否则保留。
        //构造GET请求
        Request request=new Request.Builder()
                .url("http://www.mof.gov.cn/index.htm")
                .header("User-Agent", "OkHttp Headers.java")
                .addHeader("Accept", "application/json; q=0.5")
                .addHeader("Accept", "application/vnd.github.v3+json")
                .build();
        //重新设置OkHttpClient的超时时间,最大并发数
        okHttpClient.newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();
        //设置并发数，针对单个主机并发数为64
        okHttpClient.dispatcher().setMaxRequestsPerHost(64);
        //获取数据库中所有代理
        ArrayList<IpProxyPool> ipProxyPoolArrayList=ipProxyPoolMapper.selectAll();

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
                    ipProxyPoolMapper.deleteByIpAddress(ip);
                    System.out.println("连接超时,已经删除IP："+ip.getIpAddress());
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
                            ipProxyPoolMapper.deleteByIpAddress(ip);
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
                }
            });
        }

        //将数据库中内容输出到页面
        ArrayList<IpProxyPool> ipProxyPoolArrayList1=ipProxyPoolMapper.selectAll();
        model.addAttribute("ipProxyArrayList",ipProxyPoolArrayList);
        model.addAttribute("sum",ipProxyPoolMapper.ipProxyPoolCount());
        System.out.println("数据库中目前IP数量："+ipProxyPoolMapper.ipProxyPoolCount());

        return "ipProxyPool";
    }

}

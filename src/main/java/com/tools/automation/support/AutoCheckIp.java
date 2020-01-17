package com.tools.automation.support;

import com.tools.automation.mapper.IpProxyPoolMapper;
import com.tools.automation.model.IpProxyPool;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

public class AutoCheckIp implements Runnable
{
    private ArrayList<IpProxyPool> ipProxyPoolArrayList=new ArrayList<IpProxyPool>();
    //这里的ipProxyMapper必须从外部获取，无法用Autowired注解
    private IpProxyPoolMapper ipProxyPoolMapper;

    private OkHttpClient okHttpClient;
    public void run()
    {
        //线程2
        while (true)
        {
            //定时（1分钟）数据库中读取IP信息
            try
            {
                Thread.sleep(8000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            this.ipProxyPoolArrayList=this.ipProxyPoolMapper.selectAll();
            //多线程请求指定网站，验证数据库中IP是否可用
            Request request=new Request.Builder()
                    .url("http://www.mof.gov.cn/index.htm")
                    .header("User-Agent", "OkHttp Headers.java")
                    .addHeader("Accept", "application/json; q=0.5")
                    .addHeader("Accept", "application/vnd.github.v3+json")
                    .build();
            if(this.ipProxyPoolArrayList.size()>0&&this.ipProxyPoolArrayList!=null)
            {
                for(IpProxyPool ip:this.ipProxyPoolArrayList)
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
                            System.out.println("检测线程--连接超时,已经删除IP："+ip.getIpAddress());
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
                                    System.out.println("检测线程--该IP状态码不是200："+ip.getIpAddress()+":"+ip.getPort());
                                    ipProxyPoolMapper.deleteByIpAddress(ip);
                                }
                                else
                                {
                                    System.out.println("检测线程--可用IP："+ip.getIpAddress()+":"+ip.getPort());
                                }

                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        }

        //不可用，直接在数据库中删除
    }

    public AutoCheckIp(OkHttpClient okHttpClient,IpProxyPoolMapper ipProxyPoolMapper)
    {
        this.okHttpClient=okHttpClient;
        this.ipProxyPoolMapper=ipProxyPoolMapper;
    }
}

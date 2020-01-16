package com.tools.automation.controller;

import com.tools.automation.mapper.IpProxyPoolMapper;
import com.tools.automation.model.IpProxyPool;
import com.tools.automation.support.AutoGetIp;
import com.tools.automation.support.HttpsUtils;
import com.tools.automation.support.IpProxyPoolSupport;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

@Controller
public class IpProxyPoolController
{
    private HttpsUtils httpsUtils=new HttpsUtils();
    //初始化一个OkHttpClient对象。
    private OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    @Autowired
    private IpProxyPoolSupport ipProxyPoolSupport;

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
        ArrayList<IpProxyPool> ipProxyPoolArrayList =this.ipProxyPoolSupport.getKuaiDaiLi(this.okHttpClient);
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
        //使用线程计数器，当计数器为0时，主线程再继续运行
        final CountDownLatch latch = new CountDownLatch(ipProxyPoolMapper.ipProxyPoolCount());
        ipProxyPoolSupport.checkIpProxyPool(okHttpClient,latch);
        try
        {
            //等待上面的计数器变成0，再运行主线程，打印结果
            latch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        //将数据库中内容输出到页面
        ArrayList<IpProxyPool> ipProxyPoolArrayList1=ipProxyPoolMapper.selectAll();
        model.addAttribute("ipProxyArrayList",ipProxyPoolArrayList1);
        model.addAttribute("sum",ipProxyPoolMapper.ipProxyPoolCount());
        System.out.println("数据库中目前IP数量："+ipProxyPoolMapper.ipProxyPoolCount());

        return "ipProxyPool";
    }

    //自动化维护IP池，每次刷新，服务器端都只返回能用的IP
    @GetMapping("/autoManagement")
    public String autoManagement()
    {
        //测试线程1是否好用
        AutoGetIp autoGetIp=new AutoGetIp(this.okHttpClient,this.ipProxyPoolMapper);
        Thread testAutoGetIp=new Thread(autoGetIp);
        testAutoGetIp.start();

        //线程2
        //定时（1分钟）数据库中读取IP信息
        //多线程请求指定网站，验证数据库中IP是否可用
            //不可用，直接删除

        //主线程，每次刷新，都打印数据库中的信息
        return "ipProxyPool";
    }

}

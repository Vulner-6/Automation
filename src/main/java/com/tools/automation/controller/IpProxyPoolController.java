package com.tools.automation.controller;

import com.tools.automation.mapper.IpProxyPoolMapper;
import com.tools.automation.model.IpProxyPool;
import com.tools.automation.support.AutoCheckIp;
import com.tools.automation.support.AutoGetIp;
import com.tools.automation.support.HttpsUtils;
import com.tools.automation.support.IpProxyPoolSupport;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;

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

    //设置一个开关，保证每次刷新一个页面不会开启新的线程
    private int threadSwitch=0;
    @GetMapping("ipProxyPool")
    public String ipProxyPool(Model model)
    {
        model.addAttribute("selectClass","list-group-item active");
        if(threadSwitch==0)
        {
            //测试线程1是否好用
            AutoGetIp autoGetIp=new AutoGetIp(this.okHttpClient,this.ipProxyPoolMapper);
            Thread testAutoGetIp=new Thread(autoGetIp);
            testAutoGetIp.start();

            //测试线程2是否好用
            AutoCheckIp autoCheckIp=new AutoCheckIp(this.okHttpClient,this.ipProxyPoolMapper);
            Thread testAutoCheckIp=new Thread(autoCheckIp);
            testAutoCheckIp.start();
        }
        //赋值为1，代表线程已经开启了，以后刷新页面，就不会再开启新的线程了。除非服务器重启，重新加载类，才会再次开启线程。
        this.threadSwitch=1;

        //打印数据库中IP信息
        ArrayList<IpProxyPool> ipProxyPoolArrayList=new ArrayList<IpProxyPool>();
        ipProxyPoolArrayList=ipProxyPoolMapper.selectAll();
        model.addAttribute("sum",ipProxyPoolArrayList.size());
        model.addAttribute("ipProxyPoolArrayList",ipProxyPoolArrayList);
        return "ipProxyPool";
    }
}

package com.tools.automation.controller;

import com.tools.automation.mapper.IpProxyPoolMapper;
import com.tools.automation.model.IpProxyPool;
import com.tools.automation.support.GetIpProxy;
import com.tools.automation.support.HttpsUtils;
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
    private GetIpProxy getIpProxy;

    @Autowired
    private IpProxyPoolMapper ipProxyPoolMapper;

    @GetMapping("ipProxyPool")
    public String ipProxyPool()
    {
        return "ipProxyPool";
    }

    @GetMapping("getNewIpProxy")
    public String getNewIpProxy(Model model)
    {
        ArrayList<IpProxyPool> ipProxyPoolArrayList =getIpProxy.getKuaiDaiLi(okHttpClient);
        model.addAttribute("ipProxyArrayList",ipProxyPoolArrayList);
        for(IpProxyPool ipProxyPool:ipProxyPoolArrayList)
        {
            ipProxyPoolMapper.insert(ipProxyPool);
        }
        return "ipProxyPool";
    }

}

package com.tools.automation.service;

import com.tools.automation.model.IpProxyPool;
import org.springframework.stereotype.Service;

@Service
public class IpProxyPoolService
{

    public void testInsert()
    {
        IpProxyPool ipProxyPool=new IpProxyPool();
        ipProxyPool.setIpAddress("127.0.0.1");
        ipProxyPool.setPort("1111");

    }
}

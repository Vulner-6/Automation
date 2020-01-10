package com.tools.automation.controller;

import com.tools.automation.service.IpProxyPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IpProxyPoolController
{
    @Autowired
    private IpProxyPoolService ipProxyPoolService;
    @GetMapping("/")
    public String test()
    {
        ipProxyPoolService.testInsert();
        return "index";
    }
}

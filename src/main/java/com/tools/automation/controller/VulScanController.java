package com.tools.automation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VulScanController
{
    @GetMapping("/universalScan")
    public String universalScan()
    {
        return "universalScan";
    }

    @PostMapping("/universalScan")
    public String universalScan(HttpServletRequest request)
    {
        String target=request.getParameter("target");
        System.out.println(target);
        return "universalScan";
    }


}

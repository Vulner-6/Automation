package com.tools.automation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 该控制器的作用就是渲染首页
 */
@Controller
public class IndexController
{
    @GetMapping("/")
    public String index()
    {
        return "index";
    }

}

package com.tools.automation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VulScanController
{
    @GetMapping("/universalScan")
    public String universalScan()
    {
        return "universalScan";
    }
}

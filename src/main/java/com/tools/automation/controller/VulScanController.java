package com.tools.automation.controller;

import com.tools.automation.support.HttpsUtils;
import com.tools.automation.support.UniversalScan;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class VulScanController
{
    private HttpsUtils httpsUtils=new HttpsUtils();
    private OkHttpClient okHttpClient=httpsUtils.getTrustAllClient();
    @GetMapping("/universalScan")
    public String universalScan()
    {
        return "universalScan";
    }

    @PostMapping("/universalScan")
    public String universalScan(HttpServletRequest request)
    {
        String target=request.getParameter("target");
        UniversalScan universalScan=new UniversalScan();
        Boolean result=universalScan.thinkPHP_RCE(okHttpClient,target);
        System.out.println(result);
        //Java如何自动加载类呢？
        return "universalScan";
    }


}

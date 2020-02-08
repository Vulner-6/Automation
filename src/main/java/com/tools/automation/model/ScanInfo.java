package com.tools.automation.model;

import org.springframework.stereotype.Service;

@Service
public class ScanInfo
{
    private String targetUrl;
    private String gmtCreate;
    private String gmtModify;
    private int vulNum;
    private int scanNum;
    private String vulName;

    public String getTargetUrl()
    {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl)
    {
        this.targetUrl = targetUrl;
    }

    public String getGmtCreate()
    {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate)
    {
        this.gmtCreate = gmtCreate;
    }

    public String getGmtModify()
    {
        return gmtModify;
    }

    public void setGmtModify(String gmtModify)
    {
        this.gmtModify = gmtModify;
    }

    public int getVulNum()
    {
        return vulNum;
    }

    public void setVulNum(int vulNum)
    {
        this.vulNum = vulNum;
    }

    public int getScanNum()
    {
        return scanNum;
    }

    public void setScanNum(int scanNum)
    {
        this.scanNum = scanNum;
    }

    public String getVulName()
    {
        return vulName;
    }

    public void setVulName(String vulName)
    {
        this.vulName = vulName;
    }
}

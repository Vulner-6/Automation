package com.tools.automation.model;

public class IpProxyPool
{
    private Long id;
    private String site;
    private Long gmtCreate;
    private String ipAddress;
    private int port;
    private String protocol;
    private int delayTime;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getSite()
    {
        return site;
    }

    public void setSite(String site)
    {
        this.site = site;
    }

    public Long getGmtCreate()
    {
        return gmtCreate;
    }

    public void setGmtCreate(Long gmtCreate)
    {
        this.gmtCreate = gmtCreate;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public int getDelayTime()
    {
        return delayTime;
    }

    public void setDelayTime(int delayTime)
    {
        this.delayTime = delayTime;
    }
}

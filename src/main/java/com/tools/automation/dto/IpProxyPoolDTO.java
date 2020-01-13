package com.tools.automation.dto;

public class IpProxyPoolDTO
{
    private Long id;
    private String site;
    private String gmtCreate;
    private String ipAddress;
    private String port;
    private String protocol;
    private String delayTime;
    //新增一个数量属性
    private Integer sum;

    public Integer getSum()
    {
        return sum;
    }

    public void setSum(Integer sum)
    {
        this.sum = sum;
    }

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

    public String getGmtCreate()
    {
        return gmtCreate;
    }

    public void setGmtCreate(String gmtCreate)
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

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
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

    public String getDelayTime()
    {
        return delayTime;
    }

    public void setDelayTime(String delayTime)
    {
        this.delayTime = delayTime;
    }
}

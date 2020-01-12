package com.tools.automation.mapper;

import com.tools.automation.model.IpProxyPool;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Service
@Mapper
public interface IpProxyPoolMapper
{
    @Insert("INSERT INTO ip_proxy_pool (id,site,gmt_create,ip_address,port,protocol,delay_time) VALUES (#{id}," +
            "#{site},#{gmtCreate},#{ipAddress},#{port},#{protocol},#{delayTime})")
    void insert(IpProxyPool ipProxyPool);
    @Delete("DELETE FROM ip_proxy_pool WHERE id>0")
    void cleanIpProxyPoolData();
}

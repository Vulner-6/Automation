package com.tools.automation.mapper;

import com.tools.automation.model.IpProxyPool;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Mapper
public interface IpProxyPoolMapper
{
    //插入一条数据到ip_proxy_pool中
    @Insert("INSERT INTO ip_proxy_pool (id,site,gmt_create,ip_address,port,protocol,delay_time) VALUES (#{id}," +
            "#{site},#{gmtCreate},#{ipAddress},#{port},#{protocol},#{delayTime})")
    void insert(IpProxyPool ipProxyPool);

    //删除ip_proxy_pool数据表中所有数据
    @Delete("DELETE FROM ip_proxy_pool WHERE id>0")
    void cleanIpProxyPoolData();

    //查询数据条数
    @Select("SELECT COUNT(1) FROM ip_proxy_pool")
    Integer ipProxyPoolCount();

    //获取数据库中所有代理IP信息
    @Select("SELECT * FROM ip_proxy_pool")
    ArrayList<IpProxyPool> selectAll();

    //根据IP地址删除某条记录
    @Delete("DELETE FROM ip_proxy_pool WHERE ip_address=#{ipAddress}")
    void deleteByIpAddress(IpProxyPool ipProxyPool);
}

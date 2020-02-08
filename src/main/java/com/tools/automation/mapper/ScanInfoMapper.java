package com.tools.automation.mapper;

import com.tools.automation.model.ScanInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Service
@Mapper
public interface ScanInfoMapper
{
    @Insert("INSERT INTO scan_info (target_url,gmt_create,gmt_modify,vul_num,scan_num,vul_name) VALUES (#{targetUrl}," +
            "#{gmtCreate},#{gmtModify},#{vulNum},#{scanNum},#{vulName})")
    void insert(ScanInfo scanInfo);
}

package com.itheima.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.domain.po.Permission;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}


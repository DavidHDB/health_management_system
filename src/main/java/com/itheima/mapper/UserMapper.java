package com.itheima.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.domain.po.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select t2.role_id from t_user_role t2 where t2.user_id=#{userId}")
    List<String> getRoleIdList(String userId);
    @Select("select t2.permission_id from t_role_permission t2 where t2.role_id=#{roleId}")
    List<String> getPermissionIdList(String roleId);
    @Insert(" insert into t_user_role values (#{userId},3)")
    public void insert(Integer userId);
}


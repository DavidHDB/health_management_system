package com.itheima.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import com.itheima.domain.po.Permission;
import com.itheima.domain.po.Role;
import com.itheima.mapper.UserMapper;
import com.itheima.service.PermissionService;
import com.itheima.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private RoleService roleService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private UserMapper userMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        //当前用户角色id
        List<String> roleIdList = userMapper.getRoleIdList((String) loginId);//当前用户权限id
        //当前用户权限id
        List<String> permissionIdList = new ArrayList<>();
        roleIdList.forEach(item->{
            List<String> roleIdList1 = userMapper.getPermissionIdList(String.valueOf(item));
            permissionIdList.addAll(roleIdList1);
        });
        //去重
        List<String> collect = permissionIdList.stream().distinct().collect(Collectors.toList());
        //当前用户权限集合
        List<Permission> permissions = permissionService.listByIds(collect);
        List<String> list = new ArrayList<>();
        permissions.forEach(item->{
            list.add(item.getKeyword());
        });
        return list;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        //当前用户角色id
        List<String> roleIdList = userMapper.getRoleIdList((String) loginId);
        //当前用户角色集合
        List<Role> roles = roleService.listByIds(roleIdList);
        List<String> list = new ArrayList<>();
        roles.forEach(item->{
            list.add(item.getKeyword());
        });
        return list;
    }
}

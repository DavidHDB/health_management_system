package com.itheima.controller;


import com.itheima.domain.po.Result;
import com.itheima.domain.po.UserRole;
import com.itheima.service.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/userRoles")
public class UserRoleController {
    /**
     * 服务对象
     */
    @Resource
    private UserRoleService userRoleService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public Result selectOne(@PathVariable Serializable id) {
        return Result.success("功能未开发");
    }

    /**
     * 新增数据
     *
     * @param userRole 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result insert(@RequestBody UserRole userRole) {
        return Result.success("功能未开发");
    }

    /**
     * 修改数据
     *
     * @param userRole 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(@RequestBody UserRole userRole) {
        return Result.success("功能未开发");
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Result delete(@RequestParam("idList") List<Long> idList) {
        return Result.success("功能未开发");
    }
}


package com.itheima.controller;

import com.itheima.service.SetmealCheckGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RestController
@Slf4j
@RequestMapping("/setmealCheckgroups")
public class SetmealCheckgroupController {
    /**
     * 服务对象
     */
    @Resource
    private SetmealCheckGroupService setmealCheckgroupService;



}


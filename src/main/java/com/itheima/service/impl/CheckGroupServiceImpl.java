package com.itheima.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.domain.po.Checkgroup;
import com.itheima.mapper.CheckGroupMapper;
import com.itheima.service.CheckGroupService;
import org.springframework.stereotype.Service;


@Service
public class CheckGroupServiceImpl extends ServiceImpl<CheckGroupMapper, Checkgroup> implements CheckGroupService {
}


package com.itheima.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.domain.po.CheckgroupCheckitem;
import com.itheima.mapper.CheckGroupCheckItemMapper;
import com.itheima.service.CheckGroupCheckItemService;
import org.springframework.stereotype.Service;

@Service
public class CheckgroupCheckitemServiceImpl extends ServiceImpl<CheckGroupCheckItemMapper, CheckgroupCheckitem> implements CheckGroupCheckItemService {
}


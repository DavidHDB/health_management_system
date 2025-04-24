package com.itheima.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.domain.po.Checkitem;
import com.itheima.mapper.CheckItemMapper;
import com.itheima.service.CheckItemService;
import org.springframework.stereotype.Service;

@Service
public class CheckitemServiceImpl extends ServiceImpl<CheckItemMapper, Checkitem> implements CheckItemService {

}


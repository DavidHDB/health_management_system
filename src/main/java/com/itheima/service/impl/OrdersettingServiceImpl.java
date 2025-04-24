package com.itheima.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.domain.po.Ordersetting;
import com.itheima.mapper.OrderSettingMapper;
import com.itheima.service.OrderSettingService;
import org.springframework.stereotype.Service;

@Service
public class OrdersettingServiceImpl extends ServiceImpl<OrderSettingMapper, Ordersetting> implements OrderSettingService {
}


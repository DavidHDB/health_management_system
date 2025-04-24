package com.itheima.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.domain.dto.OrdersettingDto;
import com.itheima.domain.po.Ordersetting;
import com.itheima.domain.po.Result;
import com.itheima.service.OrderSettingService;
import com.itheima.utils.POIUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.itheima.utils.MessageConstant.*;


@RestController
@Slf4j
@RequestMapping("/ordersettings")
public class OrdersettingController {
    /**
     *  预约设置管理
     */
    @Resource
    private OrderSettingService ordersettingService;
    @GetMapping("/selectByMouth")
    Result SelectByMouth(String data){
        LambdaQueryWrapper<Ordersetting> lqw = new LambdaQueryWrapper<>();
        lqw.like(Ordersetting::getOrderdate,data);
        List<OrdersettingDto> ordersettingDtos = new ArrayList<>();
        List<Ordersetting> list = ordersettingService.list(lqw);
        list.stream().forEach(item->{
            OrdersettingDto ordersettingDto = new OrdersettingDto();
            BeanUtil.copyProperties(item,ordersettingDto,"orderdate");
            ordersettingDto.setData(item.getOrderdate().getDate());
            ordersettingDtos.add(ordersettingDto);
        });

        return CollectionUtil.isNotEmpty(list)? Result.success(ordersettingDtos,GET_ORDERSETTING_SUCCESS): Result.fail(GET_ORDERSETTING_FAIL);
    }

    /**
     * 编辑预约设置
     * @param orderdate
     * @param number
     * @return
     */
    @GetMapping("/editNumberByDate")
    Result EditNumberByDate(String orderdate, String number) {
        LambdaQueryWrapper<Ordersetting> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ordersetting::getOrderdate, orderdate);
        Ordersetting one = ordersettingService.getOne(lqw);
        if (one == null) {
            // 如果不存在，创建新的预约设置
            Ordersetting ordersetting = new Ordersetting();
            ordersetting.setNumber(Integer.valueOf(number));
            try {
                ordersetting.setOrderdate(new SimpleDateFormat("yyyy-MM-dd").parse(orderdate)); // 使用 SimpleDateFormat 进行日期解析
            } catch (Exception e) {
                return Result.fail("日期格式错误");
            }
            ordersetting.setReservations(0);
            return ordersettingService.save(ordersetting) ? 
                Result.success(ordersetting, ORDERSETTING_SUCCESS) : 
                Result.fail(ORDERSETTING_FAIL);
        }
        // 如果存在，更新预约人数
        one.setNumber(Integer.valueOf(number));
        boolean updated = ordersettingService.updateById(one);
        return updated ? 
            Result.success(one, ORDERSETTING_SUCCESS) : 
            Result.fail(ORDERSETTING_FAIL);
    }

    /**
     * 导入预约设置
     * @param excelFile
     * @return
     */
    @PostMapping("/upload")
    Result Upload(MultipartFile excelFile)  {
        try {
            List<String[]> list = POIUtils.readExcel(excelFile);
            list.stream().forEach(item->{
                LambdaQueryWrapper<Ordersetting> lqw = new LambdaQueryWrapper<>();
                lqw.eq(Ordersetting::getOrderdate,item[0]);
                Ordersetting one = ordersettingService.getOne(lqw);
                if (one==null){
                    one.setOrderdate(new Date(item[0]));
                    one.setNumber(Integer.valueOf(item[1]));
                    one.setReservations(0);
                    ordersettingService.save(one);
                }
                one.setNumber(Integer.valueOf(item[1]));
                ordersettingService.updateById(one);
            });
            return Result.success(IMPORT_ORDERSETTING_SUCCESS);
        } catch (IOException e) {
            return Result.success(IMPORT_ORDERSETTING_FAIL);
        }
    }



}


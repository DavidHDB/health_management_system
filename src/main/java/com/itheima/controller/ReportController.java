package com.itheima.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.domain.po.Member;
import com.itheima.domain.po.Order;
import com.itheima.domain.po.Result;
import com.itheima.domain.po.UserRole;
import com.itheima.service.*;
import com.itheima.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@Slf4j
@RequestMapping("/report")
public class ReportController {
    /**
     * 服务对象
     */
    @Resource
    private MemberService memberService;
    @Resource
    private SetmealService setmealService;
    @Resource
    private OrderService orderService;
    @Resource
    private RoleService roleService;
    @Resource
    private UserRoleService userRoleService;
    @GetMapping("/exportBusinessReport")
    Result exportBusinessReport(){
        return Result.success("功能未开发");
    }
    @GetMapping("/getBusinessReportData")
    Result getBusinessReportData() throws Exception {
        String reportDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        LambdaQueryWrapper<Member> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Member::getRegtime,reportDate);
        long todayNewMember = memberService.count(lqw);
        long totalMember = memberService.count();

        //本周一
        String s = DateUtils.parseDate2String(DateUtils.getThisWeekMonday());
        //本月一号
        String s1 = DateUtils.parseDate2String(DateUtils.getFirstDay4ThisMonth());
        LambdaQueryWrapper<Member> lqw2 = new LambdaQueryWrapper<>();
        lqw2.between(Member::getRegtime,s,reportDate);
        LambdaQueryWrapper<Member> lqw3 = new LambdaQueryWrapper<>();
        lqw2.between(Member::getRegtime,s1,reportDate);
        //本周成员数量
        long thisWeekNewMember = memberService.count(lqw2);
        //本月成员数量
        long thisMonthNewMember = memberService.count(lqw3);
        //今日 预约数

        LambdaQueryWrapper<Order> lqw4 = new LambdaQueryWrapper<>();
        lqw4.eq(Order::getOrderdate,reportDate);
        long todayOrderNumber = orderService.count(lqw4);
        //今日到诊数
        LambdaQueryWrapper<Order> lqw5 = new LambdaQueryWrapper<>();
        String orderStatus="已到诊";
        lqw5.eq(Order::getOrderdate,reportDate).eq(Order::getOrderstatus,orderStatus);
        long todayVisitsNumber = orderService.count(lqw5);
        //本周预约数
        LambdaQueryWrapper<Order> lqw6 = new LambdaQueryWrapper<>();
        lqw6.between(Order::getOrderdate,s,reportDate);
        long thisWeekOrderNumber = orderService.count(lqw6);

        //本周到诊数
        LambdaQueryWrapper<Order> lqw7 = new LambdaQueryWrapper<>();
        lqw7.eq(Order::getOrderstatus,orderStatus).between(Order::getOrderdate,s,reportDate);
        long thisWeekVisitsNumber = orderService.count(lqw7);
        //本月预约数
        LambdaQueryWrapper<Order> lqw8 = new LambdaQueryWrapper<>();
        lqw8.between(Order::getOrderdate,s1,reportDate);
        long thisMonthOrderNumber = orderService.count(lqw8);
        //本月到诊数
        LambdaQueryWrapper<Order> lqw9 = new LambdaQueryWrapper<>();
        lqw9.eq(Order::getOrderstatus,orderStatus).between(Order::getOrderdate,s1,reportDate);
        long thisMonthVisitsNumber = orderService.count(lqw9);
        HashMap<String, Object> map = new HashMap<>();

        map.put("reportDate",reportDate);
        map.put("todayNewMember",todayNewMember);
        map.put("totalMember",totalMember);
        map.put("thisWeekNewMember",thisWeekNewMember);
        map.put("thisMonthNewMember",thisMonthNewMember);
        map.put("todayOrderNumber",todayOrderNumber);
        map.put("thisWeekOrderNumber",thisWeekOrderNumber);
        map.put("todayVisitsNumber",todayVisitsNumber);
        map.put("thisWeekVisitsNumber",thisWeekVisitsNumber);
        map.put("thisMonthOrderNumber",thisMonthOrderNumber);
        map.put("thisMonthVisitsNumber",thisMonthVisitsNumber);
        List<Map<String, Object>> list = new ArrayList<>();
        //套餐名称
         setmealService.list().stream().forEach(item->{
             HashMap<String, Object> map1 = new HashMap<>();
             map1.put("name",item.getName());
             //备注
             map1.put("remark",item.getRemark());
             LambdaQueryWrapper<Order> lqw10 = new LambdaQueryWrapper<>();
             lqw10.eq(Order::getSetmealId,item.getId());
             //总数
             long count = orderService.count();
             //预约数量
             long setmeal_count = orderService.count(lqw10);
             //占比
             Double proportion=  setmeal_count*1.0/count*100;
             //方法三：通过BigDecimal类实现
             BigDecimal bg = new BigDecimal(proportion);
             proportion = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
             map1.put("proportion",proportion.toString()+"%");
             map1.put("setmeal_count",setmeal_count);
             list.add(map1);
         });
         map.put("hotSetmeal",list);
        return CollectionUtil.isNotEmpty(map)?Result.success(map,"查询成功"): Result.fail("查询失败");
    }
    @GetMapping("/getRoleReport")
    Result getRoleReport(){
        HashMap<String, Object> map = new HashMap<>();
        List<Map<String,Object>> RoleList=new ArrayList<>();
        roleService.list().stream().forEach(item->{
            HashMap<String, Object> role = new HashMap<>();
            LambdaQueryWrapper<UserRole> lqw = new LambdaQueryWrapper<>();
            lqw.eq(UserRole::getRoleId,item.getId());
            role.put("name",item.getName());
            role.put("value",userRoleService.count(lqw));
            RoleList.add(role);
        });
        map.put("roleCount",RoleList);
        return Result.success(map,"成功");
    }
    @GetMapping("/getSetmealReport")
    Result getSetmealReport(){
        HashMap<String, Object> map = new HashMap<>();
        List<Map<String,Object>> setmealList=new ArrayList<>();
        setmealService.list().forEach(item->{
            HashMap<String, Object> setmealMap = new HashMap<>();
            LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Order::getSetmealId,item.getId());
            setmealMap.put("name",item.getName());
            setmealMap.put("value",orderService.count(lqw));
            log.info("name:{}",setmealMap.get("name"));
            log.info("value:{}",setmealMap.get("value"));
            setmealList.add(setmealMap);
        });
        log.info("setmealList:{}",setmealList);
        map.put("setmealCount", setmealList);
        log.info("map:{}",map);
        return Result.success(map,"成功");
    }
    @GetMapping("/getMemberReport")
    Result getMemberReport(){
        HashMap<String, Object> map = new HashMap<>();
        List<String> monthList = new ArrayList<>();
        List<Integer> memberCount = new ArrayList<>();
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.MONTH,-12);
        for (int i = 1; i <=12; i++) {
            instance.add(Calendar.MONTH,1);
            String format = new SimpleDateFormat("yyyy-MM").format(instance.getTime());
            monthList.add(format);
            LambdaQueryWrapper<Member> lqw = new LambdaQueryWrapper<>();
            lqw.like(Member::getRegtime,format);
            memberCount.add(memberService.list(lqw).size());
        }


        map.put("months",monthList);
        map.put("memberCount",memberCount);
        return Result.success(map,"成功");
    }


}


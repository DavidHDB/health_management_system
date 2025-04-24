package com.itheima.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.domain.dto.OrderDTO;
import com.itheima.domain.po.*;
import com.itheima.mapper.UserMapper;
import com.itheima.service.*;
import com.itheima.utils.DateUtils;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.itheima.utils.MessageConstant.*;


@RestController
@Slf4j
@RequestMapping("/orders")
public class OrderController {
    /**
     * 服务对象
     */
    @Resource
    private OrderService orderService;
    @Resource
    private MemberService memberService;
    @Resource
    private OrderSettingService ordersettingService;
    @Resource
    private SMSUtils smsUtils;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SetmealService setmealService;
    @Resource
    private  UserService userService;
    @Resource
    private UserMapper userMapper;
    @SaIgnore
    @PostMapping("/login")
    Result Login(@RequestBody Map map){
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        String key=CACHE_CODE_PHONE+telephone;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value==null){
            return Result.fail(TELEPHONE_VALIDATECODE_NOTNULL);
        }
        if (!value.equals(validateCode)){
            return Result.fail(VALIDATECODE_ERROR);
        }
        stringRedisTemplate.delete(key);
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getTelephone,telephone);
        List<User> list = userService.list(lqw);
        if (list.size()==1){
            StpUtil.login(list.get(0).getId());
            return Result.success(list,LOGIN_SUCCESS);
        }
        if (list.size()==0){
            User user = new User();
            user.setTelephone(telephone);
            user.setUsername(RandomUtil.randomStringUpper(10));
            user.setPassword(UUID.fastUUID().toString());
            userMapper.insert(user);
            userMapper.insert(user.getId());
            StpUtil.login(user.getId());
            return Result.success(list,LOGIN_SUCCESS);
        }
        return Result.fail(LOGIN_ERROR);
    }
    @SaIgnore
    @GetMapping("/sendcode")
    Result SendCode( String telephone){
        String key=CACHE_CODE_PHONE+telephone;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value!=null){
            return Result.success("请勿重复操作");
        }
        if (telephone.length()!=11){
            return Result.fail("手机号输入错误");
        }
        //TODO 判断手机号是否已经注册
        /*LambdaQueryWrapper<Member> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Member::getPhonenumber,telephone);
        List<Member> list = memberService.list(lqw);
        if (list.size()!=0){
            return Result.fail(VALIDATEPHONE_ERROR);
        }*/

        Integer code = ValidateCodeUtils.generateValidateCode(6);
        log.info("phone:{}",telephone);
        log.info("code:{}",code);
        stringRedisTemplate.opsForValue().set(key, String.valueOf(code),5, TimeUnit.MINUTES);
        return smsUtils.sendMessage(telephone, String.valueOf(code))?Result.success(SEND_VALIDATECODE_SUCCESS): Result.fail(SEND_VALIDATECODE_FAIL);
    }
    @PostMapping("/submit")
    Result Submit(@RequestBody Map map) throws Exception {
        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        String key=CACHE_CODE_PHONE+telephone;
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value==null){
            return Result.fail(TELEPHONE_VALIDATECODE_NOTNULL);
        }
        if (!value.equals(validateCode)){
            return Result.fail(VALIDATECODE_ERROR);
        }

        //TODO 判断当前日期是否进行预约设置
        String orderDate = (String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(orderDate);
        //套餐ID
        String setmealId = (String) map.get("setmealId");
        //设置预约类型 微信预约和电话预约
        String orderType= Order.ORDERTYPE_WEIXIN;
        //设置预约状态
        String orderStatus=Order.ORDERSTATUS_NO;
        LambdaQueryWrapper<Ordersetting> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ordersetting::getOrderdate,orderDate);
        Ordersetting one = ordersettingService.getOne(lqw);
        if (one==null){
            return Result.fail(SELECTED_DATE_CANNOT_ORDER);
        }
        //登录成功 删除验证码
        stringRedisTemplate.delete(key);
        //TODO 判断当前日期是否已经约满
        if (one.getReservations()>=one.getNumber()){
            return Result.fail(ORDER_FULL);
        }
        //TODO 判断当前用户是否为会员(新用户)，是会员，直接预约 不是会员 先自动注册在预约
        LambdaQueryWrapper<Member> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(Member::getPhonenumber,telephone);
        Member one2 = memberService.getOne(lqw2);
        if (one2==null){
            //保存数据报数据库 先自动注册在预约
            Member member = new Member();
            member.setName((String) map.get("name"));
            member.setIdcard((String) map.get("idCard"));
            member.setRegtime(new Date());
            member.setSex((String) map.get("sex"));
            member.setPhonenumber(telephone);
            if (!memberService.save(member)){
                return Result.fail(ADD_MEMBER_FAIL);
            }
            one2=member;
        }

        //TODO 判断当前用户是否重复预约 同一天，同一用户 同一套餐（不是新用户，判断是否重复预约）
        LambdaQueryWrapper<Order> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(Order::getOrderdate,orderDate)
                .eq(Order::getSetmealId,setmealId)
                .eq(Order::getMemberId,one2.getId());
        List<Order> list = orderService.list(lqw1);
        if (list.size()!=0){
            return Result.fail(HAS_ORDERED);
        }
        //TODO 保存预约信息并更新预约人数
        Order order = new Order();
        order.setMemberId(one2.getId());
        order.setOrderdate(date);
        order.setOrdertype(orderType);
        order.setOrderstatus(orderStatus);
        order.setSetmealId(Integer.valueOf(setmealId));
        //保存预约信息
        if (!orderService.save(order)){
            return Result.fail(ORDER_ERROR);
        }
        //更新预约人数
        one.setReservations(one.getReservations()+1);
        ordersettingService.updateById(one);
        return Result.success(order.getId(),ORDER_SUCCESS);
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/findById")
    public Result selectOne(String id) {
        Order byId = orderService.getById(id);
        if (byId==null){
            return Result.fail("查无此人");
        }
        OrderDTO orderDto = new OrderDTO();
        BeanUtil.copyProperties(byId,orderDto);
        Member byId1 = memberService.getById(byId.getMemberId());
        orderDto.setMember(byId1.getName());
        Setmeal byId2 = setmealService.getById(byId.getSetmealId());
        orderDto.setSetmeal(byId2.getName());
        return Result.success(orderDto,ORDER_SUCCESS);
    }

    /**
     * 新增数据
     *
     * @param order 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result insert(@RequestBody Order order) throws Exception {
        // 校验必要字段
        if (order.getMemberId() == null || order.getSetmealId() == null || order.getOrderdate() == null) {
            return Result.fail("必要信息不完整");
        }

        // 检查预约日期是否可预约
        LambdaQueryWrapper<Ordersetting> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Ordersetting::getOrderdate, DateUtils.parseDate2String(order.getOrderdate()));
        Ordersetting ordersetting = ordersettingService.getOne(lqw);
        if (ordersetting == null) {
            return Result.fail(SELECTED_DATE_CANNOT_ORDER);
        }

        // 检查是否约满
        if (ordersetting.getReservations() >= ordersetting.getNumber()) {
            return Result.fail(ORDER_FULL);
        }

        // 设置默认值
        order.setOrdertype(Order.ORDERTYPE_WEIXIN);
        order.setOrderstatus(Order.ORDERSTATUS_NO);

        // 保存订单信息
        if (!orderService.save(order)) {
            return Result.fail(ORDER_ERROR);
        }

        // 更新预约人数
        ordersetting.setReservations(ordersetting.getReservations() + 1);
        ordersettingService.updateById(ordersetting);

        return Result.success(order.getId(), ORDER_SUCCESS);
    }

    /**
     * 修改数据
     *
     * @param order 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(@RequestBody Order order) {
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


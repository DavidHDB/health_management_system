package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.itheima.domain.po.Result;
import com.itheima.domain.po.User;
import com.itheima.service.UserService;
import com.itheima.utils.SMSUtils;
import com.itheima.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.itheima.utils.MessageConstant.*;


@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    /**
     * 服务对象
     */
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private SMSUtils smsUtils;
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
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getTelephone,telephone);
        List<User> list = userService.list(lqw);
        if (list.size()!=0){
            return Result.fail(VALIDATEPHONE_ERROR);
        }
        Integer code = ValidateCodeUtils.generateValidateCode(6);
        log.info("phone:{}",telephone);
        log.info("code:{}",code);
        stringRedisTemplate.opsForValue().set(key, String.valueOf(code),5, TimeUnit.MINUTES);
        return smsUtils.sendMessage(telephone, String.valueOf(code))?Result.success(SEND_VALIDATECODE_SUCCESS): Result.fail(SEND_VALIDATECODE_FAIL);
    }

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
     * @param user 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result insert(@RequestBody User user) {
        return Result.success("功能未开发");
    }

    /**
     * 修改数据
     *
     * @param user 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(@RequestBody User user) {
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


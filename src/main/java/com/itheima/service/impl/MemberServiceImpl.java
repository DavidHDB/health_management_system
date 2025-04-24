package com.itheima.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.domain.dto.MemberDTO;
import com.itheima.domain.po.*;
import com.itheima.domain.*;
import com.itheima.mapper.MemberMapper;
import com.itheima.mapper.OrderMapper;
import com.itheima.mapper.SetmealCheckGroupMapper;
import com.itheima.mapper.SetmealMapper;
import com.itheima.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.itheima.utils.MessageConstant.GET_ORDERSETTING_FAIL;
import static com.itheima.utils.MessageConstant.GET_ORDERSETTING_SUCCESS;


@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, Member> implements MemberService {
    @Resource
    private MemberMapper memberMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SetmealMapper setmealMapper;
    @Resource
    private SetmealCheckGroupMapper setmealCheckgroupMapper;

    /**
     *  查询所有会员信息
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Result SelectById(Integer page,Integer pageSize) {
        page=(page-1)*pageSize;
        List<MemberDTO> memberDtos = memberMapper.selectById(page,pageSize);
        memberDtos.forEach(item->{
            String format = DateUtil.format(item.getOrderdate(), "yyyy-MM-dd");
            item.setOrderdate1(format);
        });
        return CollectionUtil.isNotEmpty(memberDtos)? Result.success(memberDtos,GET_ORDERSETTING_SUCCESS): Result.fail(GET_ORDERSETTING_FAIL);
    }

    /**
     * 添加会员信息
     * @param memberDto
     * @return
     */
    @Override
    public Result Insert(MemberDTO memberDto) {
        Member member = new Member();
        BeanUtil.copyProperties(memberDto,member);
        member.setName(memberDto.getUsername());
        member.setRegtime(new Date());
        memberMapper.insert(member);

        Setmeal setmeal = new Setmeal();
        setmeal.setName(memberDto.getSetmealname());
        setmeal.setRemark(memberDto.getRemark());
        setmeal.setImg(memberDto.getImg());
        setmealMapper.insert(setmeal);

        Order order = new Order();
        order.setOrdertype(memberDto.getOrdertype());
        order.setOrderdate(new Date());
        order.setOrderstatus("未到诊");
        order.setMemberId(member.getId());
        order.setSetmealId(setmeal.getId());
        orderMapper.insert(order);

        return Result.success("添加成功");
    }

    /**
     * 删除会员信息及相关数据
     *
     * 该方法将会根据传入的会员信息 (MemberDTO) 删除该会员在系统中的相关记录，
     * 包括会员自身的记录、关联的订单记录、套餐检查组记录和套餐记录。
     *
     * @param memberDto 需要删除的会员信息，包含会员的用户名
     * @return 返回一个 Result 对象，表示操作的结果，成功时返回“删除成功”
     */
    @Override
    @Transactional
    public Result Delete(MemberDTO memberDto) {
        // 根据用户名查询会员ID
        Integer id = memberMapper.selectByName(memberDto.getUsername());

        // 删除会员记录
        memberMapper.deleteById(id);

        // 创建查询条件，查找与该会员相关的订单
        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Order::getMemberId, id); // 通过会员ID查询订单
        Order order = orderMapper.selectOne(lqw); // 获取该会员的订单信息

        // 删除与该订单相关的记录
        orderMapper.deleteById(order); // 删除订单记录

        // 创建查询条件，查找与该订单相关的套餐检查组
        LambdaQueryWrapper<SetmealCheckgroup> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(SetmealCheckgroup::getSetmealId, order.getSetmealId()); // 通过套餐ID查询套餐检查组
        setmealCheckgroupMapper.delete(lqw2); // 删除套餐检查组记录

        // 删除与该订单相关的套餐记录
        setmealMapper.deleteById(order.getSetmealId()); // 删除套餐记录

        // 返回删除成功的结果
        return Result.success("删除成功");
    }


    /**
     * 修改会员信息
     * @param memberDto
     * @return
     */
    @Override
    public Result Update(MemberDTO memberDto) {
        Integer id = memberMapper.selectByName(memberDto.getUsername());
        Member member = memberMapper.selectById(id);
        member.setIdcard(memberDto.getIdcard());
        member.setPhonenumber(memberDto.getPhonenumber());
        memberMapper.updateById(member);

        LambdaQueryWrapper<Order> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Order::getMemberId,id);
        Order order = orderMapper.selectOne(lqw);
        order.setOrdertype(memberDto.getOrdertype());
        order.setSetmealId(Integer.valueOf(memberDto.getSetmealname()));
        orderMapper.updateById(order);

        LambdaQueryWrapper<SetmealCheckgroup> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(SetmealCheckgroup::getSetmealId,order.getSetmealId());

        List<SetmealCheckgroup> setmealCheckgroups = setmealCheckgroupMapper.selectList(lqw2);
        setmealCheckgroupMapper.delete(lqw2);
        setmealCheckgroups.forEach(item->{
            item.setSetmealId(order.getSetmealId());
            setmealCheckgroupMapper.insert(item);
        });

        return Result.success("修改成功");
    }

}


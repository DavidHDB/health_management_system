package com.itheima.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.domain.dto.MemberDTO;
import com.itheima.domain.po.Member;
import com.itheima.domain.po.Result;
import com.itheima.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

import static com.itheima.utils.MessageConstant.*;


@RestController
@Slf4j
@RequestMapping("/members")
public class MemberController {

    /**
     * 会员管理
     */
    @Resource
    private MemberService memberService;
    @PostMapping("/insert")
    Result Insert(@RequestBody MemberDTO memberDTO){
        return memberService.Insert(memberDTO);
    }

    /**
     * 删除数据
     * @param memberDTO
     * @return
     */
    @PostMapping("/Delete")
    Result delete(@RequestBody MemberDTO memberDTO) {
        return memberService.Delete(memberDTO);
    }

    /**
     * 修改数据
     * @param memberDTO
     * @return
     */
    @PutMapping("/update")
    Result Update(@RequestBody MemberDTO memberDTO){
        return memberService.Update(memberDTO);
    }

    /**
     * 删除数据
     * @param member
     * @return
     */
    @PostMapping("/delete")
    Result Delete(@RequestBody Member member){
        return memberService.removeById(member)? Result.success(DELETE_MEMBER_SUCCESS): Result.fail(DELETE_MEMBER_FAIL);
    }

    /**
     * 分页查询会员信息
     *
     * 该方法用于根据提供的分页参数（页码 `page` 和 每页条数 `pageSize`）以及查询条件 `queryString`，
     * 从数据库中查询会员信息，并返回查询结果。查询条件 `queryString` 会匹配会员的电话号码或姓名。
     * 如果 `queryString` 不为空，查询会分别对会员的电话号码和姓名进行模糊匹配。
     *
     * @param page 页码，从 1 开始，表示要查询的当前页
     * @param pageSize 每页显示的记录数，决定了查询结果的大小
     * @param queryString 用于模糊匹配查询的字符串，能够根据会员的电话号码或姓名进行筛选
     *
     * @return 返回查询结果的 `Result` 对象
     *         - 如果查询成功，返回包含会员信息的分页数据
     *         - 如果查询失败（如查询结果为空或发生异常），返回失败的结果
     */
    @GetMapping
    Result SelectByPage(Integer page, Integer pageSize, String queryString) {
        // 创建Lambda查询包装器，用于构建查询条件
        LambdaQueryWrapper<Member> lqw = new LambdaQueryWrapper<>();

        // 如果查询字符串不为空，则根据查询字符串进行会员手机号和姓名的模糊匹配
        lqw.like(StrUtil.isNotBlank(queryString), Member::getPhonenumber, queryString)
                .or().like(StrUtil.isNotBlank(queryString), Member::getName, queryString);

        // 创建分页对象，指定当前页码和每页显示的记录数
        Page<Member> memberPage = new Page<>(page, pageSize);

        // 调用服务层的分页查询方法，执行分页查询并返回结果
        Page<Member> page1 = memberService.page(memberPage, lqw);

        // 判断查询结果是否为空，如果不为空，返回成功的结果；否则返回失败的结果
        return page1 != null ? Result.success(page1, GET_MEMBER_SUCCESS) : Result.fail(GET_MEMBER_FAIL);
    }


    /**
     * 查询所有
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/selectById")
    public Result selectOne(Integer page,Integer pageSize) {
        return memberService.SelectById(page, pageSize);
    }

    /**
     * 新增数据
     * @param member 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result insert(@RequestBody Member member) {
        member.setRegtime(new Date());
        return memberService.save(member)?Result.success(ADD_MEMBER_SUCCESS): Result.fail(ADD_MEMBER_FAIL);
    }

    /**
     * 根据ID修改数据
     *
     * @param member 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(@RequestBody Member member) {
        return memberService.updateById(member)?Result.success(EDIT_MEMBER_SUCCESS): Result.fail(EDIT_MEMBER_FAIL);
    }

    /**
     * 批量和删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public Result delete(@RequestParam("idList") List<Long> idList) {
        return memberService.removeBatchByIds(idList)?Result.success(DELETE_MEMBER_SUCCESS): Result.fail(DELETE_MEMBER_FAIL);
    }
}


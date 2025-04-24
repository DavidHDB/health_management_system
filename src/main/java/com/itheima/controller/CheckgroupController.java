package com.itheima.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.domain.po.Checkgroup;
import com.itheima.domain.po.CheckgroupCheckitem;
import com.itheima.domain.po.Result;
import com.itheima.exception.BusinessException;
import com.itheima.service.CheckGroupCheckItemService;
import com.itheima.service.CheckGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static com.itheima.utils.MessageConstant.*;


@RestController
@Slf4j
@RequestMapping("/checkgroups")
public class CheckgroupController {
    /**
     * 服务对象
     */
    @Resource
    private CheckGroupService checkgroupService;
    @Resource
    private CheckGroupCheckItemService checkgroupCheckitemService;

    @PostMapping("/getcheckitemIds")
    Result GetcheckitemIds(@RequestBody Checkgroup checkgroup) {
        LambdaQueryWrapper<CheckgroupCheckitem> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CheckgroupCheckitem::getCheckgroupId, checkgroup.getId());
        List<CheckgroupCheckitem> list = checkgroupCheckitemService.list(lqw);
        return Result.success(list, QUERY_CHECKGROUP_SUCCESS);
    }

    /**
     * 分页查询所有数据
     * @param page
     * @param pageSize
     * @param queryString
     * @return
     */
    @GetMapping
    Result SelectAll(Integer page, Integer pageSize, String queryString) {
        LambdaQueryWrapper<Checkgroup> lqw = new LambdaQueryWrapper<>();
        Page<Checkgroup> checkgroupPage = new Page<>(page, pageSize);
        lqw.like(StrUtil.isNotBlank(queryString), Checkgroup::getCode, queryString).or()
                .like(StrUtil.isNotBlank(queryString), Checkgroup::getName, queryString).or()
                .like(StrUtil.isNotBlank(queryString), Checkgroup::getHelpcode, queryString)
                .orderByAsc(Checkgroup::getId);
        Page<Checkgroup> page1 = checkgroupService.page(checkgroupPage, lqw);
        return page1 != null ? Result.success(page1, QUERY_CHECKGROUP_SUCCESS) : Result.fail(QUERY_CHECKGROUP_FAIL);
    }

    /**
     * 新增数据
     *
     * @param checkgroup 实体对象
     * @return 新增结果
     */
    @PostMapping
    @Transactional
    public Result insert(String checkitemIds, @RequestBody Checkgroup checkgroup) {
        try {
            if (StrUtil.isBlank(checkitemIds)) {
                throw new BusinessException("检查项不能为空");
            }
            boolean save = checkgroupService.save(checkgroup); //保存基本信息
            if (!save) {
                throw new BusinessException(ADD_CHECKGROUP_FAIL);
            }
            extracted(checkitemIds, checkgroup); //保存中间表数据
            return Result.success(ADD_CHECKGROUP_SUCCESS);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("新增检查组失败", e);
            throw new BusinessException(ADD_CHECKGROUP_FAIL);
        }
    }

    /**
     * 修改数据
     *
     * @param checkgroup 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(String checkitemIds, @RequestBody Checkgroup checkgroup) {
        boolean b = checkgroupService.updateById(checkgroup);
        if (StrUtil.isBlank(checkitemIds)) {
            return b ? Result.success(EDIT_CHECKGROUP_SUCCESS) : Result.fail(EDIT_CHECKGROUP_FAIL);
        }
        LambdaQueryWrapper<CheckgroupCheckitem> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CheckgroupCheckitem::getCheckgroupId, checkgroup.getId());
        checkgroupCheckitemService.remove(lqw);
        extracted(checkitemIds, checkgroup);
        return b ? Result.success(EDIT_CHECKGROUP_SUCCESS) : Result.fail(EDIT_CHECKGROUP_FAIL);
    }

    /**
     * 保存中间表数据
     * @param checkitemIds
     * @param checkgroup
     */
    private void extracted(String checkitemIds, Checkgroup checkgroup) {
        if (StrUtil.isBlank(checkitemIds)) {
            return;
        }
        String[] split = checkitemIds.split(",");
        Arrays.stream(split).forEach(item -> {
            CheckgroupCheckitem checkgroupCheckitem = new CheckgroupCheckitem();
            checkgroupCheckitem.setCheckgroupId(checkgroup.getId());
            try {
                checkgroupCheckitem.setCheckitemId(Integer.valueOf(item));
                checkgroupCheckitemService.save(checkgroupCheckitem);
            } catch (NumberFormatException e) {
                throw new BusinessException("检查项ID格式不正确");
            }
        });
    }

    @PostMapping("/delete")
    public Result delete(@RequestBody Checkgroup checkgroup) {
        LambdaQueryWrapper<CheckgroupCheckitem> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CheckgroupCheckitem::getCheckgroupId, checkgroup.getId());
        List<CheckgroupCheckitem> list = checkgroupCheckitemService.list(lqw);
        if (CollectionUtil.isEmpty(list)) {
            return checkgroupService.removeById(checkgroup.getId()) ? Result.success(DELETE_CHECKGROUP_SUCCESS) : Result.fail(DELETE_CHECKGROUP_FAIL);
        }
        checkgroupCheckitemService.remove(lqw);
        return checkgroupService.removeById(checkgroup.getId()) ? Result.success(DELETE_CHECKGROUP_SUCCESS) : Result.fail(DELETE_CHECKGROUP_FAIL);
    }
}


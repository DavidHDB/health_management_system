package com.itheima.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.itheima.domain.dto.CheckGroupDTO;
import com.itheima.domain.dto.SetmealDTO;
import com.itheima.domain.po.*;
import com.itheima.exception.BusinessException;
import com.itheima.service.*;
import com.itheima.utils.QiniuUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.itheima.utils.MessageConstant.*;


@RestController
@Slf4j
@RequestMapping("/setmeals")
public class SetmealController {
    /**
     * 服务对象
     */
    @Resource
    private SetmealService setmealService;
    @Resource
    private SetmealCheckGroupService setmealCheckgroupService;
    @Resource
    private CheckGroupCheckItemService checkgroupCheckitemService;
    @Resource
    private CheckGroupService checkgroupService;
    @Resource
    private CheckItemService checkitemService;
    @Resource
    private QiniuUtils qiniuUtils;

    //删除垃圾图片
    @GetMapping("/deleteImge")
    Result deleteImge(String fileName){
        //删除垃圾文件
        qiniuUtils.deleteFileFromQiniu(fileName);
        return Result.success("删除成功");
    }
    //图片上传
    @PostMapping("/upload")
    Result Upload(@RequestParam("imgFile") MultipartFile imgFile) {
        //文件全名  eg：7d104dd7-15cd-42c5-9a85-b60ea6f423c2.jpg
        String originalFilename = imgFile.getOriginalFilename();
        //获取后缀
        String suffix = originalFilename.substring(originalFilename.lastIndexOf(".") );
        //随机文件名 + 后缀
        String fileName = UUID.randomUUID().toString()+suffix;
        //七牛云工具类 字节上传
        try {
            qiniuUtils.upload2Qiniu(imgFile.getBytes(), fileName);
            return Result.success(fileName,PIC_UPLOAD_SUCCESS);
        } catch (IOException e) {
            throw new BusinessException(PIC_UPLOAD_FAIL);
        }
    }
    @PostMapping("/getcheckgroupIds")
    Result GetCheckGroupIds(@RequestBody Setmeal setmeal)
    {
        LambdaQueryWrapper<SetmealCheckgroup> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealCheckgroup::getSetmealId,setmeal.getId());
        List<SetmealCheckgroup> list = setmealCheckgroupService.list(lqw);
        return Result.success(list,"查询成功");
    }
   @GetMapping("/selectall")
   Result SelectAll(){
       List<Checkgroup> list = checkgroupService.list();
       return CollectionUtil.isNotEmpty(list)? Result.success(list,QUERY_SETMEALLIST_SUCCESS): Result.fail(QUERY_SETMEALLIST_FAIL);
   }
   @PostMapping("/SelectAll")
   Result SelectAllSetmeal(){
       List<Setmeal> list = setmealService.list();
       return CollectionUtil.isNotEmpty(list)? Result.success(list,QUERY_ORDER_SUCCESS): Result.fail(QUERY_ORDER_FAIL);
   }


   @GetMapping("/findById")
   Result SelectById(String id){
       Setmeal byId = setmealService.getById(id);
       SetmealDTO setmealDto = new SetmealDTO();
       BeanUtil.copyProperties(byId,setmealDto);
       //得到 套餐中间表信息
       LambdaQueryWrapper<SetmealCheckgroup> lqw = new LambdaQueryWrapper<>();
       lqw.eq(SetmealCheckgroup::getSetmealId,id);
       List<SetmealCheckgroup> list = setmealCheckgroupService.list(lqw);
       ArrayList<CheckGroupDTO> checkgroupDtos = new ArrayList<>();
       //遍历每个套餐信息
       list.stream().forEach(item->{
           LambdaQueryWrapper<CheckgroupCheckitem> lqw2 = new LambdaQueryWrapper<>();
           LambdaQueryWrapper<Checkgroup> lqw4 = new LambdaQueryWrapper<>();

           //项目信息 检查组 获取检查组信息
           CheckGroupDTO checkgroupDto = new CheckGroupDTO();
           lqw4.eq(Checkgroup::getId,item.getCheckgroupId());
           Checkgroup one1 = checkgroupService.getOne(lqw4);
           BeanUtil.copyProperties(one1,checkgroupDto);
           //单项信息 检查项 获取检查项信息
           lqw2.eq(CheckgroupCheckitem::getCheckgroupId,item.getCheckgroupId());
           List<CheckgroupCheckitem> list1 = checkgroupCheckitemService.list(lqw2);
           List<Checkitem> checkitemList=new ArrayList<>();
           list1.stream().forEach(item1->{
               LambdaQueryWrapper<Checkitem> lqw3 = new LambdaQueryWrapper<>();
               lqw3.eq(Checkitem::getId,item1.getCheckitemId());
               Checkitem one = checkitemService.getOne(lqw3);
               checkitemList.add(one);
           });
           checkgroupDto.setCheckItems(checkitemList);
           checkgroupDtos.add(checkgroupDto);
       });
       setmealDto.setCheckGroups(checkgroupDtos);
       return setmealDto!=null?Result.success(setmealDto,QUERY_ORDER_SUCCESS): Result.fail(QUERY_ORDER_FAIL);
   }

   @GetMapping
   Result SelectByPage(Integer page, Integer pageSize,String queryString){
       Page<Setmeal> setmealPage = new Page<>(page, pageSize);
       LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();
       lqw.like(StrUtil.isNotBlank(queryString),Setmeal::getName,queryString).or()
               .like(StrUtil.isNotBlank(queryString),Setmeal::getCode,queryString)
               .like(StrUtil.isNotBlank(queryString),Setmeal::getHelpcode,queryString)
               .orderByAsc(Setmeal::getId);
       Page<Setmeal> page1 = setmealService.page(setmealPage, lqw);
       return page1!=null? Result.success(page1,QUERY_SETMEAL_SUCCESS): Result.fail(QUERY_SETMEAL_FAIL);

   }
    /**
     * 新增数据
     *
     * @param setmeal 实体对象
     * @return 新增结果
     */
    @PostMapping
    public Result insert(String checkgroupIds,@RequestBody Setmeal setmeal) {
        if (setmeal.getId()!=null){
            return update(checkgroupIds,setmeal);
        }
        boolean save = setmealService.save(setmeal);
        if (StrUtil.isBlank(checkgroupIds)){
            return save? Result.success(ADD_SETMEAL_SUCCESS): Result.fail(ADD_SETMEAL_FAIL);
        }
        extracted(checkgroupIds,setmeal);
        return save? Result.success(ADD_SETMEAL_SUCCESS): Result.fail(ADD_SETMEAL_FAIL);
    }

    /**
     * 修改数据
     *
     * @param setmeal 实体对象
     * @return 修改结果
     */
    @PutMapping
    public Result update(String checkgroupIds,@RequestBody Setmeal setmeal) {
        if (checkgroupIds==null){
            return setmealService.updateById(setmeal)? Result.success(EDIT_SETMEAL_SUCCESS): Result.fail(EDITY_SETMEAL_FAIL);
        }
        //修改思路，先删除 在添加
        LambdaQueryWrapper<SetmealCheckgroup> lqw = new LambdaQueryWrapper<>();
        lqw.eq(SetmealCheckgroup::getSetmealId,setmeal.getId());
        setmealCheckgroupService.remove(lqw);

        extracted(checkgroupIds, setmeal);
        return setmealService.updateById(setmeal)? Result.success(EDIT_SETMEAL_SUCCESS): Result.fail(EDITY_SETMEAL_FAIL);
    }

    private void extracted(String checkgroupIds, Setmeal setmeal) {
        String[] split = checkgroupIds.split(",");
        Arrays.stream(split).forEach(item->{
            SetmealCheckgroup setmealCheckgroup = new SetmealCheckgroup();
            setmealCheckgroup.setCheckgroupId(Integer.valueOf(item));
            setmealCheckgroup.setSetmealId(setmeal.getId());
            setmealCheckgroupService.save(setmealCheckgroup);
        });
    }


    /**
     * 删除数据
     * @param setmeal
     * @return
     */
    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Setmeal setmeal) {

        //删除套餐
        LambdaQueryWrapper<SetmealCheckgroup> lqw = new LambdaQueryWrapper<>();

        //删除套餐中间表
        lqw.eq(SetmealCheckgroup::getSetmealId,setmeal.getId());

        List<SetmealCheckgroup> list = setmealCheckgroupService.list(lqw);

        //删除图片
        qiniuUtils.deleteFileFromQiniu(setmeal.getImg());

        if (CollectionUtil.isEmpty(list)){
            return setmealService.removeById(setmeal)? Result.success(DELETE_SETMEAL_SUCCESS): Result.fail(DELETE_SETMEAL_FAIL);
        }
        // 删除中间表
        setmealCheckgroupService.remove(lqw);
        // 删除检查项
        return setmealService.removeById(setmeal) ? Result.success(DELETE_SETMEAL_SUCCESS): Result.fail(DELETE_SETMEAL_FAIL);
    }
}


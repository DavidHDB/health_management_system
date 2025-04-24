package com.itheima.controller;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import com.itheima.domain.po.Result;
import com.itheima.exception.BusinessException;
import com.itheima.exception.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.HandlerInterceptor;

@RestControllerAdvice
@Slf4j
// TODO 捕获异常
public class ExceptionHandle implements HandlerInterceptor {
    //无角色异常
    @ExceptionHandler(NotRoleException.class)
    public Result handlerNotRoleException(NotRoleException nle){
        // 打印堆栈，以供调试
        nle.printStackTrace();
        String message = nle.getMessage();
        return Result.fail(message);
    }
    //无权限异常
    @ExceptionHandler(NotPermissionException.class)
    public Result handlerNotPermissionException(NotPermissionException nle){
        // 打印堆栈，以供调试
        nle.printStackTrace();
        String message = nle.getMessage();
        return Result.fail(message);
    }
    // 全局异常拦截（拦截项目中的NotLoginException异常）
    @ExceptionHandler(NotLoginException.class)
    public Result handlerNotLoginException(NotLoginException nle)
            throws Exception {

        // 打印堆栈，以供调试
        nle.printStackTrace();

        // 判断场景值，定制化异常信息
        String message = "";
        if(nle.getType().equals(NotLoginException.NOT_TOKEN)) {
            message = "未提供token";
        }
        else if(nle.getType().equals(NotLoginException.INVALID_TOKEN)) {
            message = "token无效";
        }
        else if(nle.getType().equals(NotLoginException.TOKEN_TIMEOUT)) {
            message = "token已过期";
        }
        else if(nle.getType().equals(NotLoginException.BE_REPLACED)) {
            message = "token已被顶下线";
        }
        else if(nle.getType().equals(NotLoginException.KICK_OUT)) {
            message = "token已被踢下线";
        }
        else {
            message = "当前会话未登录";
        }

        // 返回给前端
        return Result.fail(message);
    }
    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException e) {
        return Result.fail(e.getMsg());
    }

    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException e) {
        return Result.fail(e.getMsg());
    }

    @ExceptionHandler(Exception.class)
    public Result OtherException(Exception e) {
        return Result.fail(e.getMessage());
    }
}

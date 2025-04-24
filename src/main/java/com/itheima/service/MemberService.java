package com.itheima.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.domain.dto.MemberDTO;
import com.itheima.domain.po.Member;
import com.itheima.domain.po.Result;


public interface MemberService extends IService<Member> {

    Result SelectById(Integer page, Integer pageSize);

    Result Insert(MemberDTO memberDTO);

    Result Delete(MemberDTO memberDTO);

    Result Update(MemberDTO memberDTO);
}


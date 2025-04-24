package com.itheima.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.domain.dto.MemberDTO;
import com.itheima.domain.po.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface MemberMapper extends BaseMapper<Member> {

    @Select("select  t1.name as username ,t1.idcard,t1.phonenumber,t3.name as setmealname,t3.remark,t3.img,t2.ordertype,t2.orderdate from t_member t1,t_order t2 ,t_setmeal t3 where t1.id=t2.member_id and t2.setmeal_id=t3.id LIMIT #{page},#{pageSize}")
    public List<MemberDTO> selectById(Integer page, Integer pageSize) ;

    @Select("select  id from t_member where name=#{name}")
    public Integer selectByName(String name);
}


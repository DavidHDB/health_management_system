package com.itheima.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (Member)表实体类
 *
 * @author 柠檬吖
 * @since 2023-02-10 14:20:54
 */
@Data
@NoArgsConstructor
public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String filenumber;

    private String name;

    private String sex;

    private String idcard;

    private String phonenumber;

    private Date regtime;

    private String password;

    private String email;

    private Date birthday;

    private String remark;

}


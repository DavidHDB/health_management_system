package com.itheima.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private Date birthday;

    private String gender;

    private String username;

    private String password;

    private String remark;

    private String station;

    private String telephone;

}


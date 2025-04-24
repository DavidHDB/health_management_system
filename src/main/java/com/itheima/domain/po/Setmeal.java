package com.itheima.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class Setmeal implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String code;

    private String helpcode;

    private String sex;

    private String age;

    private Float price;

    private String remark;

    private String attention;

    private String img;

}


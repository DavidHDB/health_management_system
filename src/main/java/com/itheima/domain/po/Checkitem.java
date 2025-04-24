package com.itheima.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class Checkitem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String code;

    private String name;

    private String sex;

    private String age;

    private Object price;

    /**
     * 查检项类型,分为检查和检验两种
     */
    private String type;

    private String attention;

    private String remark;

}


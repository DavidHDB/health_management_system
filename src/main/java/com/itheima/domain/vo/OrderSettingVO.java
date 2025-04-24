package com.itheima.domain.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderSettingVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orderdate;
    private Integer number;
    private Integer reservations;
}

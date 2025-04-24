package com.itheima.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (Order)表实体类
 *
 * @author 柠檬吖
 * @since 2023-02-09 15:51:01
 */
@Data
@NoArgsConstructor
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String ORDERTYPE_TELEPHONE = "电话预约";
    public static final String ORDERTYPE_WEIXIN = "微信预约";
    public static final String ORDERSTATUS_YES = "已到诊";
    public static final String ORDERSTATUS_NO = "未到诊";
    private Integer id;

    /**
     * 员会id
     */
    private Integer memberId;

    /**
     * 约预日期
     */
    private Date orderdate;

    /**
     * 约预类型 电话预约/微信预约
     */
    private String ordertype;

    /**
     * 预约状态（是否到诊）
     */
    private String orderstatus;

    /**
     * 餐套id
     */
    private Integer setmealId;

}


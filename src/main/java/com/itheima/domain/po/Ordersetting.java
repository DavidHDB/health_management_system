package com.itheima.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@NoArgsConstructor
public class Ordersetting implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 约预日期
     */
    private Date orderdate;

    /**
     * 可预约人数
     */
    private Integer number;

    /**
     * 已预约人数
     */
    private Integer reservations;

}


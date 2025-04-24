package com.itheima.domain.dto;


import com.itheima.domain.po.Order;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderDTO extends Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private String member;
    private String setmeal;
}

package com.itheima.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersettingDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer data;
    private Integer number;
    private Integer reservations;
}

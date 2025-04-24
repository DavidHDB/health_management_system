package com.itheima.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String keyword;

    private String description;

}


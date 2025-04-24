package com.itheima.domain.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name;

    private String linkurl;

    private String path;

    private Integer priority;

    private String icon;

    private String description;

    private Integer parentmenuid;

    private Integer level;

}


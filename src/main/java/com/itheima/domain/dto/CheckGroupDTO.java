package com.itheima.domain.dto;


import com.itheima.domain.po.Checkgroup;
import com.itheima.domain.po.Checkitem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class CheckGroupDTO extends Checkgroup implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Checkitem> checkItems;

}

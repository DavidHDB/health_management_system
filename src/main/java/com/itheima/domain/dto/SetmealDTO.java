package com.itheima.domain.dto;


import com.itheima.domain.po.Setmeal;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SetmealDTO extends Setmeal implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<CheckGroupDTO> checkGroups;
}

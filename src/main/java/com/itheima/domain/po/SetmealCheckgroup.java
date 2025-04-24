package com.itheima.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class SetmealCheckgroup implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "setmeal_id", type = IdType.ASSIGN_ID)
    private Integer setmealId;

    private Integer checkgroupId;

}


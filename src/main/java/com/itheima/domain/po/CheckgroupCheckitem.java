package com.itheima.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@TableName("t_checkgroup_checkitem")
public class CheckgroupCheckitem implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private Integer checkgroupId;

    private Integer checkitemId;

}


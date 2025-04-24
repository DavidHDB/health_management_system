package com.itheima.domain.po;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Constitution implements Serializable {
    private Integer id;
    private String memberName;
    private String gender;
    private Integer age;
    private String constitutionType;
    private Integer score;
    private String analysis;
    private String suggestion;
    private Date assessmentTime;
}
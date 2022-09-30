package com.hr.health.system.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class People implements Serializable {
    private Long id;
    private String card;
    private String name;
    private Integer age;
    private String score;
    private Long courseId;
    private Integer deleted;
}

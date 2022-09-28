package com.hr.health.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.sun.javafx.beans.IDProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "student")
public class Student implements Serializable {


    @TableId(value = "id")
    private Long id;

    @TableField("card")
    private String card;

    @TableField("name")
    private String name;

    @TableField("score")
    private String score;

    @TableField("course_id")
    private Long courseId;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}

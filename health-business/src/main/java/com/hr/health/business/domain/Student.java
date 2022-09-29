package com.hr.health.business.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.hr.health.common.annotation.Excel;
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
    @Excel(name = "学生序号", cellType = Excel.ColumnType.STRING, prompt = "用户编号")
    private Long id;

    @TableField("card")
    @Excel(name = "学生卡号")
    private String card;

    @TableField("name")
    @Excel(name = "学生名称")
    private String name;

    @TableField("score")
    @Excel(name = "学生分数",cellType= Excel.ColumnType.NUMERIC)
    private String score;

    @TableField("course_id")
    private Long courseId;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;
}

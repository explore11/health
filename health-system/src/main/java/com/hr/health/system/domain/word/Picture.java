package com.hr.health.system.domain.word;

import lombok.Data;

import java.io.ByteArrayOutputStream;

@Data
public class Picture {
    /**
     * word 中的文件名称
     */
    String pictureName;
    /**
     * 存储在磁盘中的文件名称（带路径），为了方便生成 word 文档
     */
    String fileName;
    /**
     * 图片输入流
     */
    ByteArrayOutputStream fos;
}
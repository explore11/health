package com.hr.health.system.domain.word;

import lombok.Data;

@Data
public class Word {
    /**
     * 文件扩展名 doc,docx
     */
    String fileExt;
    /**
     * 模板路径
     */
    String templatePath;
    /**
     * ftl 模板文件名称
     */
    String templateName;
    /**
     * docx zip 模板文件名称
     */
    String templateZipName;
    /**
     * 要生成的 word 的文件名
     */
    String docName;
    /**
     * 替换模板占位符的数据
     */
    Object data;
}

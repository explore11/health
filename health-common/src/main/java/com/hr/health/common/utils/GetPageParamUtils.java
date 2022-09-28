package com.hr.health.common.utils;

import com.hr.health.common.core.page.TableSupport;
import com.hr.health.common.core.text.Convert;

public class GetPageParamUtils {
    public static Integer getPageNum() {
        Integer pageNum = Convert.toInt(ServletUtils.getParameter(TableSupport.PAGE_NUM));
        return pageNum == null ? 0 : pageNum;
    }

    public static Integer getPageSize() {
        Integer pageSize = Convert.toInt(ServletUtils.getParameter(TableSupport.PAGE_SIZE));
        return pageSize == null ? 10 : pageSize;
    }
}

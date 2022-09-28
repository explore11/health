package com.hr.health.common.exception.file;

/**
 * 文件名大小限制异常类
 *
 * @author swq
 */
public class FileSizeLimitExceededException extends FileException {
    private static final long serialVersionUID = 1L;

    public FileSizeLimitExceededException(long defaultMaxSize) {
        super("upload.exceed.maxSize", new Object[]{defaultMaxSize});
    }

    public FileSizeLimitExceededException(long defaultMaxSize, String unit) {
        super("upload.exceed.maxSize", new Object[]{defaultMaxSize + unit});
    }
}

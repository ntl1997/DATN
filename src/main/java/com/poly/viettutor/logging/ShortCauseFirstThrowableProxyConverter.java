package com.poly.viettutor.logging;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.IThrowableProxy;

public class ShortCauseFirstThrowableProxyConverter extends ThrowableProxyConverter {

    @Override
    protected String throwableProxyToString(IThrowableProxy tp) {
        if (tp == null)
            return "";

        // Tìm nguyên nhân gốc sâu nhất
        IThrowableProxy root = tp;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        // Trả về message lỗi đầu tiên
        return "Caused by: " + root.getClassName() + ": " + root.getMessage() + "\n";
    }
}
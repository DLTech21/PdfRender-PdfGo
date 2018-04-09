package com.dl.pdfgo.utils;

import com.blankj.utilcode.util.ObjectUtils;
import com.dl.pdfgo.PdfGo;

/**
 * Created by donal on 2018/4/9.
 */

public class PdfUtils {
    public static <T> T checkNotNull(T object, String message) {
        if (ObjectUtils.isEmpty(object)) {
            throw new NullPointerException(message);
        }
        return object;
    }

    public static void runOnUiThread(Runnable runnable) {
        PdfGo.getInstance().getDelivery().post(runnable);
    }
}

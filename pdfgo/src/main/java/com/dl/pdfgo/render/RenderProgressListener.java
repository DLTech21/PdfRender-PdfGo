package com.dl.pdfgo.render;

import com.dl.pdfgo.model.PdfProgress;

/**
 * Created by donal on 2018/3/12.
 */

public interface RenderProgressListener<T> {
    /**
     * 成功添加任务的回调
     */
    void onStart(PdfProgress progress);

    /**
     * render进行时回调
     */
    void onProgress(PdfProgress progress);

    /**
     * render出错时回调
     */
    void onError(PdfProgress progress);

    /**
     * render完成时回调
     */
    void onFinish(T t, PdfProgress progress);

    /**
     * 被移除时回调
     */
    void onRemove(PdfProgress progress);
}

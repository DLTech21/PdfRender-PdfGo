package com.dl.pdfgo.model;

import android.view.View;

import com.dl.pdfgo.utils.PdfConsumer;

import java.io.Serializable;

/**
 * Created by donal on 2018/3/9.
 */

public class PdfProgress implements Serializable {

    public static final int NONE = 0;         //无状态
    public static final int WAITING = 1;      //等待
    public static final int LOADING = 2;      //下载中
    public static final int PAUSE = 3;        //暂停
    public static final int ERROR = 4;        //错误
    public static final int FINISH = 5;       //完成

    public static final String TAG = "tag";
    public static final String FOLDER = "folder";
    public static final String FILE_PATH = "filePath";
    public static final String FILE_NAME = "fileName";
    public static final String STATUS = "status";
    public static final String PRIORITY = "priority";
    public static final String DATE = "date";

    public String tag;                              //render的标识键(pdfFilePath的hash|currentPage|.png)
    public String pdfFilePath;
    public int currentPage;
    public String filePath;                         //保存文件地址
    public int status;                              //当前状态
    public int priority;                            //任务优先级
    public long date;                               //创建时间
    public Throwable exception;                     //当前进度出现的异常
    public View view;
    public PdfConsumer pdfManager;

    public PdfProgress() {
        priority = Priority.DEFAULT;
        date = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PdfProgress progress = (PdfProgress) o;
        return tag != null ? tag.equals(progress.tag) : progress.tag == null;

    }

    @Override
    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }
}

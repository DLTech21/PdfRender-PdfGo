package com.dl.pdfgo;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.blankj.utilcode.util.FileUtils;
import com.dl.pdfgo.model.PdfProgress;
import com.dl.pdfgo.render.PdfRenderTask;
import com.dl.pdfgo.render.PdfRenderThreadPool;
import com.dl.pdfgo.task.XExecutor;
import com.dl.pdfgo.utils.PdfConsumer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by donal on 2018/3/9.
 */

public class PdfGo {
    private Handler mDelivery;              //用于在主线程执行的调度器
    private String folder;
    private PdfRenderThreadPool threadPool;                      //render的线程池
    private ConcurrentHashMap<String, PdfRenderTask> taskMap;    //所有任务

    public static PdfGo getInstance() {
        return PdfRenderHolder.instance;
    }

    private static class PdfRenderHolder {
        private static final PdfGo instance = new PdfGo();
    }

    private PdfGo() {
        mDelivery = new Handler(Looper.getMainLooper());
        folder = Environment.getExternalStorageDirectory() + File.separator + "pdfgo" + File.separator;
        FileUtils.createOrExistsDir(folder);
        threadPool = new PdfRenderThreadPool();
        taskMap = new ConcurrentHashMap<>();

    }

    public static PdfRenderTask request(String tag, String pdfFilePath, int page, PdfConsumer pdfManager) {
        Map<String, PdfRenderTask> taskMap = PdfGo.getInstance().getTaskMap();
        PdfRenderTask task = taskMap.get(tag);
        if (task == null) {
            task = new PdfRenderTask(tag, pdfFilePath, page, pdfManager);
            taskMap.put(tag, task);
        }
        return task;
    }

    /**
     * 从数据库中恢复任务
     */
    public static PdfRenderTask restore(PdfProgress progress) {
        Map<String, PdfRenderTask> taskMap = PdfGo.getInstance().getTaskMap();
        PdfRenderTask task = taskMap.get(progress.tag);
        if (task == null) {
            task = new PdfRenderTask(progress);
            taskMap.put(progress.tag, task);
        }
        return task;
    }

    /**
     * 从数据库中恢复任务
     */
    public static List<PdfRenderTask> restore(List<PdfProgress> progressList) {
        Map<String, PdfRenderTask> taskMap = PdfGo.getInstance().getTaskMap();
        List<PdfRenderTask> tasks = new ArrayList<>();
        for (PdfProgress progress : progressList) {
            PdfRenderTask task = taskMap.get(progress.tag);
            if (task == null) {
                task = new PdfRenderTask(progress);
                taskMap.put(progress.tag, task);
            }
            tasks.add(task);
        }
        return tasks;
    }

    /**
     * 开始所有任务
     */
    public void startAll() {
        for (Map.Entry<String, PdfRenderTask> entry : taskMap.entrySet()) {
            PdfRenderTask task = entry.getValue();
            if (task == null) {
                continue;
            }
            task.start();
        }
    }

    /**
     * 暂停全部任务
     */
    public void pauseAll() {
        //先停止未开始的任务
        for (Map.Entry<String, PdfRenderTask> entry : taskMap.entrySet()) {
            PdfRenderTask task = entry.getValue();
            if (task == null) {
                continue;
            }
            if (task.progress.status != PdfProgress.LOADING) {
                task.pause();
            }
        }
        //再停止进行中的任务
//        for (Map.Entry<String, PdfRenderTask> entry : taskMap.entrySet()) {
//            PdfRenderTask task = entry.getValue();
//            if (task == null) {
//                OkLogger.w("can't find task with tag = " + entry.getKey());
//                continue;
//            }
//            if (task.progress.status == Progress.LOADING) {
//                task.pause();
//            }
//        }
    }

    /**
     * 删除所有任务
     */
    public void removeAll() {
        removeAll(false);
    }

    /**
     * 删除所有任务
     *
     * @param isDeleteFile 删除任务是否删除文件
     */
    public void removeAll(boolean isDeleteFile) {
        Map<String, PdfRenderTask> map = new HashMap<>(taskMap);
        //先删除未开始的任务
        for (Map.Entry<String, PdfRenderTask> entry : map.entrySet()) {
            PdfRenderTask task = entry.getValue();
            if (task == null) {
                continue;
            }
            if (task.progress.status != PdfProgress.LOADING) {
                task.remove(isDeleteFile);
            }
        }
        //再删除进行中的任务
//        for (Map.Entry<String, PdfRenderTask> entry : map.entrySet()) {
//            PdfRenderTask task = entry.getValue();
//            if (task == null) {
//                OkLogger.w("can't find task with tag = " + entry.getKey());
//                continue;
//            }
//            if (task.progress.status == Progress.LOADING) {
//                task.remove(isDeleteFile);
//            }
//        }
    }

    public PdfRenderThreadPool getThreadPool() {
        return threadPool;
    }

    public Map<String, PdfRenderTask> getTaskMap() {
        return taskMap;
    }

    public PdfRenderTask getTask(String tag) {
        return taskMap.get(tag);
    }

    public boolean hasTask(String tag) {
        return taskMap.containsKey(tag);
    }

    public PdfRenderTask removeTask(String tag) {
        return taskMap.remove(tag);
    }

    public void addOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
        threadPool.getExecutor().addOnAllTaskEndListener(listener);
    }

    public void removeOnAllTaskEndListener(XExecutor.OnAllTaskEndListener listener) {
        threadPool.getExecutor().removeOnAllTaskEndListener(listener);
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}

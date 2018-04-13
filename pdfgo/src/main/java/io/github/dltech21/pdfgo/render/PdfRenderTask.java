package io.github.dltech21.pdfgo.render;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.FileUtils;
import io.github.dltech21.pdfgo.PdfGo;
import io.github.dltech21.pdfgo.utils.PdfConsumer;
import io.github.dltech21.pdfgo.task.PriorityRunnable;
import io.github.dltech21.pdfgo.model.PdfProgress;
import io.github.dltech21.pdfgo.utils.PdfUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by donal on 2018/3/12.
 */

public class PdfRenderTask implements Runnable {


    public PdfProgress progress;
    public Map<Object, PdfRenderListener> listeners;
    private ThreadPoolExecutor executor;
    private PriorityRunnable priorityRunnable;

    public PdfRenderTask(String tag, String pdfFilePath, int page, PdfConsumer pdfManager) {
        PdfUtils.checkNotNull(tag, "tag == null");
        progress = new PdfProgress();
        progress.pdfFilePath = pdfFilePath;
        progress.currentPage = page;
        progress.tag = tag;
        progress.status = PdfProgress.NONE;
        File file = new File(PdfGo.getInstance().getFolder(), tag);
        progress.filePath = file.getAbsolutePath();
        progress.pdfManager = pdfManager;
        executor = PdfGo.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    public PdfRenderTask(PdfProgress progress) {
        PdfUtils.checkNotNull(progress, "progress == null");
        this.progress = progress;
        executor = PdfGo.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    public PdfRenderTask priority(int priority) {
        progress.priority = priority;
        return this;
    }

    public PdfRenderTask register(PdfRenderListener listener) {
        if (listener != null) {
            listeners.put(listener.tag, listener);
        }
        return this;
    }

    public void unRegister(PdfRenderListener listener) {
        PdfUtils.checkNotNull(listener, "listener == null");
        listeners.remove(listener.tag);
    }

    public void unRegister(String tag) {
        PdfUtils.checkNotNull(tag, "tag == null");
        listeners.remove(tag);
    }

    public void start() {
//        if (PdfGo.getInstance().getTask(progress.tag) == null) {
//            throw new IllegalStateException("you must call DownloadTask#save() before DownloadTask#start()！");
//        }
        if (progress.status == PdfProgress.NONE || progress.status == PdfProgress.PAUSE || progress.status == PdfProgress.ERROR) {
            File file = new File(progress.filePath);
            if (file.exists()) {
                postOnFinish(progress, new File(progress.filePath));
            } else {
                postOnStart(progress);
                postWaiting(progress);
                priorityRunnable = new PriorityRunnable(progress.priority, this);
                executor.execute(priorityRunnable);
            }
        } else if (progress.status == PdfProgress.FINISH) {
            if (progress.filePath == null) {
                postOnError(progress, new Exception("the file of the task with tag:" + progress.tag + " may be invalid or damaged, please call the method restart() to download again！"));
            } else {
                File file = new File(progress.filePath);
                if (file.exists()) {
                    postOnFinish(progress, new File(progress.filePath));
                } else {
                    postOnError(progress, new Exception("the file " + progress.filePath + " may be invalid or damaged, please call the method restart() to download again！"));
                }
            }
        } else {
            postLoading(progress);
        }
    }

    public void restart() {
        pause();
        FileUtils.deleteAllInDir(progress.filePath);
        progress.status = PdfProgress.NONE;
        start();
    }

    /**
     * 暂停的方法
     */
    public void pause() {
        executor.remove(priorityRunnable);
        if (progress.status == PdfProgress.WAITING) {
            postPause(progress);
        } else if (progress.status == PdfProgress.LOADING) {
            progress.status = PdfProgress.PAUSE;
        }
    }

    /**
     * 删除一个任务,会删除下载文件
     */
    public void remove() {
        remove(false);
    }

    /**
     * 删除一个任务,会删除下载文件
     */
    public PdfRenderTask remove(boolean isDeleteFile) {
        pause();
        if (isDeleteFile) FileUtils.deleteAllInDir(progress.filePath);
        PdfRenderTask task = PdfGo.getInstance().removeTask(progress.tag);
        postOnRemove(progress);
        return task;
    }

    @Override
    public void run() {
        try {
            render(progress);
        } catch (Exception e) {
            postOnError(progress, e);
            return;
        }
        //check finish status
        if (progress.status == PdfProgress.PAUSE) {
            postPause(progress);
        } else if (progress.status == PdfProgress.LOADING) {
            if (new File(progress.filePath).exists()) {
                postOnFinish(progress, new File(progress.filePath));
            }
        }
    }

    private void render(PdfProgress progress) throws IOException {
        progress.status = PdfProgress.LOADING;
        progress.pdfManager.getPdfiumCore().openPage(progress.pdfManager.getPdfDocument(), progress.currentPage);
        int width = (int) (progress.pdfManager.getPdfiumCore().getPageWidthPoint(progress.pdfManager.getPdfDocument(), progress.currentPage) * 1.5);
        int height = (int) (progress.pdfManager.getPdfiumCore().getPageHeightPoint(progress.pdfManager.getPdfDocument(), progress.currentPage) * 1.5);
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        progress.pdfManager.getPdfiumCore().renderPageBitmap(progress.pdfManager.getPdfDocument(), bitmap, progress.currentPage, 0, 0,
                width, height, true);
        if (bitmap != null) {
            saveImage(progress.tag, bitmap);
            bitmap.recycle();
        }
    }

    private void saveImage(String key, Bitmap bmp) {
        File file = new File(PdfGo.getInstance().getFolder(), key);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postOnStart(final PdfProgress progress) {
        progress.status = PdfProgress.NONE;
        PdfUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PdfRenderListener listener : listeners.values()) {
                    listener.onStart(progress);
                }
            }
        });
    }

    private void postWaiting(final PdfProgress progress) {
        progress.status = PdfProgress.WAITING;
        PdfUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PdfRenderListener listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postPause(final PdfProgress progress) {
        progress.status = PdfProgress.PAUSE;
        PdfUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PdfRenderListener listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postLoading(final PdfProgress progress) {
        PdfUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PdfRenderListener listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postOnError(final PdfProgress progress, final Throwable throwable) {
        progress.status = PdfProgress.ERROR;
        progress.exception = throwable;
        PdfUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PdfRenderListener listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onError(progress);
                }
            }
        });
    }

    private void postOnFinish(final PdfProgress progress, final File file) {
        progress.status = PdfProgress.FINISH;
        PdfUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PdfRenderListener listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onFinish(file, progress);
                }
            }
        });
    }

    private void postOnRemove(final PdfProgress progress) {
        PdfUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (PdfRenderListener listener : listeners.values()) {
                    listener.onRemove(progress);
                }
                listeners.clear();
            }
        });
    }
}

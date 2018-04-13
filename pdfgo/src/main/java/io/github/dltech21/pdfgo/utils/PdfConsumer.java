
package io.github.dltech21.pdfgo.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.ParcelFileDescriptor;

import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;

import java.io.File;

/**
 * Created by donal on 2018/3/13.
 */

public class PdfConsumer {
    private PdfiumCore pdfiumCore;
    private PdfDocument pdfDocument;
    private String filePath;
    private int count = -1;

    public PdfConsumer(Context context, String path) throws Exception {
        this.filePath = path;
        ParcelFileDescriptor fd = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_ONLY);
        pdfiumCore = new PdfiumCore(context);
        pdfDocument = pdfiumCore.newDocument(fd);
    }

    public void onDestroy() {
        try {
            if (pdfiumCore != null && pdfDocument != null) {
                pdfiumCore.closeDocument(pdfDocument);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPageCount() {
        if (count == -1) {
            count = pdfiumCore.getPageCount(pdfDocument);
        }
        return count;
    }

    public String getFilePath() {
        return filePath;
    }

    public PdfiumCore getPdfiumCore() {
        return pdfiumCore;
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public Point getPageScreen(int pageNum) {
        return getPageScreen(pageNum, 1);
    }

    public Point getPageScreen(int pageNum, float scale) {
        pdfiumCore.openPage(pdfDocument, pageNum);
        int width = (int) (pdfiumCore.getPageWidthPoint(pdfDocument, pageNum) * scale);
        int height = (int) (pdfiumCore.getPageHeightPoint(pdfDocument, pageNum) * scale);

        Point p = new Point(width, height);
        return p;
    }
}

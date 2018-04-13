package io.github.dltech21.pdfgo.render;

import java.io.File;

/**
 * Created by donal on 2018/3/12.
 */

public abstract class PdfRenderListener implements RenderProgressListener<File> {

    public final Object tag;

    public PdfRenderListener(Object tag) {
        this.tag = tag;
    }
}

package com.dl.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dl.pdfgo.utils.PdfConsumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Donal on 2017/6/19.
 */

public class PdfListActivity extends Activity {
    private RecyclerView rvPhoto;
    private PdfListAdapter mPdfListAdapter;
    PdfConsumer pdfConsumer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);
        try {
            File file = fileFromAsset(this, "sample.pdf");
            pdfConsumer = new PdfConsumer(this, file.getAbsolutePath());
            initView();
        } catch (Exception e) {

        }

    }

    public void initView() {
        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(this, 3));
        mPdfListAdapter = new PdfListAdapter(this, pdfConsumer.getPageCount(), pdfConsumer);
        rvPhoto.setAdapter(mPdfListAdapter);

    }


    private File fileFromAsset(Context context, String assetName) throws IOException {
        File outFile = new File(context.getCacheDir(), "pdfview.pdf");
        if (assetName.contains("/")) {
            outFile.getParentFile().mkdirs();
        }
        copy(context.getAssets().open(assetName), outFile);
        return outFile;
    }

    private void copy(InputStream inputStream, File output) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(output);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } finally {
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        }
    }


}

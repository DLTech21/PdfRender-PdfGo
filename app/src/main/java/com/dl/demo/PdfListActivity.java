package com.dl.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import io.github.dltech21.dlfilepicker.DLFilePicker;
import io.github.dltech21.dlfilepicker.DLFilePickerSelectListener;
import io.github.dltech21.dlfilepicker.model.FileItem;
import io.github.dltech21.pdfgo.utils.PdfConsumer;


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
        initView();

        DLFilePicker.getInstance().selectFile(this, new String[]{".pdf"}, new DLFilePickerSelectListener() {
            @Override
            public void onSuccess(List<FileItem> files) {
                try {
                    pdfConsumer = new PdfConsumer(PdfListActivity.this, files.get(0).getFilePath());
                    mPdfListAdapter = new PdfListAdapter(PdfListActivity.this, pdfConsumer.getPageCount(), pdfConsumer);
                    rvPhoto.setAdapter(mPdfListAdapter);
                } catch (Exception e) {

                }
            }
        });

    }

    public void initView() {
        rvPhoto = (RecyclerView) findViewById(R.id.rv_photo);
        rvPhoto.setLayoutManager(new GridLayoutManager(this, 3));
    }


}

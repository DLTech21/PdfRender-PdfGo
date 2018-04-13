package com.dl.demo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;

import io.github.dltech21.pdfgo.PdfGo;
import io.github.dltech21.pdfgo.model.PdfProgress;
import io.github.dltech21.pdfgo.render.PdfRenderListener;
import io.github.dltech21.pdfgo.render.PdfRenderTask;
import io.github.dltech21.pdfgo.utils.EncryptUtils;
import io.github.dltech21.pdfgo.utils.PdfConsumer;


/**
 * Created by Donal on 2017/6/19.
 */

public class PdfListAdapter extends RecyclerView.Adapter<PdfListAdapter.ViewHolder> {

    private Context mContext;
    private int count;
    private String filehash;
    private PdfConsumer pdfConsumer;
    DisplayImageOptions options;

    public PdfListAdapter(Context mContext, int count, PdfConsumer pdfConsumer) {
        this.mContext = mContext;
        this.count = count;
        this.pdfConsumer = pdfConsumer;
        this.filehash = EncryptUtils.encryptSHA256File2String(pdfConsumer.getFilePath());
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public PdfListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PdfListAdapter.ViewHolder holder = new PdfListAdapter.ViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.item_pdf, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final PdfListAdapter.ViewHolder holder, final int position) {
        String tag = filehash + "_" + position + ".png";
        PdfRenderTask pdfRenderTask = PdfGo.request(tag, pdfConsumer.getFilePath(), position, pdfConsumer);
        pdfRenderTask.register(new PdfRenderListener(tag) {
            @Override
            public void onStart(PdfProgress progress) {
//                holder.imageView.setImageResource(R.drawable.pdf_img);
            }

            @Override
            public void onProgress(PdfProgress progress) {
//                holder.imageView.setImageResource(R.drawable.pdf_img);
            }

            @Override
            public void onError(PdfProgress progress) {

            }

            @Override
            public void onFinish(File file, PdfProgress progress) {

                com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage("file://" + file.getAbsolutePath(), holder.imageView, options);
            }

            @Override
            public void onRemove(PdfProgress progress) {

            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        private View pdfLl;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_thumb);
        }

    }

}

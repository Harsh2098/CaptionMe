package com.hmproductions.captionme.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmproductions.captionme.R;
import com.hmproductions.captionme.data.CaptionContract.CaptionEntry;
import com.hmproductions.captionme.utils.BitmapAsyncTask;

/**
 * Created by Harsh Mahajan on 12/6/2017.
 */

public class CaptionAdapter extends CursorAdapter {

    public CaptionAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ImageView captionImage = (ImageView) view.findViewById(R.id.caption_imageView);
        TextView caption = (TextView)view.findViewById(R.id.caption_textView);

        captionImage.setImageResource(R.mipmap.ic_loading);

        BitmapAsyncTask bitmapAsyncTask = new BitmapAsyncTask(context, captionImage);
        bitmapAsyncTask.execute(cursor.getString(cursor.getColumnIndexOrThrow(CaptionEntry.COLUMN_IMAGE_PATH)));
        caption.setText(cursor.getString(cursor.getColumnIndexOrThrow(CaptionEntry.COLUMN_CAPTION)));
    }
}

package com.hmproductions.captionme.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hmproductions.captionme.R;
import com.hmproductions.captionme.data.CaptionContract;
import com.hmproductions.captionme.data.CaptionContract.CaptionEntry;

import java.io.IOException;

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

        Bitmap bitmap;

        Uri imageUri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(CaptionEntry.COLUMN_IMAGE_PATH)));

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursorData = context.getContentResolver().query(imageUri, filePathColumn, null, null, null);
        cursorData.moveToFirst();
        String picturePath = cursorData.getString(cursorData.getColumnIndexOrThrow(filePathColumn[0]));

        // BitmapFactory.Options has been used to reduce the Image rendering size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        bitmap = BitmapFactory.decodeFile(picturePath, options);

        // Scaling bitmap further
        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);


        // Setting image and captions
        ImageView captionImage = (ImageView) view.findViewById(R.id.caption_imageView);
        captionImage.setImageBitmap(scaled);

        TextView caption = (TextView)view.findViewById(R.id.caption_textView);
        caption.setText(cursor.getString(cursor.getColumnIndexOrThrow(CaptionEntry.COLUMN_CAPTION)));
    }
}

package com.hmproductions.captionme.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.ImageView;


/**
 * Created by Harsh Mahajan on 18/6/2017.
 */

public class BitmapAsyncTask extends AsyncTask<String,Void,Bitmap> {

    private ImageView mImageView;
    private Context mContext;

    public BitmapAsyncTask(Context context, ImageView imageView)
    {
        mImageView = imageView;
        mContext = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        Uri imageUri = Uri.parse(params[0]);

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursorData = mContext.getContentResolver().query(imageUri, filePathColumn, null, null, null);
        cursorData.moveToFirst();
        String picturePath = cursorData.getString(cursorData.getColumnIndexOrThrow(filePathColumn[0]));

        // BitmapFactory.Options has been used to reduce the Image rendering size
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, options);

        // Scaling bitmap further
        int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
        return Bitmap.createScaledBitmap(bitmap, 512, nh, true);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }
}

package com.hmproductions.captionme.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Harsh Mahajan on 12/6/2017.
 */

public class CaptionContract {

    static final String CONTENT_AUTHORITY = "com.hmproductions.captionme";
    private static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_CAPTION = "caption";

    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_CAPTION);

    public class CaptionEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "caption";

        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_IMAGE_PATH = "image_path_uri";
        public static final String COLUMN_CAPTION = "caption";
    }
}

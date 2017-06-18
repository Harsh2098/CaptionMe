package com.hmproductions.captionme.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by Harsh Mahajan on 16/6/2017.
 */

public class PermissionUtils {

    /* Permission Codes */
    private static final int CAMERA_PERMISSION_CODE = 100 ;
    private static final int READ_PERMISSION_CODE = 101;
    private static final int WRITE_PERMISSION_CODE = 102;

    private static boolean mCameraPermission, mReadStoragePermission, mWriteStoragePermission;
    static int permissionCode;

    public PermissionUtils() {
        mCameraPermission = false;
        mReadStoragePermission = false;
        mWriteStoragePermission = false;
    }

    public static boolean CheckAllPermissions(Context context)
    {
        CheckPermission(context, Manifest.permission.CAMERA);
        CheckPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        CheckPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return mCameraPermission && mReadStoragePermission && mWriteStoragePermission;
    }

    private static void CheckPermission(Context context, String permission)
    {
        if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale((Activity)context, permission))
            {
                Toast.makeText(context, "Permission Denied", Toast.LENGTH_LONG).show();
            }

            permissionCode = permission.equals(Manifest.permission.CAMERA)?CAMERA_PERMISSION_CODE:permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)?READ_PERMISSION_CODE:WRITE_PERMISSION_CODE;
            ActivityCompat.requestPermissions((Activity)context, new String[]{permission}, permissionCode);
        }

        else if(permission.equals(Manifest.permission.CAMERA))
            mCameraPermission = true;
        else if(permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE))
            mReadStoragePermission = true;
        else if(permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            mWriteStoragePermission = true;
    }

    /* Setters for the boolean variables
    *
    *  @param boolean variable to set one of the static variable */
    public static void setCameraPermission(boolean cameraPermission) {
        mCameraPermission = cameraPermission;
    }

    public static void setReadStoragePermission(boolean readStoragePermission) {
        mReadStoragePermission = readStoragePermission;
    }

    public static void setWriteStoragePermission(boolean writeStoragePermission) {
        mWriteStoragePermission = writeStoragePermission;
    }
}

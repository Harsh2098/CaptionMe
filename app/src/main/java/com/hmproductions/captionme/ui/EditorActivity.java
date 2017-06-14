package com.hmproductions.captionme.ui;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hmproductions.captionme.R;
import com.hmproductions.captionme.data.CaptionContract;
import com.hmproductions.captionme.data.CaptionContract.CaptionEntry;
import com.yashoid.instacropper.InstaCropperView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = EditorActivity.class.getSimpleName();
    private static final String NO_CAPTION = "(no caption)";
    private static final int REQUEST_CODE_GALLERY = 1001;
    private static final int REQUEST_CODE_CAMERA = 1002 ;
    private static final int REQUEST_CODE_EDIT = 1003;
    private static final int LOADER_ID = 1000;

    ImageView caption_imageView;
    EditText caption_editText;

    Uri mCurrentUri, mImageUri;
    static boolean userChangedSomething;

    static AlertDialog.Builder addBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        caption_editText = (EditText)findViewById(R.id.caption_editText);
        caption_imageView = (ImageView) findViewById(R.id.instaCropperView);

        /* Create a Dialog Builder to add Image via Gallery or Camera */
        addBuilder = new AlertDialog.Builder(this);
        setupAddImageDialog();

        /* If the user doesn't change anything Up button navigates to MainActivity without show alert dialog */
        userChangedSomething = false;
        CaptionEditTextClickListener();

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        if(mCurrentUri == null) {
            setTitle("Add an Image");
            AddImageClickListener();
        }
        else
        {
            setTitle("Edit an Image");
            EditImageClickListener();
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    /* Sets up Add Image Dialog */
    private void setupAddImageDialog()
    {
        addBuilder
                .setTitle("Image Selection")
                .setMessage("How to do you want to select the image ?")
                .setPositiveButton("CAMERA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CODE_CAMERA);
                    }
                })
                .setNegativeButton("GALLERY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CODE_GALLERY);
                    }
                })
                .setCancelable(true)
                .setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    /* Inserts or updates data in the SQLite database according to the presence of mCurrentUri param */
    private boolean insertData()
    {
        ContentValues contentValues = new ContentValues();

        // Inserting Image Uri
        if(mImageUri == null)
        {
            Toast.makeText(this,"Please select an image", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            contentValues.put(CaptionEntry.COLUMN_IMAGE_PATH, String.valueOf(mImageUri));
        }

        // Inserting Caption
        if(caption_editText.getText().toString().equals("") || caption_editText.getText().toString().isEmpty())
        {
            contentValues.put(CaptionEntry.COLUMN_CAPTION, NO_CAPTION);
        }
        else
        {
            contentValues.put(CaptionEntry.COLUMN_CAPTION, caption_editText.getText().toString().trim());
        }

        if(mCurrentUri == null)
        {
            Uri uri = getContentResolver().insert(CaptionContract.CONTENT_URI, contentValues);
        }
        else
        {
            int noOfRowsUpdated = getContentResolver().update(mCurrentUri, contentValues, null,null);
        }

        return true;
    }

    /* If mCurrentUri IS null */
    private void AddImageClickListener()
    {
        caption_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                userChangedSomething = true;
                addBuilder.show();
            }
        });
    }

    /* If mCurrentUri IS NOT null */
    private void EditImageClickListener()
    {
        caption_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userChangedSomething = true;

                AlertDialog.Builder editBuilder = new AlertDialog.Builder(EditorActivity.this);
                editBuilder
                        .setTitle("Choose an option")
                        .setMessage("Do you want to edit or change the image?")
                        .setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent editIntent = new Intent(Intent.ACTION_EDIT);
                                editIntent.setDataAndType(mImageUri, "image/*");
                                editIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivityForResult(Intent.createChooser(editIntent, null), REQUEST_CODE_EDIT);
                            }
                        })
                        .setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addBuilder.show();
                            }
                        })
                        .show();
            }
        });
    }

    /* Sets userChangedSomething to true if it has been clicked once */
    private void CaptionEditTextClickListener()
    {
        caption_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userChangedSomething = true;
            }
        });
    }


    /* Handles the URI or the image returned from the result and assigns it to mImageUri*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_GALLERY)
        {
            if(data != null)
            {
                mImageUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(mImageUri, filePathColumn, null, null, null);
                cursor.moveToFirst();

                String picturePath = cursor.getString(cursor.getColumnIndexOrThrow(filePathColumn[0]));
                cursor.close();

                caption_imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        }

        else if(requestCode == REQUEST_CODE_CAMERA)
        {
            if(data!=null)
            {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                mImageUri = getImageUri(EditorActivity.this, imageBitmap);
                caption_imageView.setImageBitmap(imageBitmap);
            }
        }
    }

    /* Saves the image captured into external memory and returns the URI */
    public Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    /* Inflates and handles the menu items */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(mCurrentUri == null)
            menu.findItem(R.id.action_delete).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_done :
                boolean checkIfDone = insertData();
                if(checkIfDone) {
                    startActivity(new Intent(EditorActivity.this, MainActivity.class));
                }
                break;

            case R.id.action_delete :
                getContentResolver().delete(mCurrentUri, null,null);
                startActivity(new Intent(EditorActivity.this, MainActivity.class));
                break;

            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setMessage("Discard your changes or keeping editing?\nTo save changes, press the Tick icon above. ")
                        .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        }).setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true);

                if(userChangedSomething)
                    builder.show();
                else
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                break;
        }

        return true;
    }

    /* Getting the required data from database on a background thread */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                CaptionEntry.COLUMN_ID,
                CaptionEntry.COLUMN_IMAGE_PATH,
                CaptionEntry.COLUMN_CAPTION
        };

        return new CursorLoader(this, mCurrentUri, projection, null,null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if(cursor.moveToFirst())
        {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(CaptionEntry.COLUMN_IMAGE_PATH))));
            } catch (IOException e) {
                e.printStackTrace();
            }

            caption_imageView.setImageBitmap(bitmap);

            mImageUri = Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(CaptionEntry.COLUMN_IMAGE_PATH)));
            caption_editText.setText(cursor.getString(cursor.getColumnIndexOrThrow(CaptionEntry.COLUMN_CAPTION)));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // #YOLO
    }
}

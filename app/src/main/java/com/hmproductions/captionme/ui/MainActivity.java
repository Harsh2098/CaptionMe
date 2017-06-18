package com.hmproductions.captionme.ui;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hmproductions.captionme.R;
import com.hmproductions.captionme.adapters.CaptionAdapter;
import com.hmproductions.captionme.data.CaptionContract;
import com.hmproductions.captionme.data.CaptionContract.CaptionEntry;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1000;
    ListView caption_listView;
    View emptyView;
    FloatingActionButton floatingActionButton;
    CaptionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        caption_listView = (ListView) findViewById(R.id.caption_list);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.add_fab);
        emptyView = findViewById(R.id.empty_list_view);

        adapter = new CaptionAdapter(this, null);

        caption_listView.setAdapter(adapter);
        caption_listView.setEmptyView(emptyView);

        FloatingActionButtonClickListener();
        ListViewClickListener();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void FloatingActionButtonClickListener() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });
    }

    private void ListViewClickListener() {
        caption_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // TODO : *** POSITION != ID  ***
                Uri currentUri = ContentUris.withAppendedId(CaptionContract.CONTENT_URI, id);

                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.setData(currentUri);

                startActivity(intent);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                CaptionEntry.COLUMN_ID,
                CaptionEntry.COLUMN_IMAGE_PATH,
                CaptionEntry.COLUMN_CAPTION
        };

        return new CursorLoader(MainActivity.this, CaptionContract.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    /* Using the same menu as that for the Editor activity */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.editor_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.findItem(R.id.action_done).setVisible(false);

        if(adapter.getCount() == 0)
            menu.findItem(R.id.action_delete).setVisible(false);
        else
            menu.findItem(R.id.action_delete).setIcon(R.mipmap.ic_delete_forever3);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Permanent Deletion").setMessage("Do you want to erase all the data ?").setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).setPositiveButton("Erase", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int no_of_rows_deleted = getContentResolver().delete(CaptionContract.CONTENT_URI,null,null);
                }
            }).show();
        }

        return true;
    }
}

package com.hmproductions.captionme.ui;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hmproductions.captionme.adapters.CaptionAdapter;
import com.hmproductions.captionme.R;
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

        caption_listView = (ListView)findViewById(R.id.caption_list);
        floatingActionButton = (FloatingActionButton)findViewById(R.id.add_fab);
        emptyView = findViewById(R.id.empty_list_view);

        adapter = new CaptionAdapter(this, null);

        caption_listView.setAdapter(adapter);
        caption_listView.setEmptyView(emptyView);

        FloatingActionButtonClickListener();
        ListViewClickListener();

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    private void FloatingActionButtonClickListener()
    {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditorActivity.class));
            }
        });
    }

    private void ListViewClickListener()
    {
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
}

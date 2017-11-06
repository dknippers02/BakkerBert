package com.example.sqliteinsert;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sqliteinsert.data.UserContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    public static final int USER_LOADER = 0;
    private static final String[] USER_LOADER_PROJECTION = new String[] {
            UserContract.UserEntry._ID,
            UserContract.UserEntry.COLUMN_NAME,
            UserContract.UserEntry.COLUMN_PASSWORD
    };

    private UserCursorAdapter userCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        ListView usersListView = (ListView) this.findViewById(R.id.users_list_view);
        this.userCursorAdapter = new UserCursorAdapter(this, null);
        usersListView.setAdapter(userCursorAdapter);

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri selectedPetUri = ContentUris.withAppendedId(UserContract.UserEntry.CONTENT_URI, id);

                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                intent.setData(selectedPetUri);

                startActivity(intent);
            }
        });

        this.getLoaderManager().initLoader(USER_LOADER, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_user:
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, UserContract.UserEntry.CONTENT_URI, USER_LOADER_PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        this.userCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        this.userCursorAdapter.swapCursor(null);
    }
}

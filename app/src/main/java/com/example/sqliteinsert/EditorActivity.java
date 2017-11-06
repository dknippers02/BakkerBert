package com.example.sqliteinsert;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.sqliteinsert.data.UserContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Uri selectedUserUri;
    private Boolean unsavedChanges = false;

    private EditText nameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        selectedUserUri = getIntent().getData();

        if (selectedUserUri == null) {
            setTitle(R.string.add_user);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.edit_user);

            getLoaderManager().initLoader(CatalogActivity.USER_LOADER, null, this);
        }

        nameEditText = (EditText) findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);

        nameEditText.setOnTouchListener(onTouchListener);
        passwordEditText.setOnTouchListener(onTouchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_user:
                saveUser();
                break;
            case R.id.action_delete_user:
                showDeleteConfirmationDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveUser() {
        String name = nameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (selectedUserUri == null && name.isEmpty() && password.isEmpty())
            return;

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME, name);
        values.put(UserContract.UserEntry.COLUMN_PASSWORD, password);

        if (selectedUserUri == null) {
            // create new user
            Uri newUserUri;
            try {
                newUserUri = getContentResolver().insert(UserContract.UserEntry.CONTENT_URI, values);
            } catch (IllegalArgumentException e) {
                Message.message(this, e.getMessage());
                return;
            }

            if (newUserUri == null) {
                Message.message(this, getString(R.string.toast_add_user_failed));
            } else {
                Message.message(this, getString(R.string.toast_add_user_successful));
            }
        } else {
            // update selected user by Uri
            int rowsAffected;
            try {
                rowsAffected = getContentResolver().update(selectedUserUri, values, null, null);
            } catch (IllegalArgumentException e) {
                Message.message(this, e.getMessage());
                return;
            }

            if (rowsAffected == 0) {
                Message.message(this, getString(R.string.toast_edit_user_failed));
            } else {
                Message.message(this, getString(R.string.toast_edit_user_successful));
            }
        }
        finish();
    }

    public void deleteUser() {
        if (selectedUserUri != null) {
            int rowsAffected = getContentResolver().delete(selectedUserUri, null, null);

            if (rowsAffected == 0) {
                Message.message(this, getString(R.string.toast_delete_user_failed));
            } else {
                Message.message(this, getString(R.string.toast_delete_user_successful));
            }
        }

        finish();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteUser();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.create().show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, selectedUserUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if(cursor.moveToFirst()) {
            nameEditText.setText(cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME)));
            passwordEditText.setText(cursor.getString(cursor.getColumnIndex(UserContract.UserEntry.COLUMN_PASSWORD)));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        passwordEditText.setText("");
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if (!unsavedChanges) {
            super.onBackPressed();
            return;
        }

        showUnsavedChangesDialog();
    }

    private void showUnsavedChangesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog);
        builder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        builder.create().show();
    }

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            unsavedChanges = true;
            return false;
        }
    };

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (selectedUserUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_user);
            menuItem.setVisible(false);
        }
        return true;
    }
}

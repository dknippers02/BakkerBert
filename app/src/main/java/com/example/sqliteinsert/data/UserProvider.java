package com.example.sqliteinsert.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.sqliteinsert.R;

public class UserProvider extends ContentProvider {

    private UsersDbHelper usersDbHelper;

    private static final int USERS = 0;
    private static final int USER_ID = 1;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(UserContract.CONTENT_AUTHORITY, "users", USERS);
        uriMatcher.addURI(UserContract.CONTENT_AUTHORITY, "users/#", USER_ID);
    }

    @Override
    public boolean onCreate() {
        this.usersDbHelper = new UsersDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = UserProvider.uriMatcher.match(uri);
        switch(match) {
            case USERS:
                return UserContract.UserEntry.CONTENT_LIST_TYPE;
            case USER_ID:
                return UserContract.UserEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(String.format(getContext().getString(R.string.unknown_uri_with_match), uri, match));
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        switch(uriMatcher.match(uri)) {
            case USERS:
                return insertUser(uri, values);
            default:
                throw new IllegalArgumentException(String.format(getContext().getString(R.string.insertion_not_supported_for), uri));
        }
    }

    private Uri insertUser(Uri uri, ContentValues values) {
        SQLiteDatabase db = this.usersDbHelper.getWritableDatabase();

        long id;
        try {
            id = db.insertOrThrow(UserContract.UserEntry.TABLE_NAME, null, values);
        } catch (SQLiteConstraintException e) {
            throw new IllegalArgumentException(getContext().getString(R.string.username_password_not_meet_requirements));
        }

        // Notify all listeners that the data has changed for the zanimal content URI
        this.getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = usersDbHelper.getReadableDatabase();

        Cursor cursor;
        switch(uriMatcher.match(uri)) {
            case USER_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            case USERS:
                cursor = db.query(UserContract.UserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(String.format(getContext().getString(R.string.cannot_query_unknown_uri), uri));
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(this.getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch(uriMatcher.match(uri)) {
            case USER_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            case USERS:
                return updateUser(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(String.format(getContext().getString(R.string.update_is_not_supported_for), uri));
        }
    }

    private int updateUser(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = this.usersDbHelper.getWritableDatabase();

        int id;
        try {
            id = db.update(UserContract.UserEntry.TABLE_NAME, values, selection, selectionArgs);
        } catch (SQLiteConstraintException e) {
            throw new IllegalArgumentException(getContext().getString(R.string.username_password_not_meet_requirements));
        }

        this.getContext().getContentResolver().notifyChange(uri, null);

        return id;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = this.usersDbHelper.getWritableDatabase();
        int id;

        switch(uriMatcher.match(uri)) {
            case USER_ID:
                selection = UserContract.UserEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            case USERS:
                id = db.delete(UserContract.UserEntry.TABLE_NAME, selection, selectionArgs);
                this.getContext().getContentResolver().notifyChange(uri, null);
                return id;
            default:
                throw new IllegalArgumentException(String.format(getContext().getString(R.string.delete_is_not_supported_for), uri));
        }
    }
}

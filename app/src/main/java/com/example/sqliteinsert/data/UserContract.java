package com.example.sqliteinsert.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class UserContract {
    public static final String CONTENT_AUTHORITY = "com.example.sqliteinsert";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_USERS = UserEntry.TABLE_NAME;


    public static abstract class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";

        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_PASSWORD = "Password";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_USERS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + UserContract.CONTENT_AUTHORITY + "/" + UserContract.PATH_USERS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + UserContract.CONTENT_AUTHORITY + "/" + UserContract.PATH_USERS;
    }
}

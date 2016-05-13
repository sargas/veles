package net.neoturbine.veles;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VelesProvider extends ContentProvider {
    static final String AUTHORITY = "net.neoturbine.veles.provider";

    private static final UriMatcher sUriMatcher;
    private static final int URI_QSO = 0;
    private static final int URI_QSO_ID = 1;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, QSOColumns.TABLE_NAME, URI_QSO);
        sUriMatcher.addURI(AUTHORITY, QSOColumns.TABLE_NAME + "/#", URI_QSO_ID);
    }

    private SQLiteOpenHelper mDatabaseHelper;

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case URI_QSO:
                return db.delete(QSOColumns.TABLE_NAME, selection, selectionArgs);
            case URI_QSO_ID:
                long id = ContentUris.parseId(uri);
                if (selection == null) {
                    selection = QSOColumns._ID + " = ?";
                    selectionArgs = new String[]{Long.toString(id)};
                } else {
                    selection += " and " + QSOColumns._ID + " = ?";

                    if (selectionArgs == null) {
                        selectionArgs = new String[]{};
                    }
                    List<String> selectionArgsList = new ArrayList<>(Arrays.asList(selectionArgs));
                    selectionArgsList.add(Long.toString(id));
                    selectionArgs = selectionArgsList.toArray(new String[1]);
                }

                return db.delete(QSOColumns.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case URI_QSO:
                return QSOColumns.CONTENT_TYPE;
            case URI_QSO_ID:
                return QSOColumns.SINGLE_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long id;

        switch (sUriMatcher.match(uri)) {
            case URI_QSO:
                id = db.insert(QSOColumns.TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //noinspection ConstantConditions
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new VelesSQLHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case URI_QSO:
                queryBuilder.setTables(QSOColumns.TABLE_NAME);
                break;
            case URI_QSO_ID:
                queryBuilder.setTables(QSOColumns.TABLE_NAME);
                queryBuilder.appendWhere(
                        QSOColumns._ID + " = " + Long.toString(ContentUris.parseId(uri)));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        final Cursor c = queryBuilder.query(db, projection, selection, selectionArgs, null,
                null, sortOrder);

        //noinspection ConstantConditions
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case URI_QSO:
                return db.update(QSOColumns.TABLE_NAME, values, selection, selectionArgs);
            case URI_QSO_ID:
                long id = ContentUris.parseId(uri);
                if (selection == null) {
                    selection = QSOColumns._ID + " = ?";
                    selectionArgs = new String[]{Long.toString(id)};
                } else {
                    selection += " and " + QSOColumns._ID + " = ?";

                    if (selectionArgs == null) {
                        selectionArgs = new String[]{};
                    }
                    List<String> selectionArgsList = new ArrayList<>(Arrays.asList(selectionArgs));

                    selectionArgsList.add(Long.toString(id));
                    selectionArgs = selectionArgsList.toArray(new String[1]);
                }
                return db.update(QSOColumns.TABLE_NAME, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    private class VelesSQLHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "qso.db";
        private static final int DATABASE_VERSION = 2;

        public VelesSQLHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            final String CREATE_QSO_TABLE = "CREATE  TABLE \"QSO\" (" +
                    "\"_id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , " +
                    "\"start_time\" DATETIME, \"end_time\" DATETIME, " +
                    "\"other_station\" TEXT, " +
                    "\"mode\" TEXT, " +
                    "\"tx_freq\" TEXT, " +
                    "\"rx_freq\" TEXT, " +
                    "\"power\" TEXT, " +
                    "\"my_qth\" TEXT, " +
                    "\"my_locator\" TEXT, " +
                    "\"other_qth\" TEXT, " +
                    "\"other_locator\" TEXT, " +
                    "\"comment\" TEXT )";

            db.execSQL(CREATE_QSO_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Not needed yet
        }
    }
}

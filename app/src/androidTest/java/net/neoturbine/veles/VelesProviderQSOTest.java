package net.neoturbine.veles;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ProviderTestCase2;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;

@RunWith(AndroidJUnit4.class)
public class VelesProviderQSOTest extends ProviderTestCase2<VelesProvider> {
    public VelesProviderQSOTest() {
        super(VelesProvider.class, VelesProvider.AUTHORITY);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        setContext(InstrumentationRegistry.getTargetContext());
        super.setUp();
    }

    @Test
    public void testQSO__create_db() {
        getProvider().onCreate();
        assertTrue(getMockContext().getDatabasePath("qso.db").exists());
    }

    @Test
    public void testQSO__upgrade_db() {
        class TestHelper extends SQLiteOpenHelper {
            public TestHelper(Context context) {
                super(context, "qso.db", null, 1);
            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE \"QSO\" " +
                        "(\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE)");
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        }
        TestHelper oldDBHelper = new TestHelper(getMockContext());
        oldDBHelper.getWritableDatabase();
        oldDBHelper.close();

        //should do nothing for current version
        getProvider().query(QSOColumns.CONTENT_URI, null, null, null, null);

    }

    @Test
    public void testQSO__insert_valid_record() {
        Uri uri = getMockContentResolver().insert(QSOColumns.CONTENT_URI, getQSOContentValues());
        assertNotNull(uri);
        assertEquals(1L, ContentUris.parseId(uri));
        assertEquals(VelesProvider.AUTHORITY, uri.getAuthority());
    }

    @Test
    public void testQSO__insert_invalid_uri() {
        try {
            getProvider().insert(Uri.parse("http://example.com"), getQSOContentValues());
            fail("Should fail with IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testQSO__getType_multiple() {
        Uri uri = Uri.parse("content://net.neoturbine.veles.provider/QSO");
        assertEquals(getProvider().getType(uri), QSOColumns.CONTENT_TYPE);
    }

    @Test
    public void testQSO__getType__single() {
        Uri uri = Uri.parse("content://net.neoturbine.veles.provider/QSO/4");
        assertEquals(getProvider().getType(uri), QSOColumns.SINGLE_CONTENT_TYPE);
    }

    @Test
    public void testQSO__getType__unknown() {
        Uri uri = Uri.parse("http://example.com");
        try {
            getProvider().getType(uri);
            fail("IllegalArgument should have been thrown");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testQSO__query_single() {
        setupQSOs();
        String[] projection = {QSOColumns._ID, QSOColumns.MODE};
        Cursor c = getMockContentResolver().query(
                ContentUris.withAppendedId(QSOColumns.CONTENT_URI, 2L),
                projection, null, null, null);

        assertNotNull(c);
        assertEquals(2, c.getColumnCount());
        assertArrayEquals(c.getColumnNames(), projection);

        assertEquals(1, c.getCount());
        c.moveToFirst();

        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 2L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "V2 Mode");

        c.close();
    }

    @Test
    public void testQSO__query_multiple() {
        setupQSOs();
        String[] projection = {QSOColumns._ID, QSOColumns.MODE};
        Cursor c = getMockContentResolver().query(
                QSOColumns.CONTENT_URI,
                projection, "_ID > ?", new String[]{"1"}, null);

        assertNotNull(c);
        assertEquals(2, c.getColumnCount());
        assertArrayEquals(c.getColumnNames(), projection);

        assertEquals(2, c.getCount());

        c.moveToFirst();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 2L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "V2 Mode");

        c.moveToNext();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 3L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "V3 Mode");

        c.close();
    }

    @Test
    public void testQSO__query_invalid_uri() {
        try {
            getProvider().query(Uri.parse("http://example.com"), null, null, null, null);
            fail("Should fail with IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testQSO__update_multiple_uri() {
        setupQSOs();
        ContentValues newValues = new ContentValues(2);
        newValues.put(QSOColumns.START_TIME, 50);
        int number_updated = getMockContentResolver().update(
                QSOColumns.CONTENT_URI,
                newValues, null, null);
        assertEquals(3, number_updated);

        Cursor c = getMockContentResolver().query(QSOColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(c);

        while (c.moveToNext()) {
            assertEquals(50L, c.getLong(c.getColumnIndexOrThrow(QSOColumns.START_TIME)));
        }
    }

    @Test
    public void testQSO__update_single_uri_without_selection() {
        setupQSOs();
        ContentValues newValues = new ContentValues(2);
        newValues.put(QSOColumns.START_TIME, 50);
        int number_updated = getMockContentResolver().update(
                ContentUris.withAppendedId(QSOColumns.CONTENT_URI, 2),
                newValues, null, null);
        assertEquals(1, number_updated);

        Cursor c = getMockContentResolver().query(QSOColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertEquals(3, c.getCount());

        while (c.moveToNext()) {
            long expected = c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)) == 2 ? 50L : 100000L;
            assertEquals(expected, c.getLong(c.getColumnIndexOrThrow(QSOColumns.START_TIME)));
        }
    }

    @Test
    public void testQSO__update_single_uri_with_selection_when_selection_args_null() {
        setupQSOs();
        ContentValues newValues = new ContentValues(2);
        newValues.put(QSOColumns.START_TIME, 50);
        int number_updated = getMockContentResolver().update(
                ContentUris.withAppendedId(QSOColumns.CONTENT_URI, 2),
                newValues, "_ID > 0", null);
        assertEquals(1, number_updated);

        Cursor c = getMockContentResolver().query(QSOColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertEquals(3, c.getCount());

        while (c.moveToNext()) {
            long expected = c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)) == 2 ? 50L : 100000L;
            assertEquals(expected, c.getLong(c.getColumnIndexOrThrow(QSOColumns.START_TIME)));
        }
    }

    @Test
    public void testQSO__update_single_uri_with_selection_when_selection_args_not_null() {
        setupQSOs();
        ContentValues newValues = new ContentValues(2);
        newValues.put(QSOColumns.START_TIME, 50);
        int number_updated = getMockContentResolver().update(
                ContentUris.withAppendedId(QSOColumns.CONTENT_URI, 2),
                newValues, "_id > ?", new String[]{"0"});
        assertEquals(1, number_updated);

        Cursor c = getMockContentResolver().query(QSOColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertEquals(3, c.getCount());

        while (c.moveToNext()) {
            long expected = c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)) == 2 ? 50L : 100000L;
            assertEquals(expected, c.getLong(c.getColumnIndexOrThrow(QSOColumns.START_TIME)));
        }
    }

    @Test
    public void testQSO__update_invalid_uri() {
        try {
            getProvider().update(Uri.parse("http://example.com"), null, null, null);
            fail("Should fail with IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    public void testQSO__delete_multiple_uri() {
        setupQSOs();
        int number_deleted = getMockContentResolver().delete(
                QSOColumns.CONTENT_URI,
                null, null);
        assertEquals(3, number_deleted);

        Cursor c = getMockContentResolver().query(QSOColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertEquals(0, c.getCount());
    }

    @Test
    public void testQSO__delete_single_uri_without_selection() {
        setupQSOs();
        int number_deleted = getMockContentResolver().delete(
                ContentUris.withAppendedId(QSOColumns.CONTENT_URI, 2),
                null, null);
        assertEquals(1, number_deleted);

        Cursor c = getMockContentResolver().query(QSOColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertEquals(2, c.getCount());

        c.moveToFirst();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 1L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "FM");

        c.moveToNext();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 3L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "V3 Mode");
    }

    @Test
    public void testQSO__delete_single_uri_with_selection_when_selection_args_null() {
        setupQSOs();
        int number_deleted = getMockContentResolver().delete(
                ContentUris.withAppendedId(QSOColumns.CONTENT_URI, 2),
                "_ID > 1", null);
        assertEquals(1, number_deleted);

        Cursor c = getMockContentResolver().query(QSOColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertEquals(2, c.getCount());

        c.moveToFirst();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 1L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "FM");

        c.moveToNext();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 3L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "V3 Mode");
    }

    @Test
    public void testQSO__delete_single_uri_with_selection_when_selection_args_notnull() {
        setupQSOs();
        int number_deleted = getMockContentResolver().delete(
                ContentUris.withAppendedId(QSOColumns.CONTENT_URI, 2),
                QSOColumns.OTHER_STATION + " = ?", new String[]{"Nada"});
        assertEquals(0, number_deleted);

        Cursor c = getMockContentResolver().query(QSOColumns.CONTENT_URI, null, null, null, null);
        assertNotNull(c);
        assertEquals(3, c.getCount());

        c.moveToFirst();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 1L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "FM");

        c.moveToNext();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 2L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "V2 Mode");

        c.moveToNext();
        assertEquals(c.getLong(c.getColumnIndexOrThrow(QSOColumns._ID)), 3L);
        assertEquals(c.getString(c.getColumnIndexOrThrow(QSOColumns.MODE)), "V3 Mode");
    }

    @Test
    public void testQSO__delete_invalid_uri() {
        try {
            getProvider().delete(Uri.parse("http://example.com"), null, null);
            fail("Should fail with IllegalArgumentException");
        } catch (IllegalArgumentException ignored) {
        }
    }

    private void setupQSOs() {
        ContentValues v1 = getQSOContentValues();
        getMockContentResolver().insert(QSOColumns.CONTENT_URI, v1);

        ContentValues v2 = getQSOContentValues();
        v2.put(QSOColumns.MODE, "V2 Mode");
        getMockContentResolver().insert(QSOColumns.CONTENT_URI, v2);

        ContentValues v3 = getQSOContentValues();
        v3.put(QSOColumns.MODE, "V3 Mode");
        getMockContentResolver().insert(QSOColumns.CONTENT_URI, v3);
    }

    private static ContentValues getQSOContentValues() {
        ContentValues v = new ContentValues(5);
        v.put(QSOColumns.START_TIME, 100000);
        v.put(QSOColumns.END_TIME, 200000);
        v.put(QSOColumns.OTHER_STATION, "W1AW");
        v.put(QSOColumns.MODE, "FM");
        v.put(QSOColumns.COMMENT, "No comment.");
        return v;
    }
}

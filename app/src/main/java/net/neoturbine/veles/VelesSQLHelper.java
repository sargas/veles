package net.neoturbine.veles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.neoturbine.veles.qso.data.QSOColumns;

import javax.inject.Inject;
import javax.inject.Named;

public class VelesSQLHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "qso.db";
    private static final int DATABASE_VERSION = 2;

    @Inject
    VelesSQLHelper(@Named("application context") Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(QSOColumns.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not needed yet
    }
}

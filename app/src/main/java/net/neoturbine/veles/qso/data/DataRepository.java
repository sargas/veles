package net.neoturbine.veles.qso.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.QSOColumns;
import net.neoturbine.veles.VelesSQLHelper;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class DataRepository {
    private final VelesSQLHelper mDatabaseHelper;

    @Inject
    DataRepository(VelesSQLHelper dbHelper) {
        mDatabaseHelper = dbHelper;

    }

    public Observable<QSO> getQSO(long id) {
        return Observable.just(id)
                .map(this::getQSOFromDB);
    }

    private QSO getQSOFromDB(long id) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(QSOColumns.TABLE_NAME);
        queryBuilder.appendWhere(QSOColumns._ID + " = " + id);

        final SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        final Cursor c = queryBuilder.query(db, null, null, null, null,
                null, null);

        if (!c.moveToFirst())
            throw new RuntimeException("QSO of id "+id+" not found");

        QSO output = new QSO(c);
        c.close();
        db.close();
        return output;
    }

    public Completable deleteQSO(long id) {
        android.util.Log.d("DataRepository", "Deleting "+id);
        return Completable
                .fromCallable(() -> deleteQSOFromDB(id));
    }

    private Void deleteQSOFromDB(long id) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();

        db.delete(
                QSOColumns.TABLE_NAME,
                QSOColumns._ID + " = ?",
                new String[]{Long.toString(id)});
        db.close();

        return null;
    }
}

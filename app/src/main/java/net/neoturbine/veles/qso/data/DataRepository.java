package net.neoturbine.veles.qso.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.QSOColumns;
import net.neoturbine.veles.VelesSQLHelper;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class DataRepository {
    private final VelesSQLHelper mDBHelper;

    @Inject
    DataRepository(VelesSQLHelper dbHelper) {
        mDBHelper = dbHelper;

    }

    public Observable<QSO> getQSO(long id) {
        return Observable.just(id)
                .subscribeOn(Schedulers.io())
                .map(this::getQSOFromDB);
    }

    private QSO getQSOFromDB(long id) {
        final SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(QSOColumns.TABLE_NAME);
        queryBuilder.appendWhere(QSOColumns._ID + " = " + id);

        final SQLiteDatabase db = mDBHelper.getReadableDatabase();
        final Cursor c = queryBuilder.query(db, null, null, null, null,
                null, null);

        if (!c.moveToFirst())
            throw new RuntimeException("QSO of id "+id+" not found");

        QSO output = new QSO(c);
        c.close();
        db.beginTransaction();
        return output;
    }
}

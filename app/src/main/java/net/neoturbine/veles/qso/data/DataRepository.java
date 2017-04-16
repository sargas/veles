package net.neoturbine.veles.qso.data;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.QSOColumns;
import net.neoturbine.veles.VelesSQLHelper;

import javax.inject.Inject;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;

public class DataRepository {
    private BriteDatabase mDB;

    @Inject
    DataRepository(VelesSQLHelper dbHelper) {
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mDB = sqlBrite.wrapDatabaseHelper(dbHelper,
                rx.schedulers.Schedulers.io());
    }

    public Observable<QSO> getQSO(long id) {
        return RxJavaInterop.toV2Observable(mDB
                .createQuery(QSOColumns.TABLE_NAME,
                        "SELECT * from " + QSOColumns.TABLE_NAME +
                                " WHERE " + QSOColumns._ID + " = ?", String.valueOf(id))
                .mapToOne(QSO::new));
    }

    public void deleteQSO(long id) {
        mDB.delete(QSOColumns.TABLE_NAME,
                QSOColumns._ID + " = ?",
                String.valueOf(id));
    }

}
package net.neoturbine.veles.qso.data;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import net.neoturbine.veles.BuildConfig;
import net.neoturbine.veles.QSO;
import net.neoturbine.veles.VelesSQLHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava.interop.RxJavaInterop;
import io.reactivex.Observable;

import static net.neoturbine.veles.qso.data.QSODatabaseConverter.valuesFromQSO;

@Singleton
public class DatabaseDataRepository implements DataRepository {
    private final BriteDatabase mDB;

    @Inject
    DatabaseDataRepository(VelesSQLHelper dbHelper) {
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mDB = sqlBrite.wrapDatabaseHelper(dbHelper,
                rx.schedulers.Schedulers.io());

        if (BuildConfig.DEBUG)
            mDB.setLoggingEnabled(true);
    }

    @Override
    public Observable<QSO> getQSO(long id) {
        return RxJavaInterop.toV2Observable(mDB
                .createQuery(QSOColumns.TABLE_NAME,
                        "SELECT * from " + QSOColumns.TABLE_NAME +
                                " WHERE " + QSOColumns._ID + " = ?", String.valueOf(id))
                .mapToOne(QSODatabaseConverter::qsoFromCursor));
    }

    @Override
    public void deleteQSO(long id) {
        mDB.delete(QSOColumns.TABLE_NAME,
                QSOColumns._ID + " = ?",
                String.valueOf(id));
    }

    @Override
    public Observable<List<QSO>> getAllQSO() {
        return RxJavaInterop.toV2Observable(mDB
                .createQuery(QSOColumns.TABLE_NAME,
                        "SELECT * FROM " + QSOColumns.TABLE_NAME)
                .mapToList(QSODatabaseConverter::qsoFromCursor));
    }

    @Override
    public void addQSO(QSO qso) {
        mDB.insert(QSOColumns.TABLE_NAME, valuesFromQSO(qso));
    }

    @Override
    public void updateQSO(QSO qso) {
        if (qso.getID() == -1)
            throw new IllegalArgumentException("Called updateQSO without a valid QSO");

        mDB.update(QSOColumns.TABLE_NAME, valuesFromQSO(qso),
                QSOColumns._ID + " = ?", String.valueOf(qso.getID()));
    }
}

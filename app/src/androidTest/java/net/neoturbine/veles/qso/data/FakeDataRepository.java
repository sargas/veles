package net.neoturbine.veles.qso.data;

import net.neoturbine.veles.QSO;
import net.neoturbine.veles.qso.model.QSOBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

@Singleton
public class FakeDataRepository implements DataRepository {
    private static final FakeDataRepository ourInstance = new FakeDataRepository();

    private final ConcurrentHashMap<Long, QSO> mData = new ConcurrentHashMap<>(0);
    private final BehaviorSubject<List<QSO>> mSubject = BehaviorSubject.create();

    private FakeDataRepository() {
        mSubject.onNext(Collections.emptyList());
    }

    static FakeDataRepository getInstance() {
        return ourInstance;
    }

    @Override
    public Observable<QSO> getQSO(long id) {
        QSO result = mData.get(id);

        if (result != null)
            return Observable.just(result);
        else
            return Observable.empty();
    }

    @Override
    public void deleteQSO(long id) {
        if (mData.get(id) == null)
            throw new IllegalArgumentException("Deleting a QSO without an existing ID");

        mData.remove(id);
        triggerSubject();
    }

    @Override
    public Observable<List<QSO>> getAllQSO() {
        return mSubject;
    }

    void setQSOs(List<QSO> newList) {
        mData.clear();
        for (QSO qso : newList)
            addQSO(qso);
        triggerSubject();
    }

    @Override
    public void addQSO(QSO qso) {
        if (mData.get(qso.getID()) != null)
            throw new IllegalArgumentException("Adding a QSO with an existing ID");

        if (qso.getID() == -1) {
            qso = QSOBuilder.fromQSO(qso).setId(ThreadLocalRandom.current().nextLong()).createQSO();
        }
        mData.put(qso.getID(), qso);
        triggerSubject();
    }

    @Override
    public void updateQSO(QSO qso) {
        if (mData.get(qso.getID()) == null)
            throw new IllegalArgumentException("Updating a QSO without an existing ID");

        mData.put(qso.getID(), qso);
        triggerSubject();
    }

    private void triggerSubject() {
        mSubject.onNext(new ArrayList<>(mData.values()));
    }
}

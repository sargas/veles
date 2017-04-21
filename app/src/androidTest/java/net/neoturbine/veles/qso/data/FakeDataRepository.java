package net.neoturbine.veles.qso.data;

import net.neoturbine.veles.QSO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

@Singleton
public class FakeDataRepository implements DataRepository {
    private static List<QSO> mList = new ArrayList<>(0);
    private static final PublishSubject<List<QSO>> mSubject = PublishSubject.create();

    @Inject
    public FakeDataRepository() {
    }

    @Override
    public Observable<QSO> getQSO(long id) {
        for (QSO qso : mList) {
            if (qso.getID() == id)
                return Observable.just(qso);
        }
        return Observable.empty();
    }

    @Override
    public void deleteQSO(long id) {}

    @Override
    public Observable<List<QSO>> getAllQSO() {
        return mSubject;
    }

    public void setQSOs(List<QSO> newList) {
        mList = newList;
        mSubject.onNext(newList);
    }
}

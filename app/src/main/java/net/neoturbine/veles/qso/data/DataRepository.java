package net.neoturbine.veles.qso.data;

import net.neoturbine.veles.QSO;

import java.util.List;

import io.reactivex.Observable;

public interface DataRepository {
    Observable<QSO> getQSO(long id);

    void deleteQSO(long id);

    Observable<List<QSO>> getAllQSO();
}

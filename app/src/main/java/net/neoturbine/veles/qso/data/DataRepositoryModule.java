package net.neoturbine.veles.qso.data;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import net.neoturbine.veles.VelesSQLHelper;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module(includes = {ApplicationContextModule.class})
public class DataRepositoryModule {
    @Provides static DataRepository provideDataRepository(DatabaseDataRepository repository) {
        return repository;
    }

    @Provides
    SqlBrite provideSqlBrite() {
        return new SqlBrite
                .Builder()
                .logger((s) -> Timber.tag("Database").d(s))
                .build();
    }

    @Provides
    BriteDatabase provideBriteDatabase(SqlBrite sqlBrite, VelesSQLHelper dbHelper) {
        BriteDatabase database = sqlBrite.wrapDatabaseHelper(dbHelper,
                rx.schedulers.Schedulers.io());
        database.setLoggingEnabled(true);

        return database;
    }
}

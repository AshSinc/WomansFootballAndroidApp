package uk.ash.womensfootball.fixture;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import uk.ash.womensfootball.Converters;

@Database(entities = {FixtureData.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class FixtureDatabase extends RoomDatabase {

    public abstract FixtureDao fixtureDao();

    private static FixtureDatabase INSTANCE;

    public static FixtureDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (FixtureDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), FixtureDatabase.class, "fixture_database")
                            .fallbackToDestructiveMigration()
                            //remove in final
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

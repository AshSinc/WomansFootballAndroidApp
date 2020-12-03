package uk.ash.womensfootball.fixture;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import uk.ash.womensfootball.Converters;
import uk.ash.womensfootball.league.LeagueDao;
import uk.ash.womensfootball.league.LeagueData;
import uk.ash.womensfootball.league.LeagueDatabase;

@Database(entities = {LeagueData.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class FixtureDatabase extends RoomDatabase {
    public abstract FixtureDao fixtureDao();
    private static uk.ash.womensfootball.fixture.FixtureDatabase INSTANCE;

    public static uk.ash.womensfootball.fixture.FixtureDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (uk.ash.womensfootball.fixture.FixtureDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), uk.ash.womensfootball.fixture.FixtureDatabase.class, "fixture_database")
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

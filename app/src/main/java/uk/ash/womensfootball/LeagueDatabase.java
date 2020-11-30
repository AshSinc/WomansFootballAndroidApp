package uk.ash.womensfootball;
import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities={LeagueData.class}, version=1)
@TypeConverters({Converters.class})
public abstract class LeagueDatabase extends RoomDatabase {

    public abstract LeagueDao leagueDao();

    private static LeagueDatabase INSTANCE;

    public static LeagueDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (LeagueDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LeagueDatabase.class,"league_database")
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
package uk.ash.womensfootball.event;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import uk.ash.womensfootball.Converters;

@Database(entities = {EventData.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class EventDatabase extends RoomDatabase {

    public abstract EventDao eventDao();

    private static EventDatabase INSTANCE;

    public static EventDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (EventDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), EventDatabase.class, "event_database")
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



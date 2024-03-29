package uk.ash.womensfootball.event;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<EventData> fixtureData);

    @Query("SELECT * from event_data WHERE fixtureId LIKE :fixtureId")
    public List<EventData> findByFixtureId(int fixtureId);

    //clear table
    @Query("DELETE FROM event_data")
    void clearTable();
}

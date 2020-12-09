package uk.ash.womensfootball.fixture;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FixtureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<FixtureData> fixtureData);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void update(FixtureData fixtureData);

    @Query("SELECT * from fixture_data WHERE leagueId LIKE :leagueId")
    public List<FixtureData> findByLeagueId(String leagueId);

    @Query("SELECT * from fixture_data WHERE fixtureId LIKE :fixtureId")
    public FixtureData findFixtureById(int fixtureId);

    //clear table
    @Query("DELETE FROM fixture_data")
    void clearTable();
}
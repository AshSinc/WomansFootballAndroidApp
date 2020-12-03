package uk.ash.womensfootball.fixture;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FixtureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<FixtureData> fixtureData);

    @Query("SELECT * from fixture_data")
    public List<FixtureData> getAll();

    @Query("SELECT * from fixture_data WHERE leagueId LIKE :leagueId")
    public List<FixtureData> findByLeagueId(String leagueId);

    @Delete
    public void delete(FixtureData fixtureData);
}
package uk.ash.womensfootball.league;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface LeagueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<LeagueData> leagueData);

    @Query("SELECT * from league_data WHERE leagueId LIKE :leagueId")
    public List<LeagueData> findByLeagueId(String leagueId);

    @Query("DELETE FROM league_data")
    void clearTable();
}

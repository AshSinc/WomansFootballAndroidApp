package uk.ash.womensfootball;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface LeagueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insert(List<LeagueData> leagueData);

    @Query("SELECT * from league_data")
    public List<LeagueData> getAll();

    @Query("SELECT * from league_data WHERE leagueId LIKE :leagueId")
    public List<LeagueData> findByLeagueId(String leagueId);


    //@Query("SELECT * from league_data WHERE locationName LIKE :locName AND date LIKE :date")
    //public LocationForecast findByNameAndDate(String locName, String date);

    @Delete
    public void delete(LeagueData leagueData);
}

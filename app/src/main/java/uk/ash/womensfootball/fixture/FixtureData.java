package uk.ash.womensfootball.fixture;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

//basic class to hold and retrieve fixture data
@Entity(tableName = "fixture_data")
public class FixtureData {

    @NonNull
    @PrimaryKey
    private int fixtureId; //fixture id from Fixtures API
    private int leagueId; //league id from league API
    private String teamNameH;
    private String teamNameA;
    private int homeScore;
    private int awayScore;
    private LocalDateTime dateTime;
    private int teamIdH; //teamID from Api
    private int teamIdA; //teamID from Api
    private boolean gameComplete;
    private long nextEventUpdate = -1;

    public FixtureData(int fixtureId, int leagueId, String teamNameH, String teamNameA, int homeScore, int awayScore, LocalDateTime dateTime, int teamIdH, int teamIdA, boolean gameComplete){
        this.fixtureId = fixtureId;
        this.leagueId = leagueId;
        this.teamNameH = teamNameH;
        this.teamNameA = teamNameA;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.dateTime = dateTime;
        this.teamIdH = teamIdH;
        this.teamIdA = teamIdA;
        this.gameComplete = gameComplete;
    }

    //getters
    public String getTeamNameH(){
        return teamNameH;
    }
    public String getTeamNameA(){
        return teamNameA;
    }
    public int getHomeScore(){
        return homeScore;
    }
    public int getAwayScore(){
        return awayScore;
    }
    public LocalDateTime getDateTime(){
        return dateTime;
    }
    public int getFixtureId(){
        return fixtureId;
    }
    public int getLeagueId(){
        return leagueId;
    }
    public int getTeamIdH(){
        return teamIdH;
    }
    public int getTeamIdA(){
        return teamIdA;
    }
    public boolean getGameComplete(){
        return gameComplete;
    }
    public long getNextEventUpdate(){
        return nextEventUpdate;
    }
    public void setNextEventUpdate(long l){
        nextEventUpdate = l;
    }
}

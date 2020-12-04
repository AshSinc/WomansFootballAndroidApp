package uk.ash.womensfootball.fixture;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.Date;

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

    public FixtureData(int fixtureId, int leagueId, String teamNameH, String teamNameA, int homeScore, int awayScore, LocalDateTime dateTime, int teamIdH, int teamIdA){
        this.fixtureId = fixtureId;
        this.leagueId = leagueId;
        this.teamNameH = teamNameH;
        this.teamNameA = teamNameA;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.dateTime = dateTime;
        this.teamIdH = teamIdH;
        this.teamIdA = teamIdA;
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

    //setters - probably don't need
    public void setTeamNameH(String n){
        teamNameH = n;
    }
    public void setTeamNameA(String n){
        teamNameA = n;
    }
    public void setHomeScore(int s){
        homeScore = s;
    }
    public void setAwayScore(int s){
        awayScore = s;
    }
    public void setDateTime(LocalDateTime d){
        dateTime = d;
    }
    public void setFixtureId(int i){
        fixtureId = i;
    }

}

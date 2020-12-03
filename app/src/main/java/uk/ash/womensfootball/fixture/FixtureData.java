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
    private Drawable badgeH; //home team logo
    private Drawable badgeA; //away team logo
    private int homeScore;
    private int awayScore;
    private LocalDateTime dateTime;

    public FixtureData(String nH, String nA, Drawable iH, Drawable iA, int hS, int aS, LocalDateTime dT, int id){
        teamNameH = nH;
        teamNameA = nA;
        badgeH =iH;
        badgeA = iA;
        homeScore = hS;
        awayScore = aS;
        dateTime = dT;
        fixtureId = id;
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
    public Drawable getBadgeH() {
        return badgeH;
    }
    public Drawable getBadgeA() {
        return badgeA;
    }
    public int getFixtureId(){
        return fixtureId;
    }

    //setters - probably don't need
    public void setTeamNameH(String n){
        teamNameH = n;
    }
    public void setTeamNameA(String n){
        teamNameA = n;
    }
    public void setBadgeH(Drawable p) {
        badgeH = p;
    }
    public void setBadgeA(Drawable p) {
        badgeA = p;
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

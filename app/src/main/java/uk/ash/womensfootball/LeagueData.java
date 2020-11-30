package uk.ash.womensfootball;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//basic class to hold and retrieve league data
@Entity(tableName = "league_data")
public class LeagueData {

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private String uid;

    private String leagueId; //league name
    private String teamName; //team name
    //private Drawable badge; //team badge
    //private String badge; //team badge
    private int played; //stats
    private int wins;
    private int draws;
    private int losses;
    private int goalDiff;
    private int points;
    private int teamId; //teamID from Api


    public LeagueData(String teamName, int played, int wins, int draws, int losses, int goalDiff, int points, int teamId) {
        this.leagueId = "2745"; //temp hardcoded, should be passed in TODO
        this.teamName = teamName;
        this.uid = leagueId+teamName;
       // badge = b;
        this.played = played;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.goalDiff = goalDiff;
        this.points = points;
        this.teamId = teamId;
    }

    //getters
    public String getTeamName() {
        return teamName;
    }
    public int getPlayed() {
        return played;
    }
    public int getWins() {
        return wins;
    }
    public int getDraws() {
        return draws;
    }
    public int getLosses() {
        return losses;
    }
    public int getGoalDiff() {
        return goalDiff;
    }
    public int getPoints() {
        return points;
    }
    //public String getBadge() {
    //    return badge;
    //}
    public int getTeamId(){
        return teamId;
    }
    public String getUid(){
        return uid;
    }
    public String getLeagueId(){
        return leagueId;
    }

    //setters - maybe not needed
    public void setTeamName(String n) {
        teamName = n;
    }
    public void setPlayed(int p) {
        played = p;
    }
    public void setWins(int w) {
        wins = w;
    }
    public void setDraws(int d) {
        draws = d;
    }
    public void setLosses(int l) {
        losses = l;
    }
    public void setGoalDiff(int gd) {
        goalDiff = gd;
    }
    public void setPoints(int pts) {
        points = pts;
    }
    public void setTeamId(int i){
        teamId = i;
    }
    public void setUid(String i){
        uid = i;
    }
    public void setLeagueId(String s){
        leagueId = s;
    }
}

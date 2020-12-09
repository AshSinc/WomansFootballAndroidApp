package uk.ash.womensfootball.league;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//basic class to hold and retrieve league data
@Entity(tableName = "league_data")
public class LeagueData {

    @NonNull
    @PrimaryKey
    private String uid;

    private String leagueId; //league name
    private String teamName; //team name
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
        this.uid = leagueId + teamName;
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

    public int getTeamId() {
        return teamId;
    }

    public String getUid() {
        return uid;
    }

    public String getLeagueId() {
        return leagueId;
    }

    public void setUid(String i) {
        uid = i;
    }

    public void setLeagueId(String s) {
        leagueId = s;
    }
}


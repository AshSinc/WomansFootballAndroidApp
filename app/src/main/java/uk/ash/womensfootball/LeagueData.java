package uk.ash.womensfootball;

import android.graphics.drawable.Drawable;

//basic class to hold and retrieve league data
public class LeagueData {
    private String teamName; //team name
    private Drawable badge; //team badge
    private int played; //stats
    private int wins;
    private int draws;
    private int losses;
    private int goalDiff;
    private int points;

    public LeagueData(String n, Drawable b, int p, int w, int d, int l, int gd, int pts) {
        teamName = n;
        badge = b;
        played = p;
        wins = w;
        draws = d;
        losses = l;
        goalDiff = gd;
        points = pts;
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
    public Drawable getBadge() {
        return badge;
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
}

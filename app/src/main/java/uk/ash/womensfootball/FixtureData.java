package uk.ash.womensfootball;

import java.time.LocalDateTime;
import java.util.Date;

//basic class to hold and retrieve fixture data
public class FixtureData {
    private String teamNameH;
    private String teamNameA;
    private String imagePathH;
    private String imagePathA;
    private int homeScore;
    private int awayScore;
    private LocalDateTime dateTime;

    public FixtureData(String nH, String nA, String iH, String iA, int hS, int aS, LocalDateTime dT){
        teamNameH = nH;
        teamNameA = nA;
        imagePathH =iH;
        imagePathA = iA;
        homeScore = hS;
        awayScore = aS;
        dateTime = dT;
    }

    public void setTeamNameH(String n){
        teamNameH = n;
    }
    public String getTeamNameH(){
        return teamNameH;
    }
    public void setTeamNameA(String n){
        teamNameA = n;
    }
    public String getTeamNameA(){
        return teamNameA;
    }
    public void setImagePathH(String p){
        imagePathH = p;
    }
    public String getImagePathH(){
        return imagePathH;
    }
    public void setImagePathA(String p){
        imagePathA = p;
    }
    public String getImagePathA(){
        return imagePathA;
    }
    public void setHomeScore(int s){
        homeScore = s;
    }
    public int getHomeScore(){
        return homeScore;
    }
    public void setAwayScore(int s){
        awayScore = s;
    }
    public int getAwayScore(){
        return awayScore;
    }
    public void setDateTime(LocalDateTime d){
        dateTime = d;
    }
    public LocalDateTime getDateTime(){
        return dateTime;
    }
}

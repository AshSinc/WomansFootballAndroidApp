package uk.ash.womensfootball;

//basic class to hold and retrieve league data
public class LeagueData {
    private String teamName;
    private String imagePath;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalDiff;
    private int points;

    public LeagueData(String n, String path, int p, int w, int d, int l, int gd, int pts) {
        teamName = n;
        imagePath = path;
        played = p;
        wins = w;
        draws = d;
        losses = l;
        goalDiff = gd;
        points = pts;
    }

    public void setTeamName(String n) {
        teamName = n;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setImagePath(String p) {
        imagePath = p;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setPlayed(int p) {
        played = p;
    }

    public int getPlayed() {
        return played;
    }

    public void setWins(int w) {
        wins = w;
    }

    public int getWins() {
        return wins;
    }

    public void setDraws(int d) {
        draws = d;
    }

    public int getDraws() {
        return draws;
    }

    public void setLosses(int l) {
        losses = l;
    }

    public int getLosses() {
        return losses;
    }

    public void setGoalDiff(int gd) {
        goalDiff = gd;
    }

    public int getGoalDiff() {
        return goalDiff;
    }

    public void setPoints(int pts) {
        points = pts;
    }

    public int getPoints() {
        return points;
    }
}

package uk.ash.womensfootball.event;

import android.graphics.drawable.Drawable;

import java.time.LocalDateTime;
import java.util.List;

//basic class to hold and retrieve fixture data
public class EventData {
    private boolean inProgress; //stores if game is in progress or complete...... not needed maybe
    private String teamNameH;
    private String teamNameA;
    private Drawable badgeH; //home team logo
    private Drawable badgeA; //away team logo
    private int homeScore;
    private int awayScore;
    private LocalDateTime dateTime;
    //holds the timeline of events
    private List<EventsItem> eventsList;

    public EventData(String nH, String nA, Drawable dH, Drawable dA, int hS, int aS, LocalDateTime dT, List<EventsItem> eL) {
        teamNameH = nH;
        teamNameA = nA;
        badgeH = dH;
        badgeA = dA;
        homeScore = hS;
        awayScore = aS;
        dateTime = dT;
        eventsList = eL;
    }

    //an eventItem tracks each individual event
    public static class EventsItem {
        public boolean away; //tracks if home or away
        public int time; //stores time of event in minutes
        public int eventID; //eg. goal = 1, yellow = 2, will be used to fetch icon paths
        public String description; //the text description of event
        public Drawable eventDrawable; //the Drawable to be displayed for this event

        public EventsItem(boolean a, int t, int id, String desc, Drawable d) {
            away = a;
            time = t;
            eventID = id;
            description = desc;
            eventDrawable = d;
        }
    }

    //getters
    public String getTeamNameH() {
        return teamNameH;
    }
    public String getTeamNameA() {
        return teamNameA;
    }
    public Drawable getBadgeH() {
        return badgeH;
    }
    public Drawable getBadgeA() {
        return badgeA;
    }
    public int getHomeScore() {
        return homeScore;
    }
    public int getAwayScore() {
        return awayScore;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public List<EventsItem> getEventsList() {
        return eventsList;
    }

    //setters - possibly not needed, except maybe add/set eventList
    public void setTeamNameA(String n) {
        teamNameA = n;
    }
    public void setTeamNameH(String n) {
        teamNameH = n;
    }
    public void setBadgeH(Drawable p) {
        badgeH = p;
    }
    public void setBadgeA(Drawable p) {
        badgeA = p;
    }
    public void setHomeScore(int s) {
        homeScore = s;
    }
    public void setAwayScore(int s) {
        awayScore = s;
    }
    public void setDateTime(LocalDateTime d) {
        dateTime = d;
    }
    public void setEventsList(List<EventsItem> el) {
        eventsList = el;
    }
    public void addEventItem(EventsItem i) {
        eventsList.add(i);
    }
}

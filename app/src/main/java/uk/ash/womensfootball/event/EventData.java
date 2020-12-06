package uk.ash.womensfootball.event;

import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;
import java.util.List;

//basic class to hold and retrieve fixture data
@Entity(tableName = "event_data")
public class EventData {

    @NonNull
    @PrimaryKey(autoGenerate = true) //TODO check this isn't generating duplicate entries in DB
    public int id;

    public String eventType; //

    private int fixtureId; //fixture id from Fixtures API

    public boolean away; //tracks if home or away
    public int time; //stores time of event in minutes

    public String description; //the text description of event

    public static long updateTime;

    //public EventData(int fixtureId, String teamNameH, String teamNameA, int homeScore, int awayScore, LocalDateTime dateTime, List<EventsItem> eventsList, boolean gameComplete) {
    public EventData(String eventType, int fixtureId, boolean away, int time, String description) {
        this.eventType = eventType; //eg. goal = 1, yellow = 2, will be used to fetch icon paths
        this.fixtureId = fixtureId;
        this.away = away; //tracks if home or away
        this.time = time; //stores time of event in minutes
        this.description = description; //the text description of event
    }

    public String getEventType(){
        return eventType;
    }

    public int getFixtureId(){
        return fixtureId;
    }

    public boolean getAway(){
        return away;
    }

    public int getTime(){
        return time;
    }

    public String getDescription(){
        return description;
    }

    public long getLongUpdateTime(){
        return updateTime;
    }

    public void setLongUpdateTime(long l){
        updateTime = l;
    }

}

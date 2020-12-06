package uk.ash.womensfootball.event;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.Converters;
import uk.ash.womensfootball.JsonToDataTask;
import uk.ash.womensfootball.R;
import uk.ash.womensfootball.fixture.FixtureDao;
import uk.ash.womensfootball.fixture.FixtureData;
import uk.ash.womensfootball.fixture.FixtureDatabase;
import uk.ash.womensfootball.fixture.FixturesActivity;
import uk.ash.womensfootball.league.LeagueActivity;
import uk.ash.womensfootball.league.LeagueDao;
import uk.ash.womensfootball.league.LeagueDatabase;
import uk.ash.womensfootball.league.LeagueRecyclerViewAdapter;


public class EventsActivity extends ActivityBase {
    private List<EventData> eventData;
    private FixtureData fixture;
    private EventDao eventDao;
    private FixtureDao fixtureDao;
    private int fixtureID, homeTeamID, awayTeamID, homeTeamScore, awayTeamScore;
    private String homeTeamName, awayTeamName;
    private boolean gameComplete;
    private LocalDateTime kickoffTime, updateTime;
    private int MAX_AGE = 60000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if savedInstanceState is null we need to make a new bundle and pass it with ACTIVITY key set to tell the base class which activity we are
        Intent intent = getIntent();
        if (savedInstanceState == null) {
            Log.d(TAG, "onCreate , no savedInstanceState: ");
            Bundle newInstanceState = new Bundle();
            newInstanceState.putInt("ACTIVITY", 2);
            super.onCreate(newInstanceState);
        } else {
            Log.d(TAG, "onCreate , found savedInstanceState: ");
            savedInstanceState.putInt("ACTIVITY", 2);
            super.onCreate(savedInstanceState);
        }

        //if (intent == null)
        //    return; //TODO should give a fail message but for now this will do

        /*fixtureID = intent.getIntExtra("FIXTURE_ID", -1);
        homeTeamID = intent.getIntExtra("HOME_TEAM_ID", -1);
        awayTeamID = intent.getIntExtra("AWAY_TEAM_ID", -1);
        homeTeamScore = intent.getIntExtra("HOME_SCORE", -1);
        awayTeamScore = intent.getIntExtra("AWAY_SCORE", -1);
        homeTeamName = intent.getStringExtra("HOME_TEAM_NAME");
        awayTeamName = intent.getStringExtra("AWAY_TEAM_NAME");
        gameComplete = intent.getBooleanExtra("IS_COMPLETE", false);
        kickoffTime = Converters.ldtFromLong(intent.getLongExtra("LONG_TIME", -1));*/

        synchronized (EventsActivity.class) {
            EventDatabase eventsDb = EventDatabase.getDatabase(this);
            eventDao = eventsDb.eventDao();
        }
        synchronized (FixturesActivity.class) {
            FixtureDatabase db = FixtureDatabase.getDatabase(this);
            fixtureDao = db.fixtureDao();
        }

        fixture = fixtureDao.findFixtureById(fixtureID);

        fixtureID = fixture.getFixtureId();
        homeTeamID = fixture.getTeamIdH();
        awayTeamID = fixture.getTeamIdA();
        homeTeamScore = fixture.getHomeScore();
        awayTeamScore = fixture.getAwayScore();
        homeTeamName = fixture.getTeamNameH();
        awayTeamName = fixture.getTeamNameA();
        gameComplete = fixture.getGameComplete();
        kickoffTime = fixture.getDateTime();

        //set the header info from savedInstanceState data
        ((TextView) findViewById(R.id.tv_time)).setText(kickoffTime.format(TIME_PATTERN));
        ((TextView) findViewById(R.id.tv_TeamNameH)).setText(homeTeamName);
        ((TextView) findViewById(R.id.tv_TeamNameA)).setText(awayTeamName);
        ((TextView) findViewById(R.id.tv_homeScore)).setText(Integer.toString(homeTeamScore));
        ((TextView) findViewById(R.id.tv_awayScore)).setText(Integer.toString(awayTeamScore));
        ((ImageView) findViewById(R.id.iv_BadgeFixtureH)).setForeground(ActivityBase.getBadgeForTeam(this, homeTeamID));
        ((ImageView) findViewById(R.id.iv_BadgeFixtureA)).setForeground(ActivityBase.getBadgeForTeam(this, awayTeamID));

        boolean shouldRefreshData = false;
        eventData = eventDao.findByFixtureId(fixtureID);
        if (eventData.isEmpty()) {
            Log.d(TAG, "onCreate: eventData is empty, should refresh");
            shouldRefreshData = true;
        } else {
            Long now = System.currentTimeMillis() / 1000; //system time in seconds
            if (gameComplete) {
                Log.d(TAG, "onCreate: game is over, dont refresh");
                shouldRefreshData = false;
            } else if (now.compareTo(fixture.getNextEventUpdate()) > 0) {
                Log.d(TAG, "onCreate: refresh threshold of 10 minutes has passed");
                shouldRefreshData = true;
                //TODO get last refresh time, need to save this in event, fixture would be better but much more awkward
            }
            //else if(now - Converters.longFromLdt(eventData.get(0).getUpdateTime()) > MAX_AGE){
            //    Log.d(TAG, "onCreate: game is over, dont refresh");
            //    //TODO get last refresh time, need to save this in event, fixture would be better but much more awkward
            //}
        }

        shouldRefreshData = false; //TODO remove

        if (shouldRefreshData) {
            requestEventsUpdate();
        } else {
            RecyclerView recyclerView = findViewById(R.id.rv_EventTable);
            RecyclerView.Adapter adapter = new EventRecyclerViewAdapter(getApplicationContext(), eventData);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

    public void requestEventsUpdate() {
        //send Volley request to url
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getEventURL(String.valueOf(fixtureID)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //call getLeagueFromJSON and parse the response to a new event list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        eventData = jsonToData.getEventFromJSON(getApplicationContext(), response, fixtureID, homeTeamID);

                        if (eventData == null || eventData.size() <= 0) {
                            Log.d("DEBUGDB", "requestEventsUpdate() : eventData is null or empty, returning.");
                            return;
                        }

                        eventDao.insert(eventData);

                        //update the fixture timing in fixture DB
                        long nextEventRefresh = System.currentTimeMillis() + 600000; //set next refresh to 10 mins later
                        fixture.setNextEventUpdate(nextEventRefresh);
                        fixtureDao.update(fixture); //TODO check this is actually working

                        Log.d("DEBUGDB", "Updated DB checking: " + eventDao.findByFixtureId(fixtureID).get(0).toString());

                        //construct and add recyclerView data, need to move this out of here will probably slow app?
                        RecyclerView recyclerView = findViewById(R.id.rv_EventTable);
                        RecyclerView.Adapter adapter = new EventRecyclerViewAdapter(getApplicationContext(), eventData);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        ) {
            //have to override getHeaders() method to pass the api key with StringRequest
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("x-rapidapi-key", "e5279c018911db9fe82d1a151043cb31");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public Drawable getDrawableForEvent(int id) {
        Drawable d;
        switch (id) {
            case (0):
                d = ContextCompat.getDrawable(this, R.drawable.goal);
                break;
            case (1):
                d = ContextCompat.getDrawable(this, R.drawable.yellowcard);
                break;
            case (2):
                d = ContextCompat.getDrawable(this, R.drawable.redcard);
                break;
            case (3):
                d = null;
                break;
            case (4):
                d = null;
                break;
            case (5):
                d = null;
                break;
            default:
                d = ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_on);
        }
        return d;
    }

    public String getEventURL(String id) {
        return "https://v2.api-football.com/events/" + id;
    }

}
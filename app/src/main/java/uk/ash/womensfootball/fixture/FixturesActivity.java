package uk.ash.womensfootball.fixture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.Converters;
import uk.ash.womensfootball.JsonToDataTask;
import uk.ash.womensfootball.R;
import uk.ash.womensfootball.event.EventsActivity;
import uk.ash.womensfootball.league.LeagueRecyclerViewAdapter;


public class FixturesActivity extends ActivityBase {
    private List<FixtureData> fixturesData;
    private List<FixtureData> lastFixtures;
    private List<FixtureData> nextFixtures;
    private String selectedLeague = "2745";
    private FixtureDao fixtureDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if savedInstanceState is null we need to make a new bundle and pass it with ACTIVITY key set to tell the base class which activity we are
        if (savedInstanceState == null) {
            Log.d("DEBUG", "savedInstanceState is null");
            Bundle newInstanceState = new Bundle();
            newInstanceState.putInt("ACTIVITY", 1);
            super.onCreate(newInstanceState);
        } else {
            Log.d("DEBUG", "savedInstanceState is not null");
            savedInstanceState.putInt("ACTIVITY", 1);
            super.onCreate(savedInstanceState);
        }

        synchronized (FixturesActivity.class) {
            FixtureDatabase db = FixtureDatabase.getDatabase(this);
            fixtureDao = db.fixtureDao();
        }

        fixturesData = new ArrayList<>();

        selectedLeague = getSharedPreferencesSelectedLeague();

        // TODO
        //currently selected league, not implemented

        boolean shouldRefreshData = false;
        //check time of last refresh
        Long now = System.currentTimeMillis()/1000; //system time in seconds
        Log.d("DEBUGDB", now + " now compareTo() + " + getSharedPreferencesLastUpdate("FIXTURE", selectedLeague));
        Log.d("DEBUGDB", "" + now.compareTo(getSharedPreferencesLastUpdate("FIXTURE", selectedLeague)));

        //if(true)
        //    return;
        if (now.compareTo(getSharedPreferencesLastUpdate("FIXTURE", selectedLeague)) > 0) {
        //if (now - getSharedPreferencesLastUpdate("FIXTURE", selectedLeague) > MIN_AGE) {
            Log.d("DEBUGDB", "DB is old should refresh");
            shouldRefreshData = true;
        } else {
            fixturesData = fixtureDao.findByLeagueId(selectedLeague);
            if (fixturesData.size() > 0) {
                Log.d("DEBUGDB", "Found " + fixturesData.get(0));
                shouldRefreshData = false;
            } else
                shouldRefreshData = true;
        }
        shouldRefreshData = false; //TODO remove
        if (shouldRefreshData) {
            requestFixtureUpdate();
        } else {
            fixturesData.sort(new SortByTime());
            RecyclerView recyclerView = findViewById(R.id.rv_FixtureTable);
            RecyclerView.Adapter adapter = new FixtureRecyclerViewAdapter(getApplicationContext(), fixturesData, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

    public void requestFixtureUpdate() {
        lastFixtures = null;
        nextFixtures = null;
        fixturesData = new ArrayList<>();
        //send Volley request to url
        RequestQueue queue = Volley.newRequestQueue(this);
        FixturesActivity fa = this; //passing through to recycler view adapter, so we can navigate from Fixture to Event view
        StringRequest stringRequestLast = new StringRequest(Request.Method.GET, getLastFixturesURL(selectedLeague),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "onResponse: " + response);
                        //call getLeagueFromJSON and parse the response to a new league list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        lastFixtures = jsonToData.getFixtureFromJSON(getApplicationContext(), response);
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
        StringRequest stringRequestNext = new StringRequest(Request.Method.GET, getNextFixturesURL(selectedLeague),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d(TAG, "onResponse: " + response);

                        //call getLeagueFromJSON and parse the response to a new league list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        nextFixtures = jsonToData.getFixtureFromJSON(getApplicationContext(), response);
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
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if(nextFixtures != null && lastFixtures != null){
                    Log.d("DEBUGDB", "onRequestFinished: both not null");

                    lastFixtures.sort(new SortByTime());
                    nextFixtures.sort(new SortByTime());
                    // set next fixture refresh for 100 minutes after the start of the next game
                    //not ideal but workaround for API restrictions for now
                    long nextFixtureRefresh = System.currentTimeMillis()+86400000; //set default to a day later, would only be used at end of season if there are no upcoming games
                    if(!nextFixtures.isEmpty())
                        nextFixtureRefresh = Converters.longFromLdt(nextFixtures.get(0).getDateTime().plusMinutes(100));

                    Log.d("DEBUGDB", "Next Expected end of game: " + nextFixtureRefresh);

                    fixturesData.addAll(lastFixtures);
                    fixturesData.addAll(nextFixtures);

                    fixtureDao.clearTable();
                    fixtureDao.insert(fixturesData);

                    writeSharedPreferencesDBRefresh(nextFixtureRefresh, "FIXTURE", getSharedPreferencesSelectedLeague());

                    //construct and add recyclerView data
                    RecyclerView recyclerView = findViewById(R.id.rv_FixtureTable);
                    RecyclerView.Adapter adapter = new FixtureRecyclerViewAdapter(getApplicationContext(), fixturesData, fa);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

                    //set to null again to be safe
                    lastFixtures=null;
                    nextFixtures=null;
                }
            }
        });

        queue.add(stringRequestLast);
        queue.add(stringRequestNext);
    }

    public String getLastFixturesURL(String id) {
        //TODO get timezone from phone
        return "https://v2.api-football.com/fixtures/league/" + id + "/last/20?timezone=Europe/London";
    }

    public String getNextFixturesURL(String id) {
        //TODO get timezone from phone
        return "https://v2.api-football.com/fixtures/league/" + id + "/next/20?timezone=Europe/London";
    }

    //public void switchToEvents(int fixtureId) {
    public void switchToEvents() {
        Intent intent = new Intent(this, EventsActivity.class); //Intent, the activity we want to switch to
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); //stops animation on switching activity, stops annoying flicker
        /*intent.putExtra("FIXTURE_ID", fixtureId);
        FixtureData fixture = fixtureDao.findFixtureById(fixtureId); //get Fixture data from database by fixture id
        intent.putExtra("HOME_TEAM_NAME", fixture.getTeamNameH());
        intent.putExtra("AWAY_TEAM_NAME", fixture.getTeamNameA());
        intent.putExtra("HOME_TEAM_ID", fixture.getTeamIdH());
        intent.putExtra("AWAY_TEAM_ID", fixture.getTeamIdA());
        intent.putExtra("HOME_SCORE", fixture.getHomeScore());
        intent.putExtra("AWAY_SCORE", fixture.getAwayScore());
        intent.putExtra("IS_COMPLETE", fixture.getGameComplete());
        intent.putExtra("LONG_TIME", Converters.longFromLdt(fixture.getDateTime()));*/
        //intent.putExtra("UPDATE_TIME", Converters.longFromLdt(fixture.getDateTime()));
        startActivity(intent); //start activity via context that called it
    }
    public class SortByTime implements Comparator<FixtureData>
    {
        @Override
        public int compare(FixtureData o1, FixtureData o2) {
            return o1.getDateTime().compareTo(o2.getDateTime());
        }
    }
}
package uk.ash.womensfootball.fixture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.Converters;
import uk.ash.womensfootball.JsonToDataTask;
import uk.ash.womensfootball.R;
import uk.ash.womensfootball.event.EventsActivity;


public class FixturesActivity extends ActivityBase {
    private List<FixtureData> fixturesData;
    private List<FixtureData> lastFixtures;
    private List<FixtureData> nextFixtures;

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

        fixturesData = new ArrayList<>();

        //checks if can update based on shared prefs timings activity and leagueId
        if (now.compareTo(getSharedPreferencesLastUpdate("FIXTURE", selectedLeague)) > 0) {
            Log.d("DEBUGDB", "DB is old should refresh");
            shouldRefreshData = true;
        } else {
            fixturesData = fixtureDao.findByLeagueId(selectedLeague);
            if (fixturesData.size() > 0) {
                //Log.d("DEBUGDB", "Found " + fixturesData.get(0));
                shouldRefreshData = false;
            } else
                shouldRefreshData = true;
        }

        if(NEVER_UPDATE) //debug variable
            shouldRefreshData = false;

        //begin refresh call or pass fixtureData made of database entries
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
        if(checkAPILimit()) //always check we are not over limit
            return;
        showToast("Updating Fixtures");
        //reset to null to check completion of calls
        lastFixtures = null;
        nextFixtures = null;
        fixturesData = new ArrayList<>();
        //send Volley request to url
        RequestQueue queue = Volley.newRequestQueue(this);
        FixturesActivity fa = this; //passing through to recycler view adapter, so we can navigate from Fixture to Event view
        //call for previous fixtures
        StringRequest stringRequestLast = new StringRequest(Request.Method.GET, getLastFixturesURL(selectedLeague),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //call getLeagueFromJSON and parse the response to a new league list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        lastFixtures = jsonToData.getFixtureFromJSON(response);

                        if(lastFixtures == null || lastFixtures.isEmpty()) {
                            //we hit limit, set timestamp in prefs and show message
                            setUsageTimerInSharedPrefs();
                            showLimitReachedMessage();
                            return;
                        }
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
        //call for next fixtures
        StringRequest stringRequestNext = new StringRequest(Request.Method.GET, getNextFixturesURL(selectedLeague),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //call getLeagueFromJSON and parse the response to a new league list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        nextFixtures = jsonToData.getFixtureFromJSON(response);

                        if(nextFixtures == null || nextFixtures.isEmpty()) {
                            //we hit limit, set timestamp in prefs and show message
                            setUsageTimerInSharedPrefs();
                            showLimitReachedMessage();
                            return;
                        }
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
                params.put("x-rapidapi-key", "api key here, should use enviroment variables"); //old key invalid anyway, but for clarity
                return params;
            }
        };
        //queues the request finish listener
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                //makes sure both calls finished before proceeding
                if(nextFixtures != null && lastFixtures != null){
                    Log.d("DEBUGDB", "onRequestFinished: both not null");

                    lastFixtures.sort(new SortByTime());
                    nextFixtures.sort(new SortByTime());

                    long nextFixtureRefresh = System.currentTimeMillis()+86400000; //set default to a day later, would only be used at end of season if there are no upcoming games
                    if(!nextFixtures.isEmpty())
                        nextFixtureRefresh = Converters.longFromLdt(nextFixtures.get(0).getDateTime().plusMinutes(100)); //set next fixture refresh for 100 minutes after the start of the next game

                    Log.d("DEBUGDB", "Next Expected end of game: " + nextFixtureRefresh);

                    //combine fixtures
                    fixturesData.addAll(lastFixtures);
                    fixturesData.addAll(nextFixtures);

                    //clear table
                    fixtureDao.clearTable();
                    //insert new data
                    fixtureDao.insert(fixturesData);

                    //write next fixture refresh to DB
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

        //submits requests
        queue.add(stringRequestLast);
        queue.add(stringRequestNext);
    }

    public String getLastFixturesURL(String id) {
        return "https://v2.api-football.com/fixtures/league/" + id + "/last/20?timezone=Europe/London";
    }

    public String getNextFixturesURL(String id) {
        return "https://v2.api-football.com/fixtures/league/" + id + "/next/20?timezone=Europe/London";
    }

    public void switchToEvents(int fixtureId) {
        Intent intent = new Intent(this, EventsActivity.class); //Intent, the activity we want to switch to
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); //stops animation on switching activity, stops annoying flicker
        intent.putExtra("FIXTURE_ID", fixtureId);
        startActivity(intent); //start activity via context that called it
    }

    //sort comparator so we can sort by time of kickoff
    public class SortByTime implements Comparator<FixtureData>
    {
        @Override
        public int compare(FixtureData o1, FixtureData o2) {
            return o1.getDateTime().compareTo(o2.getDateTime());
        }
    }

    private void refreshDB(){
        requestFixtureUpdate();
    }

    //context menu handling
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_Refresh:
                refreshDB();
                return true;
            case R.id.mi_ClearDB:
                wipeDB();
                //show are you sure, yes or no warning
                //then wipe the two DB's here, and get access to Events DB and wipe that to
                return true;
            //case R.id.mi_FASWL:
            //    //changeLeagueSelectionTo("2745");
            //    return true;
            //case R.id.mi_WC:
            //    changeLeagueSelectionTo("3015");
            //    return true;
        }
        return true;
    }
}

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.JsonToDataTask;
import uk.ash.womensfootball.R;
import uk.ash.womensfootball.event.EventsActivity;
import uk.ash.womensfootball.league.LeagueRecyclerViewAdapter;


public class FixturesActivity extends ActivityBase {
    private List<FixtureData> fixturesData;
    private String selectedLeague = "2745";
    private FixtureDao fixtureDao;
    private long lastDBRefresh;// = System.currentTimeMillis();
    private long MIN_AGE = 600000; //milliseconds, 60,000 in 1 minute

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

        selectedLeague = getSharedPreferencesSelectedLeague();

        // TODO
        //currently selected league, not implemented

        boolean shouldRefreshData = false;
        //check time of last refresh
        Long now = System.currentTimeMillis();
        if (now - getSharedPreferencesLastUpdate("FIXTURE", selectedLeague) > MIN_AGE) {
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

        fixturesData = getTestData();

        if (shouldRefreshData) {
            requestFixtureUpdate();
        } else {
            RecyclerView recyclerView = findViewById(R.id.rv_FixtureTable);
            RecyclerView.Adapter adapter = new FixtureRecyclerViewAdapter(getApplicationContext(), fixturesData, this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

    public void requestFixtureUpdate() {
        //send Volley request to url
        RequestQueue queue = Volley.newRequestQueue(this);

        FixturesActivity fa = this; //passing through to recycler view adapter, so we can navigate from Fixture to Event view
        //TODO need to edit this string getLeagueURL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getLeagueURL(selectedLeague),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d(TAG, "onResponse: " + response);

                        //call getLeagueFromJSON and parse the response to a new league list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        fixturesData = jsonToData.getFixtureFromJSON(getApplicationContext(), response);

                        Log.d("DEBUGDB", "Before insert: ");

                        fixtureDao.insert(fixturesData);
                        Log.d("DEBUGDB", "AFTERDB: ");

                        //Log.d("DEBUGDB", "Checking db: " + fixtureDao.findByLeagueId(getSharedPreferencesSelectedLeague()).get(0).getTeamName());

                        lastDBRefresh = System.currentTimeMillis();
                        writeSharedPreferencesDBRefresh(lastDBRefresh, "FIXTURE", getSharedPreferencesSelectedLeague());

                        //construct and add recyclerView data, need to move this out of here will probably slow app?
                        RecyclerView recyclerView = findViewById(R.id.rv_LeagueTable);
                        RecyclerView.Adapter adapter = new FixtureRecyclerViewAdapter(getApplicationContext(), fixturesData, fa);
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

    public String getLeagueURL(String id) {
        return "https://v2.api-football.com/leagueTable/" + id;
    }

    public void switchToEvents() {
        Intent intent = new Intent(this, EventsActivity.class); //Intent, the activity we want to switch to
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); //stops animation on switching activity, stops annoying flicker
        //intent.putExtra("FROM_ACTIVITY", from); //can pass vars in bundle, useful later? pass the ref to DB
        startActivity(intent); //start activity via context that called it
    }

    //returns some test data
    public List<FixtureData> getTestData() {
        List<FixtureData> dl = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime d = LocalDateTime.now();
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 1));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        dl.add(new FixtureData("Man U", "Arbroath", getBadgeForTeam(this, 0), getBadgeForTeam(this, 0), 1, 5, d, 2));
        return dl;
    }
}
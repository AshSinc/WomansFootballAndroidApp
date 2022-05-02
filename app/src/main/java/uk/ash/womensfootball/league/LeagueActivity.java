package uk.ash.womensfootball.league;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.JsonToDataTask;
import uk.ash.womensfootball.R;

public class LeagueActivity extends ActivityBase {
    private List<LeagueData> leagueData; //holds all LeagueData

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if savedInstanceState is null we need to make a new bundle and pass it with ACTIVITY key set to tell the base class which activity we are
        if (savedInstanceState == null) {
            Bundle newInstanceState = new Bundle();
            newInstanceState.putInt("ACTIVITY", 0);
            super.onCreate(newInstanceState);
        } else {
            savedInstanceState.putInt("ACTIVITY", 0);
            super.onCreate(savedInstanceState);
        }

        //checks if can update based on shared prefs timings activity and leagueId
        if (now.compareTo(getSharedPreferencesLastUpdate("FIXTURE", selectedLeague)) > 0) {
            Log.d("DEBUGDB", "DB is old should refresh");
            shouldRefreshData = true;
        } else {
            leagueData = leagueDao.findByLeagueId(selectedLeague);
            if (leagueData.size() > 0) {
                shouldRefreshData = false;
            } else
                shouldRefreshData = true;
        }

        if (NEVER_UPDATE) //debug variable
            shouldRefreshData = false;

        //begin refresh call or pass leagueData made of database entries
        if (shouldRefreshData) {
            requestLeagueUpdate();
        } else {
            RecyclerView recyclerView = findViewById(R.id.rv_LeagueTable);
            RecyclerView.Adapter adapter = new LeagueRecyclerViewAdapter(getApplicationContext(), leagueData);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
        //setUsageTimerInSharedPrefs(); //Uncomment these if you want to test hitting data limit
        //showLimitReachedMessage(); //Uncomment these if you want to test hitting data limit
    }

    public void requestLeagueUpdate() {
        if (checkAPILimit()) //always check we are not over limit
            return;
        showToast("Updating League Table");
        //send Volley request to url
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getLeagueURL(selectedLeague),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //call getLeagueFromJSON and parse the response to a new league list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        leagueData = jsonToData.getLeagueFromJSON(response);

                        if (leagueData == null || leagueData.isEmpty()) {
                            //we hit limit, set timestamp in prefs and show message
                            setUsageTimerInSharedPrefs();
                            showLimitReachedMessage();
                            return;
                        }

                        //insert league data, db is updated on duplicates based on position
                        leagueDao.insert(leagueData);

                        //writes next refresh time
                        long nextDBRefresh = System.currentTimeMillis();
                        writeSharedPreferencesDBRefresh(nextDBRefresh, "LEAGUE", getSharedPreferencesSelectedLeague());

                        //passes data to recycler view
                        RecyclerView recyclerView = findViewById(R.id.rv_LeagueTable);
                        RecyclerView.Adapter adapter = new LeagueRecyclerViewAdapter(getApplicationContext(), leagueData);
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
                params.put("x-rapidapi-key", "api key here, should use enviroment variables"); //old key invalid anyway, but for clarity
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public String getLeagueURL(String id) {
        return "https://v2.api-football.com/leagueTable/" + id;
    }

    private void refreshDB() {
        requestLeagueUpdate();
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

package uk.ash.womensfootball.league;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.JsonToDataTask;
import uk.ash.womensfootball.R;


public class LeagueActivity extends ActivityBase {
    private List<LeagueData> leagueData;
    private String selectedLeague = "2745";
    private LeagueDao leagueDao;

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

        synchronized (LeagueActivity.class) {
            LeagueDatabase db = LeagueDatabase.getDatabase(this);
            leagueDao = db.leagueDao();
        }

        selectedLeague = getSharedPreferencesSelectedLeague();

        // TODO
        //currently selected league, not implemented

        boolean shouldRefreshData = false;
        //check time of last refresh
        Long now = System.currentTimeMillis()/1000;

        if(now.compareTo(getSharedPreferencesLastUpdate("FIXTURE", selectedLeague)) > 0){
            Log.d("DEBUGDB", "DB is old should refresh");
            shouldRefreshData = true;
        }
        else{
            leagueData = leagueDao.findByLeagueId(selectedLeague);
            if (leagueData.size() > 0) {
                shouldRefreshData = false;
            }
            else
                shouldRefreshData = true;
        }
        if(NEVER_UPDATE)
            shouldRefreshData = false; //TODO remove
        if (shouldRefreshData) {
            requestLeagueUpdate();
        } else {
            RecyclerView recyclerView = findViewById(R.id.rv_LeagueTable);
            RecyclerView.Adapter adapter = new LeagueRecyclerViewAdapter(getApplicationContext(), leagueData);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

    public void requestLeagueUpdate() {
        //send Volley request to url
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getLeagueURL(selectedLeague),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //call getLeagueFromJSON and parse the response to a new league list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        leagueData = jsonToData.getLeagueFromJSON(getApplicationContext(), response);

                        leagueDao.insert(leagueData);

                        Log.d("DEBUGDB", "Updated DB checking: " + leagueDao.findByLeagueId(getSharedPreferencesSelectedLeague()).get(0).getTeamName());

                        long nextDBRefresh = System.currentTimeMillis();
                        writeSharedPreferencesDBRefresh(nextDBRefresh, "LEAGUE", getSharedPreferencesSelectedLeague());

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
}
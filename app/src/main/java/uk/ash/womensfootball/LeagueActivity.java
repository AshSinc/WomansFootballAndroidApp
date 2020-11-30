package uk.ash.womensfootball;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class LeagueActivity extends ActivityBase {
    private List<LeagueData> leagueData;
    private String selectedLeague = "2745";
    private LeagueDao leagueDao;
    private long lastDBRefresh;// = System.currentTimeMillis();
    private long MIN_AGE = 600000; //milliseconds, 60,000 in 1 minute
    //MIN_AGE should be once per day?
    //but tracking additional changes manually where possible?

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

        //need to check saved instance state and just reload data, not resend the request
        // TODO
        // restore the instance state if there is something to restore
        /*if (savedInstanceState != null){
            // get details from the savedInstanceState
            String locationName = savedInstanceState.getString(LOCATION_NAME);
            if (locationName != null) {
                // we have some saved LocationForecast details
                int maxTemp = savedInstanceState.getInt(MAX_TEMP);
                int minTemp = savedInstanceState.getInt(MIN_TEMP);
                String date = savedInstanceState.getString(DATE);
                String weather = savedInstanceState.getString(WEATHER);

                // update the forecast member variable with the details from the
                // saved instance state
                forecast = new LocationForecast();
                forecast.setLocationName(locationName);
                forecast.setMaxTemp(maxTemp);
                forecast.setMinTemp(minTemp);
                forecast.setDate(date);
                forecast.setWeather(weather);

                // display the forecast
                displayLocationForecast();
            }
        }*/

        // TODO
        //currently selected league, not implemented

        boolean shouldRefreshData = false;
        //check time of last refresh
        Long now = System.currentTimeMillis();
        if(now - getSharedPreferencesLastUpdate() > MIN_AGE){
            Log.d("DEBUGDB", "DB is old should refresh");
            shouldRefreshData = true;
        }
        else{
            leagueData = leagueDao.findByLeagueId(getSharedPreferencesSelectedLeague());
            if (leagueData.size() > 0) {
                Log.d("DEBUGDB", "Found " + leagueData.get(0));
                shouldRefreshData = false;
            }
            else
                shouldRefreshData = true;
        }

        if (shouldRefreshData) {
            requestLeagueUpdate();
        } else {
            Log.d("DEBUGDB", "Found " + leagueData.get(0).getTeamName());
            RecyclerView recyclerView = findViewById(R.id.rv_LeagueTable);
            RecyclerView.Adapter adapter = new LeagueRecyclerViewAdapter(getApplicationContext(), leagueData);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }
    }

    private String getSharedPreferencesSelectedLeague(){
        return sharedPreferences.getString(getString(R.string.shared_pref_league_id), new String("2745"));
    }

    private Long getSharedPreferencesLastUpdate(){
        return sharedPreferences.getLong(getString(R.string.shared_pref_league_update), 0);
    }

    private void writeSharedPreferencesSelectedLeague(String leagueId){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.shared_pref_league_id));
        editor.apply();
        editor.putString(getString(R.string.shared_pref_league_id), leagueId);
        editor.apply();
    }

    private void writeSharedPreferencesDBRefresh(long time){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.shared_pref_league_update));
        editor.apply();
        editor.putLong(getString(R.string.shared_pref_league_update), lastDBRefresh);
        editor.apply();
    }

    public void requestLeagueUpdate() {
        //send Volley request to url
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getLeagueURL(selectedLeague),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d(TAG, "onResponse: " + response);

                        //call getLeagueFromJSON and parse the response to a new league list object
                        JsonToLeagueTask jsonToLeague = new JsonToLeagueTask();
                        leagueData = jsonToLeague.getLeagueFromJSON(getApplicationContext(), response);

                        Log.d("DEBUGDB", "Before insert: ");
                        //add to database and update timestamp
                        // TODO
                        leagueDao.insert(leagueData);
                        Log.d("DEBUGDB", "AFTERDB: ");

                        Log.d("DEBUGDB", "Checking db: " + leagueDao.findByLeagueId(getSharedPreferencesSelectedLeague()).get(0).getTeamName());

                        lastDBRefresh = System.currentTimeMillis();
                        writeSharedPreferencesDBRefresh(lastDBRefresh);

                        //construct and add recyclerView data, need to move this out of here will probably slow app?
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

    //returns some test data
    public List<LeagueData> getTestData() {
        List<LeagueData> dl = new ArrayList<>();
       /* dl.add(new LeagueData("Man U", 10, 5, 3, 5, 5, 10,1));
        dl.add(new LeagueData("Celtic", 8, 5, 2, 1, 0, 13,2));
        dl.add(new LeagueData("Rangers", 3, 5, 3, 1, -1, 11,3));
        dl.add(new LeagueData("Arbroath", 10, 5, 3, 1, -2, 55,4));
        dl.add(new LeagueData("Elgin", 12, 3, 1, 3, -3, 12,4));
        dl.add(new LeagueData("Forres", 10, 5, 3, 1, -4, 10,4));
        dl.add(new LeagueData("Aberdeen", 1, 5, 3, 1, -6, 10,4));
        dl.add(new LeagueData("Lossie", 2, 5, 3, 1, -8, 10,4));
        dl.add(new LeagueData("Montrose", 3, 0, 0, 10, -8, 0,4));
        dl.add(new LeagueData("Man U1", 10, 5, 3, 5, -8, 10,4));
        dl.add(new LeagueData("Celtic2",  8, 5, 2, 1, -8, 13,4));
        dl.add(new LeagueData("Rangers2",  3, 5, 3, 1, -8, 11,4));
        dl.add(new LeagueData("Arbroath2",  10, 5, 3, 1, -8, 55,4));
        dl.add(new LeagueData("Elgin2",  12, 3, 1, 3, -8, 12,4));
        dl.add(new LeagueData("Forres2", 10, 5, 3, 1, -8, 10,4));
        dl.add(new LeagueData("Aberdeen2",  1, 5, 3, 1, -8, 10,4));
        dl.add(new LeagueData("Lossie2", 2, 5, 3, 1, -8, 10,4));*/
        //dl.add(new LeagueData("Montrose2", getBadgeForTeam(this, 0), 3, 0, 0, 10, -81, 0,4));
        return dl;
    }
}
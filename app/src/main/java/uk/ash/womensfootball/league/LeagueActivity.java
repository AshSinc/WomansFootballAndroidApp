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

        selectedLeague = getSharedPreferencesSelectedLeague();

        // TODO
        //currently selected league, not implemented

        boolean shouldRefreshData = false;
        //check time of last refresh
        Long now = System.currentTimeMillis();
        if(now - getSharedPreferencesLastUpdate("LEAGUE", selectedLeague) > MIN_AGE){
            Log.d("DEBUGDB", "DB is old should refresh");
            shouldRefreshData = true;
        }
        else{
            leagueData = leagueDao.findByLeagueId(selectedLeague);
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

    /*private String getSharedPreferencesSelectedLeague(){
        return sharedPreferences.getString(getString(R.string.shared_pref_league_id), new String("2745"));
    }

    private Long getSharedPreferencesLastUpdate(){
        return sharedPreferences.getLong(getString(R.string.shared_pref_league_update), 0);
    }*/

    /*private void writeSharedPreferencesSelectedLeague(String leagueId){
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
        editor.putLong(getString(R.string.shared_pref_league_update), time);
        editor.apply();
    }*/

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
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        leagueData = jsonToData.getLeagueFromJSON(getApplicationContext(), response);

                        Log.d("DEBUGDB", "Before insert: ");

                        leagueDao.insert(leagueData);
                        Log.d("DEBUGDB", "AFTERDB: ");

                        Log.d("DEBUGDB", "Checking db: " + leagueDao.findByLeagueId(getSharedPreferencesSelectedLeague()).get(0).getTeamName());

                        lastDBRefresh = System.currentTimeMillis();
                        writeSharedPreferencesDBRefresh(lastDBRefresh, "LEAGUE", getSharedPreferencesSelectedLeague());

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

    /*@Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "App is saving instance bundle");
        /*if (forecast != null) {
            // need to add location name, minimum temperature, maximum temperature, date, weather for
            // the forecast to the outState
            outState.putString(LOCATION_NAME, forecast.getLocationName());
            outState.putInt(MIN_TEMP, forecast.getMinTemp());
            outState.putInt(MAX_TEMP, forecast.getMaxTemp());
            outState.putString(DATE, forecast.getDate());
            outState.putString(WEATHER, forecast.getWeather());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "App is in onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "App is in onPause");
        //updateSharedPreferencesWithFavouriteLocation();
    }*/
}
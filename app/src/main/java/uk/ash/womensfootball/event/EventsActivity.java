package uk.ash.womensfootball.event;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.Converters;
import uk.ash.womensfootball.JsonToDataTask;
import uk.ash.womensfootball.R;
import uk.ash.womensfootball.fixture.FixtureData;


public class EventsActivity extends ActivityBase {
    private List<EventData> eventData;
    private FixtureData fixture;
    private EventDao eventDao;
    private int fixtureID, homeTeamID, awayTeamID, homeTeamScore, awayTeamScore;
    private String homeTeamName, awayTeamName;
    private boolean gameComplete;
    private LocalDateTime kickoffTime;

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

        //we need an intent because it should have been passed from fixture
        if (intent == null){
            showToast("Error : No intent");
            return; //TODO should give a fail message but for now this will do, should never occur
        }

        //stores fixtureID from intent
        fixtureID = intent.getIntExtra("FIXTURE_ID", -1);

        //checks if fixture id -1, it means theres been an error, should never happen but just in case
        if(fixtureID == -1){
            showToast("Error : No Event Id");
            return;
        }

        //access events DB and retrieves Fixture information from FixtureDb based on fixtureID
        synchronized (EventsActivity.class) {
            EventDatabase eventsDb = EventDatabase.getDatabase(this);
            eventDao = eventsDb.eventDao();
            fixture = fixtureDao.findFixtureById(fixtureID);
        }

        //if found Fixture then get required data for the top bar
        if(fixture != null){
            fixtureID = fixture.getFixtureId();
            homeTeamID = fixture.getTeamIdH();
            awayTeamID = fixture.getTeamIdA();
            homeTeamScore = fixture.getHomeScore();
            awayTeamScore = fixture.getAwayScore();
            homeTeamName = fixture.getTeamNameH();
            awayTeamName = fixture.getTeamNameA();
            gameComplete = fixture.getGameComplete();
            kickoffTime = fixture.getDateTime();
        }
        else{
            //something broke so return
            showToast("Error : Fixture is null, refresh fixtures");
            return; //could force back to fixtures
        }

        //set the header info from savedInstanceState data
        ((TextView) findViewById(R.id.tv_time)).setText(kickoffTime.format(TIME_PATTERN));
        ((TextView) findViewById(R.id.tv_TeamNameH)).setText(homeTeamName);
        ((TextView) findViewById(R.id.tv_TeamNameA)).setText(awayTeamName);
        ((TextView) findViewById(R.id.tv_homeScore)).setText(Integer.toString(homeTeamScore));
        ((TextView) findViewById(R.id.tv_awayScore)).setText(Integer.toString(awayTeamScore));
        ((ImageView) findViewById(R.id.iv_BadgeFixtureH)).setForeground(ActivityBase.getBadgeForTeam(this, homeTeamID));
        ((ImageView) findViewById(R.id.iv_BadgeFixtureA)).setForeground(ActivityBase.getBadgeForTeam(this, awayTeamID));

        //gets eventdata from event database based on fixtureId
        eventData = eventDao.findByFixtureId(fixtureID);
        if (eventData.isEmpty()) {
            if (gameComplete) {
                Log.d(TAG, "onCreate: game is over but we have no data, refresh");
                shouldRefreshData = true;
            }
            else{
                if (now.compareTo(Converters.longFromLdt(fixture.getDateTime())) > 0){ //TODO check this
                    Log.d(TAG, "onCreate: eventData is empty and time is after game start, should refresh");
                    shouldRefreshData = true;
                }
                else{
                    Log.d(TAG, "onCreate: eventData is empty and time is before game start, no need to refresh");
                }
            }
        } else {
            if (gameComplete) {
                Log.d(TAG, "onCreate: game is over, dont refresh");
                shouldRefreshData = false;
            } else if (now.compareTo(fixture.getNextEventUpdate()) > 0) {
                Log.d(TAG, "onCreate: refresh threshold of 10 minutes has passed");
                shouldRefreshData = true;
            }
        }

        if(NEVER_UPDATE) //debug variable
            shouldRefreshData = false;

        //begin refresh call or pass eventData made of database entries
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
        if(checkAPILimit()) //always check we are not over limit
            return;
        showToast("Updating Events");
        //send Volley request to url
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getEventURL(String.valueOf(fixtureID)),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //call getEventFromJSON and parse the response to a new event list object
                        JsonToDataTask jsonToData = new JsonToDataTask();
                        eventData = jsonToData.getEventFromJSON(response, fixtureID, homeTeamID);

                        if(eventData == null || eventData.isEmpty()) {
                            //we hit limit, set timestamp in prefs and show message
                            setUsageTimerInSharedPrefs();
                            showLimitReachedMessage();
                            return;
                        }

                        //insert to eventData db
                        eventDao.insert(eventData);

                        //update the fixture timing in fixture DB
                        long nextEventRefresh = System.currentTimeMillis() + 600000; //set next refresh to 10 mins later
                        fixture.setNextEventUpdate(nextEventRefresh);
                        fixtureDao.update(fixture);

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
                params.put("x-rapidapi-key", "api key here, should use enviroment variables"); //old key invalid anyway, but for clarity
                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //gets the drawables associated with type of event
    public static Drawable getDrawableForEvent(Context context, String type) {
        Drawable d;
        switch (type) {
            case ("Goal"):
                d = ContextCompat.getDrawable(context, R.drawable.goal);
                break;
            case ("Yellow"):
                d = ContextCompat.getDrawable(context, R.drawable.yellowcard);
                break;
            case ("Red"):
                d = ContextCompat.getDrawable(context, R.drawable.redcard);
                break;
            case ("Sub"):
                d = ContextCompat.getDrawable(context, R.drawable.sub);
                break;
            case ("Miss"):
                d = ContextCompat.getDrawable(context, R.drawable.miss);
                break;
            default:
                d = ContextCompat.getDrawable(context, android.R.drawable.btn_star_big_on);
        }
        return d;
    }

    //returns events url endpoint for api
    public String getEventURL(String id) {
        return "https://v2.api-football.com/events/" + id;
    }

    //make a call to update events if game in progress
    private void refreshDB(){
        if(!gameComplete){
            Long now = System.currentTimeMillis() / 1000; //system time in seconds
            if (now.compareTo(Converters.longFromLdt(fixture.getDateTime())) > 0) { //if game has started but isnt complete
                requestEventsUpdate();
            }
            else
                showToast("Game not started");
        }
        else
            showToast("Game is over");
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

package uk.ash.womensfootball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;

import java.time.format.DateTimeFormatter;

import uk.ash.womensfootball.fixture.FixturesActivity;
import uk.ash.womensfootball.league.LeagueActivity;

//acts as a base class for the three Activity tabs
public class ActivityBase extends AppCompatActivity {

    protected static String TAG = "DebugTag";
    //custom date/time pattern
    //public static DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("d LLL 'Kickoff' HH:mm");
    public static DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("d LLL YY 'Kickoff' HH:mm");
    protected static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // instantiate the sharedPreferences
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_file), MODE_PRIVATE);

        int activitySelected = 0; //tracks currently selected activity, default to 0

        // checks savedInstanceState for ACTIVITY number for setting setContentView to correct layout
        if (savedInstanceState != null) {
            activitySelected = savedInstanceState.getInt("ACTIVITY", 0);
            switch (activitySelected) {
                case 0:
                    setContentView(R.layout.activity_league);
                    break;
                case 1:
                    setContentView(R.layout.activity_fixtures);
                    break;
                case 2:
                    setContentView(R.layout.activity_events);
                    break;
            }
        } else
            setContentView(R.layout.activity_league);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tb_Toolbar);
        //toolbar.setTitle(getString(R.string.res_league)); //change the ActionBarTitle
        setSupportActionBar(toolbar); // Sets the Toolbar to act as the ActionBar for this Activity window.
        getSupportActionBar().setDisplayShowTitleEnabled(false); //hide the title, possibly use for displaying League Name

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout); //get the tab bar
        tabLayout.selectTab(tabLayout.getTabAt(activitySelected), true);  //re-selects the correct tab using the value passed by savedInstanceState
        //adds a new listener to the tabLayout, and passes a new TabSelectedActivitySwitch to control activity switching, have to pass context, this passes the child context if called from child instance
        tabLayout.addOnTabSelectedListener(new ActivityBase.TabSelectedActivitySwitch(this));
    }

    //adds dropdown menu to Actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate toolbar_menu, defined in res>menu
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    //switches to passed activity, needs Context as its static and called from static listener TabSelectedActivitySwitch
    private static void switchActivityTo(Class activity, Context context) {
        Intent intent = new Intent(context, activity); //Intent, the activity we want to switch to
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); //stops animation on switching activity, stops annoying flicker
        //intent.putExtra("FROM_ACTIVITY", from); //can pass vars in bundle, useful later?
        context.startActivity(intent); //start activity via context that called it
    }

    protected String getSharedPreferencesSelectedLeague() {
        return sharedPreferences.getString(getString(R.string.shared_pref_league_id), new String("2745"));
    }

    protected void writeSharedPreferencesSelectedLeague(String leagueId) { //TODO need to use this
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(R.string.shared_pref_league_id));
        editor.apply();
        editor.putString(getString(R.string.shared_pref_league_id), leagueId);
        editor.apply();
    }

    private int getPreferenceIdFor(String activityName, String id){
        int prefId = -1;
        if(activityName.matches("LEAGUE")) {
            if(id.matches("2745")) {
                prefId = R.string.shared_pref_league_2745_update;
                /*editPref = getString(R.string.shared_pref_league_2745_update);
                editPref = getString(R.string.shared_pref_league_2745_update);*/
            }
        }
        else if(activityName.matches("FIXTURE")) {
            if(id.matches("2745")) {
                prefId = R.string.shared_pref_fixture_2745_update;
            }
        }
        //Log.d(TAG, "getPreferenceStringFor: " + activityName + " " + id + " = " + prefId);
        return prefId;
    }

    protected Long getSharedPreferencesLastUpdate(String activityName, String id) {
        int prefId = getPreferenceIdFor(activityName, id);
        return sharedPreferences.getLong(getString(prefId), 0);
    }

    protected void writeSharedPreferencesDBRefresh(long lastUpdate, String activityName, String id) {
        Log.d(TAG, "writeSharedPreferencesDBRefresh: " + lastUpdate + activityName + id );
        int prefId = getPreferenceIdFor(activityName, id); //TODO what was i doing in there? Why does that work 0o

        if(prefId == -1) {
            Log.d(TAG, "writeSharedPreferencesDBRefresh: Not saving prefId is -1");
            return;
        }

        Log.d(TAG, "writeSharedPreferencesDBRefresh: Saving DB refresh time to shared pref");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(getString(prefId));
        editor.apply();
        editor.putLong(getString(prefId), lastUpdate);
        editor.apply();
}

    @Override
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
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "App is in onResume");
        /*if (forecast != null)
            displayLocationForecast();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "App is in onPause");
        //updateSharedPreferencesWithFavouriteLocation();
    }

    //returns the badge for Team, needed in all activities
    //todo - need to set up an array or DB of teams and get all their badges stored - associate id and teams/badges
    public static Drawable getBadgeForTeam(Context context, int id) {
        Drawable d;
        switch (id) {
            case (1845):
                d = ContextCompat.getDrawable(context, R.drawable.b1845);
                break;
            case (1846):
                d = ContextCompat.getDrawable(context, R.drawable.b1846);
                break;
            case (1850):
                d = ContextCompat.getDrawable(context, R.drawable.b1850);
                break;
            case (1852):
                d = ContextCompat.getDrawable(context, R.drawable.b1852);
                break;
            case (1853):
                d = ContextCompat.getDrawable(context, R.drawable.b1853);
                break;
            case (1854):
                d = ContextCompat.getDrawable(context, R.drawable.b1854);
                break;
            case (1855):
                d = ContextCompat.getDrawable(context, R.drawable.b1855);
                break;
            case (1856):
                d = ContextCompat.getDrawable(context, R.drawable.b1856);
                break;
            case (1857):
                d = ContextCompat.getDrawable(context, R.drawable.b1857);
                break;
            case (4898):
                d = ContextCompat.getDrawable(context, R.drawable.b4898);
                break;
            case (4899):
                d = ContextCompat.getDrawable(context, R.drawable.b4899);
                break;
            case (14219):
                d = ContextCompat.getDrawable(context, R.drawable.b14219);
                break;
            default:
                d = ContextCompat.getDrawable(context, android.R.drawable.btn_star_big_on);
        }
        return d;
    }

protected static class TabSelectedActivitySwitch implements TabLayout.OnTabSelectedListener {

    private Context context; //have to track context as we are moving through static classes/methods

    public TabSelectedActivitySwitch(Context c) {
        context = c;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //get current tab number and call switchActivityTo with the desired destination and pass context that called this.
        switch (tab.getPosition()) {
            case 0:
                Log.d(TAG, "switch0: tab ");
                switchActivityTo(LeagueActivity.class, context);
                break;
            case 1:
                Log.d(TAG, "switch1: tab ");
                switchActivityTo(FixturesActivity.class, context);
                break;
            //case 2: //not needed
            //    Log.d(TAG, "switch2: tab");
            //    switchActivityTo(EventsActivity.class, context);
            //    break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
}




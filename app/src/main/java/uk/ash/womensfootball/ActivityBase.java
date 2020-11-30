package uk.ash.womensfootball;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.tabs.TabLayout;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

//acts as a base class for the three Activity tabs
public class ActivityBase extends AppCompatActivity {

    protected static String TAG = "DebugTag";
    //custom date/time pattern
    //protected static DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("d LLL yy 'Kickoff' hh:mm a");
    protected static DateTimeFormatter TIME_PATTERN = DateTimeFormatter.ofPattern("d LLL 'Kickoff' hh:mm a");
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


    //returns the badge for Team, needed in all activities
    //todo - need to set up an array or DB of teams and get all their badges stored - associate id and teams/badges
    public static Drawable getBadgeForTeam(Context context, int id){
        Drawable d;
        switch (id){
            //case(0): d = ContextCompat.getDrawable(context, R.drawable.team0); break;
            //case(1): d = ContextCompat.getDrawable(context, R.drawable.team1); break;
           // case(2): d = ContextCompat.getDrawable(context, R.drawable.team2); break;
            case(3): d = null; break;
            case(4): d = null; break;
            case(5): d = null; break;
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




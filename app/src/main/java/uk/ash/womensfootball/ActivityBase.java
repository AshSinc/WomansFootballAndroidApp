package uk.ash.womensfootball;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.tabs.TabLayout;

//acts as a base class for the three Activity tabs
public class ActivityBase extends AppCompatActivity {

    private static String TAG = "DebugTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int activitySelected = 0; //tracks currently selected activity, assume start at 0

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
        getSupportActionBar().setDisplayShowTitleEnabled(false); //hide the title, not needed as we have tabs right now

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout); //get the tab bar
        tabLayout.selectTab(tabLayout.getTabAt(activitySelected), true);  //re-selects the correct tab using the value passed by savedInstanceState
        //adds a new listener to the tabLayout, and passes a new TabSelectedActivitySwitch to control activity switching, have to pass context, this passes the child context if called from child it seems, need to test
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
                case 2:
                    Log.d(TAG, "switch2: tab");
                    switchActivityTo(EventsActivity.class, context);
                    break;
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




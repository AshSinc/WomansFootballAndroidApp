package uk.ash.womensfootball.event;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.R;


public class EventsActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if savedInstanceState is null we need to make a new bundle and pass it with ACTIVITY key set to tell the base class which activity we are
        if (savedInstanceState == null){
            Bundle newInstanceState = new Bundle();
            newInstanceState.putInt("ACTIVITY", 2);
            super.onCreate(newInstanceState);
        }
        else{
            savedInstanceState.putInt("ACTIVITY", 2);
            super.onCreate(savedInstanceState);
        }

        //get some test data
        EventData eventData;
        eventData = getTestData();

        //set the header info from eventData
        ((TextView)findViewById(R.id.tv_time)).setText(eventData.getDateTime().format(TIME_PATTERN));
        ((TextView)findViewById(R.id.tv_TeamNameH)).setText(eventData.getTeamNameH());
        ((TextView)findViewById(R.id.tv_TeamNameA)).setText(eventData.getTeamNameA());
        ((TextView)findViewById(R.id.tv_homeScore)).setText(String.valueOf(eventData.getHomeScore()));
        ((TextView)findViewById(R.id.tv_awayScore)).setText(String.valueOf(eventData.getAwayScore()));
        //((ImageView)findViewById(R.id.iv_BadgeFixtureH)).setImageResource(eventData.getImagePathH());
        //((ImageView)findViewById(R.id.iv_BadgeFixtureA)).setImageResource(eventData.getImagePathA());


        RecyclerView recyclerView = findViewById(R.id.rv_EventTable);
        RecyclerView.Adapter adapter = new EventRecyclerViewAdapter(getApplicationContext(), eventData);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    //returns some test data
    public EventData getTestData(){
        Drawable yellow = ContextCompat.getDrawable(this, R.drawable.yellowcard);
        List<EventData.EventsItem> eI = new ArrayList<>();//(int t, int id, String desc){
        eI.add(new EventData.EventsItem(false, 3, 0, "Someone kicked it",getDrawableForEvent(0)));
        eI.add(new EventData.EventsItem(false, 5, 0, "Someone kicked it",getDrawableForEvent(0)));
        eI.add(new EventData.EventsItem(true, 15, 1, "Yellow for Muzz!",getDrawableForEvent(1)));
        eI.add(new EventData.EventsItem(true, 30, 2, "Goal for Muzz!",getDrawableForEvent(2)));
        eI.add(new EventData.EventsItem(false, 50, 2, "Goal for Ryan!",getDrawableForEvent(2)));
        eI.add(new EventData.EventsItem(false, 72, 0, "Someone kicked it",getDrawableForEvent(0)));
        eI.add(new EventData.EventsItem(false, 3, 0, "Someone kicked it",getDrawableForEvent(0)));
        eI.add(new EventData.EventsItem(false, 5, 0, "Someone kicked it",getDrawableForEvent(0)));
        eI.add(new EventData.EventsItem(true, 15, 1, "Yellow for Muzz!",getDrawableForEvent(1)));
        eI.add(new EventData.EventsItem(true, 30, 2, "Goal for Muzz!",getDrawableForEvent(2)));
        eI.add(new EventData.EventsItem(false, 50, 2, "Goal for Ryan!",getDrawableForEvent(2)));
        eI.add(new EventData.EventsItem(false, 72, 0, "Someone kicked it",getDrawableForEvent(0)));
        eI.add(new EventData.EventsItem(false, 3, 0, "Someone kicked it",getDrawableForEvent(0)));
        eI.add(new EventData.EventsItem(false, 5, 0, "Someone kicked it",getDrawableForEvent(0)));
        eI.add(new EventData.EventsItem(true, 15, 1, "Yellow for Muzz!",getDrawableForEvent(1)));
        eI.add(new EventData.EventsItem(true, 30, 2, "Goal for Muzz!",getDrawableForEvent(2)));
        eI.add(new EventData.EventsItem(false, 50, 2, "Goal for Ryan!",getDrawableForEvent(2)));
        eI.add(new EventData.EventsItem(false, 72, 0, "Someone kicked it",getDrawableForEvent(0)));
        EventData data = new EventData("Man U", "Arbroath", null, null, 0, 6, LocalDateTime.now(), eI);
        return data;
    }

    public Drawable getDrawableForEvent(int id){
        Drawable d;
        switch (id){
            case(0): d = ContextCompat.getDrawable(this, R.drawable.goal); break;
            case(1): d = ContextCompat.getDrawable(this, R.drawable.yellowcard); break;
            case(2): d = ContextCompat.getDrawable(this, R.drawable.redcard); break;
            case(3): d = null; break;
            case(4): d = null; break;
            case(5): d = null; break;
            default:
                d = ContextCompat.getDrawable(this, android.R.drawable.btn_star_big_on);
        }
        return d;
    }
}
package uk.ash.womensfootball;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class FixturesActivity extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //if savedInstanceState is null we need to make a new bundle and pass it with ACTIVITY key set to tell the base class which activity we are
        if (savedInstanceState == null){
            Log.d("DEBUG", "savedInstanceState is null");
            Bundle newInstanceState = new Bundle();
            newInstanceState.putInt("ACTIVITY", 1);
            super.onCreate(newInstanceState);
        }
        else{
            Log.d("DEBUG", "savedInstanceState is not null");
            savedInstanceState.putInt("ACTIVITY", 1);
            super.onCreate(savedInstanceState);
        }
        List<FixtureData> fixturesData;
        fixturesData = getTestData();

        RecyclerView recyclerView = findViewById(R.id.rv_FixtureTable);
        RecyclerView.Adapter adapter = new FixtureRecyclerViewAdapter(getApplicationContext(), fixturesData, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    public void switchToEvents(){
        Intent intent = new Intent(this, EventsActivity.class); //Intent, the activity we want to switch to
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); //stops animation on switching activity, stops annoying flicker
        //intent.putExtra("FROM_ACTIVITY", from); //can pass vars in bundle, useful later? pass the ref to DB
        startActivity(intent); //start activity via context that called it
    }

    //returns some test data
    public List<FixtureData> getTestData(){
        List<FixtureData> dl = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime d = LocalDateTime.now();

        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));
        dl.add(new FixtureData("Man U", "Arbroath", "", "", 1, 5, d));



        return dl;
    }
}
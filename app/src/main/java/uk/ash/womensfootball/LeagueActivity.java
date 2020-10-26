package uk.ash.womensfootball;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public class LeagueActivity extends ActivityBase {

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

        List<LeagueData> leagueData;
        leagueData = getTestData();

        RecyclerView recyclerView = findViewById(R.id.rv_LeagueTable);
        RecyclerView.Adapter adapter = new LeagueRecyclerViewAdapter(getApplicationContext(), leagueData);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    //returns some test data
    public List<LeagueData> getTestData() {
        List<LeagueData> dl = new ArrayList<>();
        dl.add(new LeagueData("Man U", getBadgeForTeam(0), 10, 5, 3, 5, 5, 10));
        dl.add(new LeagueData("Celtic", getBadgeForTeam(0), 8, 5, 2, 1, 0, 13));
        dl.add(new LeagueData("Rangers", getBadgeForTeam(0), 3, 5, 3, 1, -1, 11));
        dl.add(new LeagueData("Arbroath", getBadgeForTeam(0), 10, 5, 3, 1, -2, 55));
        dl.add(new LeagueData("Elgin", getBadgeForTeam(0), 12, 3, 1, 3, -3, 12));
        dl.add(new LeagueData("Forres", getBadgeForTeam(0), 10, 5, 3, 1, -4, 10));
        dl.add(new LeagueData("Aberdeen", getBadgeForTeam(0), 1, 5, 3, 1, -6, 10));
        dl.add(new LeagueData("Lossie", getBadgeForTeam(0), 2, 5, 3, 1, -8, 10));
        dl.add(new LeagueData("Montrose", getBadgeForTeam(0), 3, 0, 0, 10, -8, 0));
        dl.add(new LeagueData("Man U1", getBadgeForTeam(0), 10, 5, 3, 5, -8, 10));
        dl.add(new LeagueData("Celtic2", getBadgeForTeam(0), 8, 5, 2, 1, -8, 13));
        dl.add(new LeagueData("Rangers2", getBadgeForTeam(0), 3, 5, 3, 1, -8, 11));
        dl.add(new LeagueData("Arbroath2", getBadgeForTeam(0), 10, 5, 3, 1, -8, 55));
        dl.add(new LeagueData("Elgin2", getBadgeForTeam(0), 12, 3, 1, 3, -8, 12));
        dl.add(new LeagueData("Forres2", getBadgeForTeam(0), 10, 5, 3, 1, -8, 10));
        dl.add(new LeagueData("Aberdeen2", getBadgeForTeam(0), 1, 5, 3, 1, -8, 10));
        dl.add(new LeagueData("Lossie2", getBadgeForTeam(0), 2, 5, 3, 1, -8, 10));
        dl.add(new LeagueData("Montrose2", getBadgeForTeam(0), 3, 0, 0, 10, -8, 0));
        return dl;
    }
}
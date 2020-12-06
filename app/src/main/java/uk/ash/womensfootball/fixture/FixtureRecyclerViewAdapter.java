package uk.ash.womensfootball.fixture;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ash.womensfootball.ActivityBase;
import uk.ash.womensfootball.R;
import uk.ash.womensfootball.league.LeagueData;

public class FixtureRecyclerViewAdapter extends RecyclerView.Adapter<FixtureRecyclerViewAdapter.FixtureViewHolder> {

    //member vars for context adapter is working in
    private Context context;

    //league data to be displayed
    private List<FixtureData> data;

    private FixturesActivity fixturesActivity;

    public FixtureRecyclerViewAdapter(Context c, List<FixtureData> d, FixturesActivity fA){
        context = c;
        data = d;
        fixturesActivity = fA;
    }

    @NonNull
    @Override
    public FixtureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View dataItemView = LayoutInflater.from(context).inflate(R.layout.fixture_list_view_item, parent, false);
        FixtureViewHolder viewHolder = new FixtureViewHolder(dataItemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FixtureViewHolder holder, int pos) {
        //get data of table at position
        FixtureData entry = data.get(pos);
        int teamIdH = entry.getTeamIdH();
        int teamIdA = entry.getTeamIdA();
        //update the TextViews
        ((TextView)holder.dataItemView.findViewById(R.id.tv_TeamNameH)).setText(entry.getTeamNameH());
        ((TextView)holder.dataItemView.findViewById(R.id.tv_TeamNameA)).setText(entry.getTeamNameA());
        ((ImageView)holder.dataItemView.findViewById(R.id.iv_BadgeFixtureH)).setForeground(ActivityBase.getBadgeForTeam(context,teamIdH));
        ((ImageView)holder.dataItemView.findViewById(R.id.iv_BadgeFixtureA)).setForeground(ActivityBase.getBadgeForTeam(context,teamIdA));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_homeScore)).setText(String.valueOf(entry.getHomeScore()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_awayScore)).setText(String.valueOf(entry.getAwayScore()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_time)).setText(String.valueOf(entry.getDateTime().format(ActivityBase.TIME_PATTERN)));
    }

    @Override
    public int getItemCount() {
        if(data == null)
            return 0;
        else
            return data.size();
    }

    //sits between adapter and the View that's displaying the item, captures clicks
    class FixtureViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View dataItemView;
        private FixtureRecyclerViewAdapter adapter;

        public FixtureViewHolder(View v, FixtureRecyclerViewAdapter a) {
            super(v);
            dataItemView = v;
            adapter = a;
            dataItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition(); //get clicked item pos
            Log.d("DEBUG", "onClick: " + pos + " is fixtureID - " + data.get(pos).getFixtureId());
            //fixturesActivity.switchToEvents(data.get(pos).getFixtureId());
            fixturesActivity.switchToEvents();
        }
    }
}


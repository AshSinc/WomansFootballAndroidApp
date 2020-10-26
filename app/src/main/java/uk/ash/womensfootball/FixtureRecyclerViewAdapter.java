package uk.ash.womensfootball;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
        //update the TextViews
        ((TextView)holder.dataItemView.findViewById(R.id.tv_TeamNameH)).setText(entry.getTeamNameH());
        ((TextView)holder.dataItemView.findViewById(R.id.tv_TeamNameA)).setText(entry.getTeamNameA());
        //((ImageView)holder.dataItemView.findViewById(R.id.iv_BadgeFixtureH)).setImageResource(R.drawable.imagePathH); // need to work on this and figure out best method
        //((ImageView)holder.dataItemView.findViewById(R.id.iv_BadgeFixtureA)).setImageResource(R.drawable.imagePathA); // need to work on this and figure out best method
        ((TextView)holder.dataItemView.findViewById(R.id.tv_homeScore)).setText(String.valueOf(entry.getHomeScore()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_awayScore)).setText(String.valueOf(entry.getAwayScore()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_time)).setText(String.valueOf(entry.getDateTime().format(ActivityBase.TIME_PATTERN)));
    }

    @Override
    public int getItemCount() {
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

            Log.d("DEBUG", "onClick: " + pos + " is team - " + data.get(pos).getTeamNameA());

            //get team name from data at that pos.
            //data.get(pos).getTeamName();
            //then switch to fixtures and show only those upcoming games

            fixturesActivity.switchToEvents();

        }
    }
}


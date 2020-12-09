package uk.ash.womensfootball.league;

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

public class LeagueRecyclerViewAdapter extends RecyclerView.Adapter<LeagueRecyclerViewAdapter.LeagueViewHolder> {

    //member vars for context adapter is working in
    private Context context;

    //league data to be displayed
    private List<LeagueData> data;

    public LeagueRecyclerViewAdapter(Context c, List<LeagueData> d){
        context = c;
        data = d;
    }

    @NonNull
    @Override
    public LeagueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View dataItemView = LayoutInflater.from(context).inflate(R.layout.league_list_view_item, parent, false);
        LeagueViewHolder viewHolder = new LeagueViewHolder(dataItemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LeagueViewHolder holder, int pos) {
        //get data of table at position
        LeagueData entry = data.get(pos);
        int teamId = entry.getTeamId();
        //update the Views
        ((TextView)holder.dataItemView.findViewById(R.id.tv_pos)).setText(String.valueOf(pos+1));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_TeamName)).setText(entry.getTeamName());
        ((ImageView)holder.dataItemView.findViewById(R.id.iv_TeamBadge)).setForeground(ActivityBase.getBadgeForTeam(context,teamId));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_Played)).setText(String.valueOf(entry.getPlayed()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_Wins)).setText(String.valueOf(entry.getWins()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_Draws)).setText(String.valueOf(entry.getDraws()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_Loss)).setText(String.valueOf(entry.getLosses()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_Diff)).setText(String.valueOf(entry.getGoalDiff()));
        ((TextView)holder.dataItemView.findViewById(R.id.tv_Points)).setText(String.valueOf(entry.getPoints()));

        Log.d("IDTeams", "teamname : " + entry.getTeamName() + ", id : " + entry.getTeamId());
    }

    @Override
    public int getItemCount() {
        if(data == null)
            return 0;
        else
            return data.size();
    }

    //sits between adapter and the View that's displaying the item, captures clicks
    class LeagueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View dataItemView;
        private LeagueRecyclerViewAdapter adapter;

        public LeagueViewHolder(View v, LeagueRecyclerViewAdapter a) {
            super(v);
            dataItemView = v;
            adapter = a;
            dataItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition(); //get clicked item pos
            Log.d("DEBUG", "onClick: " + pos + " is team - " + data.get(pos).getTeamName());
            //if ever wanted to navigate based on teams
        }
    }
}

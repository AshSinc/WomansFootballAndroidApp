package uk.ash.womensfootball.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uk.ash.womensfootball.R;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder> {

    //member vars for context adapter is working in
    private Context context;

    //league data to be displayed
    private List<EventData> data;

    public EventRecyclerViewAdapter(Context c, List<EventData> d){
        context = c;
        data = d;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View dataItemView = LayoutInflater.from(context).inflate(R.layout.event_list_view_item, parent, false);
        EventViewHolder viewHolder = new EventViewHolder(dataItemView, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int pos) {
        //get data of table at position
        EventData eventsItem = data.get(pos);
        //update the Views
        if (eventsItem.away){
            //((ImageView)holder.dataItemView.findViewById(R.id.iv_eventIconA)).setForeground(eventsItem.eventDrawable); TODO gete
            ((TextView)holder.dataItemView.findViewById(R.id.tv_descriptionA)).setText(String.valueOf(eventsItem.description));
            ((TextView)holder.dataItemView.findViewById(R.id.tv_eventTimeA)).setText(String.valueOf(eventsItem.time));
            //set home texts to null and Icon to transparent so recycler doesn't show repeated values
            ((ImageView)holder.dataItemView.findViewById(R.id.iv_eventIconH)).setForeground(ContextCompat.getDrawable(context, android.R.color.transparent)); // workaround but seems to be best solution
            ((TextView)holder.dataItemView.findViewById(R.id.tv_descriptionH)).setText(null);
            ((TextView)holder.dataItemView.findViewById(R.id.tv_eventTimeH)).setText(null);
        }
        else{
            //((ImageView)holder.dataItemView.findViewById(R.id.iv_eventIconH)).setForeground(eventsItem.eventDrawable); TODO gete
            ((TextView)holder.dataItemView.findViewById(R.id.tv_descriptionH)).setText(String.valueOf(eventsItem.description));
            ((TextView)holder.dataItemView.findViewById(R.id.tv_eventTimeH)).setText(String.valueOf(eventsItem.time));
            //set away texts to null so recycler doesn't show repeated values
            ((ImageView)holder.dataItemView.findViewById(R.id.iv_eventIconA)).setForeground(ContextCompat.getDrawable(context, android.R.color.transparent)); // workaround but seems to be best solution
            ((TextView)holder.dataItemView.findViewById(R.id.tv_descriptionA)).setText(null);
            ((TextView)holder.dataItemView.findViewById(R.id.tv_eventTimeA)).setText(null);
        }

    }

    @Override
    public int getItemCount() {
        if(data == null)
            return 0;
        else
            return data.size();
    }

    //sits between adapter and the View that's displaying the item, captures clicks
    class EventViewHolder extends RecyclerView.ViewHolder {
        private View dataItemView;
        private EventRecyclerViewAdapter adapter;

        public EventViewHolder(View v, EventRecyclerViewAdapter a) {
            super(v);
            dataItemView = v;
            adapter = a;
        }
    }
}


package de.dmate.marvin.dmate.room;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.util.Helper;

//This RecyclerViewAdapter is my own implementation of RecyclerView.Adapter
//It manages the RecyclerView in MainActivity and feeds data to it.
//Also there is a ViewHolder implemented which helps reducing the calls of findViewById to make scrolling smoother.
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{

    private List<EntryRoom> entries;
    private View.OnLongClickListener longClickListener;
    private OnItemClickedListener itemClickedListener;

    public RecyclerViewAdapter(List<EntryRoom> entries, View.OnLongClickListener longClickListener, RecyclerViewAdapter.OnItemClickedListener clickListener) {
        this.entries = entries;
        this.longClickListener = longClickListener;
        this.itemClickedListener = clickListener;
    }

    public interface OnItemClickedListener {
        void onItemClick(View v, int position);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        EntryRoom entryRoom = entries.get(position);

        holder.dateTextView.setText(Helper.formatMillisToTimeString(entryRoom.getDate().getTime()));

        if (entryRoom.getBloodsugar() != null) {
            holder.bloodsugarTextView.setText(entryRoom.getBloodsugar().toString());
        } else holder.bloodsugarTextView.setText(null);

        if (entryRoom.getBreadunit() != null) {
            holder.breadunitTextView.setText(entryRoom.getBreadunit().toString());
        } else holder.breadunitTextView.setText(null);

        if (entryRoom.getBolus() != null) {
            holder.bolusTextView.setText(entryRoom.getBolus().toString());
        } else holder.bolusTextView.setText(null);

        if (entryRoom.getBasal() != null) {
            holder.basalTextView.setText(entryRoom.getBasal().toString());
        } else holder.basalTextView.setText(null);

        holder.itemView.setTag(entryRoom);

        //set click listeners = MainActivity which implements both of the interfaces to receive callbacks when an item is clicked
        holder.itemView.setOnLongClickListener(longClickListener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void addItems(List<EntryRoom> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public EntryRoom getItemByPosition(int position) {
        return entries.get(position);
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView;
        TextView bloodsugarTextView;
        TextView breadunitTextView;
        TextView bolusTextView;
        TextView basalTextView;

        TextView staticdateTextView;
        TextView staticbloodsugarTextView;
        TextView staticbreadunitTextView;
        TextView staticbolusTextView;
        TextView staticbasalTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            dateTextView = (TextView) itemView.findViewById(R.id.entry_date);
            bloodsugarTextView = (TextView) itemView.findViewById(R.id.entry_bloodsugar);
            breadunitTextView = (TextView) itemView.findViewById(R.id.entry_breadunit);
            bolusTextView = (TextView) itemView.findViewById(R.id.entry_bolus);
            basalTextView = (TextView) itemView.findViewById(R.id.entry_basal);

            staticdateTextView = (TextView) itemView.findViewById(R.id.static_date);
            staticbloodsugarTextView = (TextView) itemView.findViewById(R.id.static_bloodsugar);
            staticbreadunitTextView = (TextView) itemView.findViewById(R.id.static_breadunit);
            staticbolusTextView = (TextView) itemView.findViewById(R.id.static_bolus);
            staticbasalTextView = (TextView) itemView.findViewById(R.id.static_basal);
        }
    }
}

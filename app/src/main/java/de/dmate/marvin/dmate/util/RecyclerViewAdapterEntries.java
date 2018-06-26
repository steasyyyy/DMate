package de.dmate.marvin.dmate.util;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Entry;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

//This RecyclerViewAdapterEntries is my own implementation of RecyclerView.Adapter
//It manages the RecyclerView in MainActivity and feeds data to it.
//Also there is a ViewHolder implemented which helps reducing the calls of findViewById to make scrolling smoother.
public class RecyclerViewAdapterEntries extends RecyclerView.Adapter<RecyclerViewAdapterEntries.RecyclerViewHolder>{

    public List<Entry> entries;

    DataViewModel viewModel;
    private User user;

    //both listeners are an instance of MainActivtiy which implements the interfaces that are defined here
    private OnItemClickedListener itemClickedListener;
    private OnContextMenuCreatedListener contextMenuListener;

    public RecyclerViewAdapterEntries(List<Entry> entries, RecyclerViewAdapterEntries.OnItemClickedListener itemClickListener, RecyclerViewAdapterEntries.OnContextMenuCreatedListener contextMenuListener) {
        this.entries = entries;
        this.contextMenuListener = contextMenuListener;
        this.itemClickedListener = itemClickListener;
    }

    //Custom interface to delegate listener actions to MainActivity
    public interface OnItemClickedListener {
        void onItemClick(View v, int position);
    }

    //Custom interface to delegate listener actions to MainActivity
    public interface OnContextMenuCreatedListener {
        void onContextMenuCreated(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, final int position);
    }

    //check if entry is last of the day
    //if yes, we need to inflate the date seperator
    private boolean isLastEntryOfDay(Entry entry) {
        boolean isLast = true;

        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.setTimeInMillis(entry.getTimestamp().getTime());

        Calendar calendarTemp = Calendar.getInstance();

        for (Entry e : entries) {
            calendarTemp.setTimeInMillis(e.getTimestamp().getTime());
            if (!entry.geteId().equals(e.geteId())) {
                if (calendarCurrent.get(Calendar.YEAR) == calendarTemp.get(Calendar.YEAR)
                        && calendarCurrent.get(Calendar.MONTH) == calendarTemp.get(Calendar.MONTH)
                        && calendarCurrent.get(Calendar.DAY_OF_MONTH) == calendarTemp.get(Calendar.DAY_OF_MONTH)) {
                    if (entry.getTimestamp().before(e.getTimestamp())) {
                        isLast=false;
                    }
                }
            }
        }
        return isLast;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_entry, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        Entry entry = entries.get(position);

        holder.dateTextView.setText(Helper.formatMillisToTimeString(entry.getTimestamp().getTime()));

        if (entry.getBloodSugar() != null) {
            holder.bloodsugarTextView.setText(entry.getBloodSugar().toString());
        } else holder.bloodsugarTextView.setText(null);

        if (entry.getBreadUnit() != null) {
            holder.breadunitTextView.setText(entry.getBreadUnit().toString());
        } else holder.breadunitTextView.setText(null);

        if (entry.getBolus() != null) {
            holder.bolusTextView.setText(entry.getBolus().toString());
        } else holder.bolusTextView.setText(null);

        if (entry.getBasal() != null) {
            holder.basalTextView.setText(entry.getBasal().toString());
        } else holder.basalTextView.setText(null);

        if (entry.getDiseased()) {
            holder.itemView.setBackgroundResource(R.color.colorEntryBackgroundDiseased);
            holder.itemView.findViewById(R.id.constraintLayout_date_separator).setBackgroundColor(Color.WHITE);
        } else holder.itemView.setBackgroundColor(Color.WHITE);

        if (isLastEntryOfDay(entry)) {
            holder.constraintLayout.setVisibility(ConstraintLayout.VISIBLE);
            holder.dateSeperatorTextView.setText(Helper.formatMillisToDateString(entry.getTimestamp().getTime()));
        } else {
            holder.constraintLayout.setVisibility(ConstraintLayout.GONE);
            holder.dateSeperatorTextView.setText(null);
        }

        holder.itemView.setTag(entry);

        //set ClickListener so that it redirects to MainActivity, where the action is defined
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedListener.onItemClick(v, position);
            }
        });

        //set ContextMenuListener so that it redirects to MainActivity, where the action is defined
        holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                final int positionF = position;
                contextMenuListener.onContextMenuCreated(menu, v, menuInfo, positionF);
            }
        });


    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void updateItems(List<Entry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public Entry getItemByPosition(int position) {
        return entries.get(position);
    }

    //custom ViewHolder to minimize calls of findViewById and increase scrolling performance
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

        ConstraintLayout constraintLayout;
        TextView dateSeperatorTextView;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            dateTextView = itemView.findViewById(R.id.entry_date);
            bloodsugarTextView = itemView.findViewById(R.id.entry_bloodsugar);
            breadunitTextView = itemView.findViewById(R.id.entry_breadunit);
            bolusTextView = itemView.findViewById(R.id.entry_bolus);
            basalTextView = itemView.findViewById(R.id.entry_basal);

            staticdateTextView = itemView.findViewById(R.id.static_date);
            staticbloodsugarTextView = itemView.findViewById(R.id.static_bloodsugar);
            staticbreadunitTextView = itemView.findViewById(R.id.static_breadunit);
            staticbolusTextView = itemView.findViewById(R.id.static_bolus);
            staticbasalTextView = itemView.findViewById(R.id.static_basal);

            constraintLayout = itemView.findViewById(R.id.constraintLayout_date_separator);
            dateSeperatorTextView = constraintLayout.findViewById(R.id.textView_date_separator);
        }
    }
}

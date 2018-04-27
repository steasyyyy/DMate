package de.dmate.marvin.dmate.roomDatabase;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.util.Helper;

//This RecyclerViewAdapter is my own implementation of RecyclerView.Adapter
//It manages the RecyclerView in MainActivity and feeds data to it.
//Also there is a ViewHolder implemented which helps reducing the calls of findViewById to make scrolling smoother.
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{

    public List<Entry> entries;
    //both listeners are an instance of MainActivtiy which implements the needed interfaces
    private OnItemClickedListener itemClickedListener;
    private OnContextMenuCreatedListener contextMenuListener;

    public RecyclerViewAdapter(List<Entry> entries, RecyclerViewAdapter.OnItemClickedListener itemClickListener, RecyclerViewAdapter.OnContextMenuCreatedListener contextMenuListener) {
        this.entries = entries;
        this.contextMenuListener = contextMenuListener;
        this.itemClickedListener = itemClickListener;
    }

    public interface OnItemClickedListener {
        void onItemClick(View v, int position);
    }

    public interface OnContextMenuCreatedListener {
        void onContextMenuCreated(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo, final int position);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        Entry entry = entries.get(position);

        holder.dateTextView.setText(Helper.formatMillisToTimeString(entry.getDate().getTime()));

        if (entry.getBloodsugar() != null) {
            holder.bloodsugarTextView.setText(entry.getBloodsugar().toString());
        } else holder.bloodsugarTextView.setText(null);

        if (entry.getBreadunit() != null) {
            holder.breadunitTextView.setText(entry.getBreadunit().toString());
        } else holder.breadunitTextView.setText(null);

        if (entry.getBolus() != null) {
            holder.bolusTextView.setText(entry.getBolus().toString());
        } else holder.bolusTextView.setText(null);

        if (entry.getBasal() != null) {
            holder.basalTextView.setText(entry.getBasal().toString());
        } else holder.basalTextView.setText(null);

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

    public void addItems(List<Entry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public Entry getItemByPosition(int position) {
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

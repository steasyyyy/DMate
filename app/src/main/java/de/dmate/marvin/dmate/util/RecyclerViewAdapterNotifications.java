package de.dmate.marvin.dmate.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.dmate.marvin.dmate.R;
import de.dmate.marvin.dmate.roomDatabase.DataViewModel;
import de.dmate.marvin.dmate.roomDatabase.Entities.Notification;
import de.dmate.marvin.dmate.roomDatabase.Entities.User;

public class RecyclerViewAdapterNotifications extends RecyclerView.Adapter<RecyclerViewAdapterNotifications.RecyclerViewHolder> {

    public List<Notification> notifications;
    private DataViewModel viewModel;
    private User user;

    private RecyclerViewAdapterNotifications.OnItemClickedListener itemClickedListener;
    private RecyclerViewAdapterNotifications.OnContextMenuCreatedListener contextMenuListener;

    public RecyclerViewAdapterNotifications(List<Notification> notifications, RecyclerViewAdapterNotifications.OnItemClickedListener itemClickListener, RecyclerViewAdapterNotifications.OnContextMenuCreatedListener contextMenuListener) {
        this.notifications = notifications;
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

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int position) {
        Notification notification = notifications.get(position);

        holder.textViewTimestamp.setText(notification.getTimestamp().toString());
        holder.textViewMessage.setText(notification.getMessage());

        holder.itemView.setTag(notification);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickedListener.onItemClick(v, position);
            }
        });

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
        return notifications.size();
    }

    public void updateItems(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public Notification getItemByPosition(int position) {
        return notifications.get(position);
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTimestamp;
        TextView textViewMessage;

        public RecyclerViewHolder(View itemView) {
            super(itemView);

            textViewTimestamp = itemView.findViewById(R.id.textView_notification_timestamp);
            textViewMessage = itemView.findViewById(R.id.textView_notification_message);
        }
    }

}



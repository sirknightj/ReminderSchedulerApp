package com.example.reminderscheduleapp;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

class eventAdapter extends RecyclerView.Adapter<eventAdapter.ViewHolder> {

    private ArrayList<event> events;
    private OnEventListener mOnEventListener;

    public eventAdapter(ArrayList<event> events, OnEventListener onEventListener) {
        this.events = events;
        this.mOnEventListener = onEventListener;
    }

    @NonNull
    @Override
    public eventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_row, parent,false);
        return new ViewHolder(view, mOnEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull eventAdapter.ViewHolder viewHolder, int position) {
        viewHolder.eventTitle.setText(events.get(position).getTitle());
        viewHolder.eventDescription.setText(events.get(position).getDescription().length() == 0 ? "No Description" : events.get(position).getDescription());
        viewHolder.eventDateTime.setText(events.get(position).getDate() + " " + events.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView eventTitle, eventDescription, eventDateTime;
        private OnEventListener onEventListener;

        public ViewHolder(@NonNull View itemView, OnEventListener onEventListener) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.event_description);
            eventDateTime = itemView.findViewById(R.id.event_date);
            itemView.setOnClickListener(this);
            this.onEventListener = onEventListener;
        }

        @Override
        public void onClick(View v) {
            onEventListener.onEventClick(getAdapterPosition());
        }
    }

    public interface OnEventListener {
        void onEventClick(int Position);
    }
}

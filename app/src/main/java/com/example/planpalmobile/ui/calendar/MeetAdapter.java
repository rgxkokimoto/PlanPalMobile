package com.example.planpalmobile.ui.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planpalmobile.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

public class MeetAdapter extends RecyclerView.Adapter<MeetAdapter.MeetViewHolder> {

    private final List<Map.Entry<Date, String>> meetList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());

    public MeetAdapter(List<Map.Entry<Date, String>> meetList) {
        this.meetList = meetList;
    }

    @NonNull
    @Override
    public MeetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meet, parent, false);
        return new MeetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetViewHolder holder, int position) {
        Map.Entry<Date, String> entry = meetList.get(position);
        Date fecha = entry.getKey();
        String usuario = entry.getValue();

        holder.tvMeetDate.setText(dateFormat.format(fecha));
        holder.tvMeetUser.setText(usuario);
    }

    @Override
    public int getItemCount() {
        return meetList != null ? meetList.size() : 0;
    }

    public static class MeetViewHolder extends RecyclerView.ViewHolder {
        TextView tvMeetDate, tvMeetUser;

        public MeetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMeetDate = itemView.findViewById(R.id.tvMeetDate);
            tvMeetUser = itemView.findViewById(R.id.tvMeetUser);
        }
    }
}

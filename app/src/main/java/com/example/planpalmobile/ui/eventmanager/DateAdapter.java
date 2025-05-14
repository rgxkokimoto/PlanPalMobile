package com.example.planpalmobile.ui.eventmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planpalmobile.R;
import com.example.planpalmobile.databinding.FragmentDateItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder> {

    private List<Date> dateList;
    private final OnDateActionListener listener;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd 'de' MMMM ', ' HH:mm", Locale.getDefault());

    public interface OnDateActionListener {
        void onDelete(Date date, int position);
    }

    public DateAdapter(List<Date> dates, OnDateActionListener listener) {
        this.dateList = dates != null ? dates : new ArrayList<>();
        this.listener = listener;
    }

    public void updateList(List<Date> newList) {
        this.dateList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentDateItemBinding binding = FragmentDateItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date date = dateList.get(position);
        String dateFormat = SDF.format(date);
        holder.binding.tvDatereserve.setText(dateFormat);

        holder.binding.btnDeleteDate.setOnClickListener(v -> listener.onDelete(date, position));
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final FragmentDateItemBinding binding;

        public ViewHolder(@NonNull FragmentDateItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}


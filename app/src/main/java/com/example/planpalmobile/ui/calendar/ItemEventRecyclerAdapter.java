package com.example.planpalmobile.ui.calendar;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planpalmobile.data.dto.EventoDTOItem;
import com.example.planpalmobile.databinding.FragmentEventItemBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemEventRecyclerAdapter
        extends RecyclerView.Adapter<ItemEventRecyclerAdapter.ViewHolder> {

    private List<EventoDTOItem> listaEventItem;

    public ItemEventRecyclerAdapter(List<EventoDTOItem> listaEventItem) {
        this.listaEventItem = listaEventItem;
    }

    public void updateList(List<EventoDTOItem> nueva) {
        this.listaEventItem = nueva;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentEventItemBinding binding = FragmentEventItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventoDTOItem event = listaEventItem.get(position);

        holder.binding.tvNameEvent.setText(event.getCodigo());

        Date hiContext = event.getHoraInicio();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String dateFormat = sdf.format(hiContext);
        holder.binding.tvDateEvent.setText(dateFormat);
    }

    @Override
    public int getItemCount() {
        return listaEventItem.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final FragmentEventItemBinding binding;

        public ViewHolder(@NonNull FragmentEventItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }


}

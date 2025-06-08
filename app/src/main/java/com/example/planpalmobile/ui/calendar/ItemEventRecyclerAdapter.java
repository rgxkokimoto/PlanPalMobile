package com.example.planpalmobile.ui.calendar;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planpalmobile.R;
import com.example.planpalmobile.data.dto.EventoDTOItem;
import com.example.planpalmobile.databinding.FragmentEventItemBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemEventRecyclerAdapter
        extends RecyclerView.Adapter<ItemEventRecyclerAdapter.ViewHolder> {

    private List<EventoDTOItem> listaEventItem;

    public ItemEventRecyclerAdapter(List<EventoDTOItem> listaEventItem) {
        this.listaEventItem = listaEventItem;
    }

    public void updateList(List<EventoDTOItem> newListEventItem) {
        this.listaEventItem = newListEventItem;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FragmentEventItemBinding binding = FragmentEventItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (listaEventItem.size() < 0) {

        } else {
            EventoDTOItem event = listaEventItem.get(position);

            holder.binding.tvNameEvent.setText(event.getCodigo());

            Date hiContext = event.getHoraInicio();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateFormat = sdf.format(hiContext);
            holder.binding.tvDateEvent.setText(dateFormat);

            holder.binding.tvEstateEvent.setText(event.getEtiqueta());
            Log.d("ItemEventRecyclerAdapter", "Etiqueta: " + event.getEtiqueta());
            if (event.getEtiqueta() != null) {
                String etiquetaSelected = event.getEtiqueta();
                setCardColor(holder, etiquetaSelected);
            }


            holder.itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putString("codigo_evento", event.getId());

                Navigation.findNavController(v).navigate(
                        R.id.action_navigation_calendar_to_pickMeetEventDetailFragment,
                        bundle
                );
            });

        }
    }

    // "profesional", "ocio", "personal", "académico", "otro"
    private void setCardColor(ViewHolder holder, String etiquetaSelected) {
        int color;

        switch (etiquetaSelected) {
            case "profesional":
                color = holder.itemView.getResources().getColor(R.color.mainBlue);
                break;
            case "ocio":
                color = holder.itemView.getResources().getColor(R.color.teal_200);
                break;
            case "personal":
                color = holder.itemView.getResources().getColor(R.color.purple_200);
                break;
            case "academico":
                color = holder.itemView.getResources().getColor(R.color.bar_color);
                break;

            default:
                Log.e("ItemEventRecyclerAdapter", "Etiqueta no reconocida: " + etiquetaSelected);
                color = holder.itemView.getResources().getColor(R.color.gray_list);
                break;
        }

        holder.binding.cardEtc.setCardBackgroundColor(color);
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

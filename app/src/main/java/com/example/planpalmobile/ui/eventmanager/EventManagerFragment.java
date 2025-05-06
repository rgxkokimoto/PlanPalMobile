package com.example.planpalmobile.ui.eventmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.planpalmobile.databinding.FragmentEventManagerBinding;

public class EventManagerFragment extends Fragment {

    private FragmentEventManagerBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventManagerViewModel dashboardViewModel =
                new ViewModelProvider(this).get(EventManagerViewModel.class);

        binding = FragmentEventManagerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
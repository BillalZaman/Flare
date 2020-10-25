package com.infotech4it.flare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentStatusViewerBinding;


public class StatusViewerFragment extends Fragment {
    private FragmentStatusViewerBinding binding;

    public StatusViewerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_status_viewer, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {

    }
}
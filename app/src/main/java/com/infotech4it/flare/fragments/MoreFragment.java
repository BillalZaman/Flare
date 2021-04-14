package com.infotech4it.flare.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.infotech4it.flare.R;
import com.infotech4it.flare.databinding.FragmentSettingBinding;
import com.infotech4it.flare.helpers.UIHelper;
import com.infotech4it.flare.interfaces.MoreInterface;
import com.infotech4it.flare.views.adapters.MoreAdapter;
import com.infotech4it.flare.views.models.MoreModel;

import java.util.ArrayList;

public class MoreFragment extends Fragment{
    private FragmentSettingBinding binding;
    private ArrayList<MoreModel> data;
    private MoreAdapter adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;

    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        setRecyclerview();
    }

    private void setRecyclerview() {
        adapter = new MoreAdapter(getContext());
        data = new ArrayList<>();
        data.add(new MoreModel("Friends", R.drawable.change_password_icon));
        data.add(new MoreModel("Friends Request", R.drawable.change_password_icon));
        data.add(new MoreModel("Change Password", R.drawable.change_password_icon));
        data.add(new MoreModel("FeedBack", R.drawable.ic_feedback_icon));
        data.add(new MoreModel("Logout", R.drawable.ic_logout));
        data.add(new MoreModel("Share our app", R.drawable.ic_baseline_share_24));
        data.add(new MoreModel("Privacy Policies", R.drawable.ic_privacypolicy));
        adapter.setData(data);
        binding.recyclerview.setAdapter(adapter);
    }
}
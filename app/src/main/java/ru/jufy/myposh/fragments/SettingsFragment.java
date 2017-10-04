package ru.jufy.myposh.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.adapters.SettingsAdapter;
import ru.jufy.myposh.R;


public class SettingsFragment extends Fragment {


    View rootView;
    RecyclerView recyclerView;
    SettingsAdapter adapter;

    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_settings, container, false);

        recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SettingsAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        return rootView;
    }
}

package ru.jufy.myposh.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.jufy.myposh.R;
import ru.jufy.myposh.ui.activities.LaunchActivity;

/**
 * Created by BorisDev on 03.09.2017.
 */

public class LoginTypesFragment extends Fragment {
    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login_types, container, false);


        rootView.findViewById(R.id.email_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LaunchActivity)getActivity()).showEmailLogin();
            }
        });

        return rootView;
    }
}

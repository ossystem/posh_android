package ru.jufy.myposh.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.jufy.myposh.R;
import ru.jufy.myposh.activities.LoginActivity;

/**
 * Created by BorisDev on 03.09.2017.
 */

public class LoginTypesFragment extends Fragment {
    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_login_types, container, false);

        rootView.findViewById(R.id.vk_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).authorizeInstagram();
            }
        });

        rootView.findViewById(R.id.imageViewVk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).authorizeInstagram();
            }
        });

        rootView.findViewById(R.id.fb_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).authorizeFB();
            }
        });

        rootView.findViewById(R.id.imageViewFb).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).authorizeFB();
            }
        });

        rootView.findViewById(R.id.email_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).showEmailLogin();
            }
        });

        return rootView;
    }
}

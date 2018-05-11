package ru.jufy.myposh.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.activities.LoginActivity;
import ru.jufy.myposh.utils.HttpPostAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;

/**
 * Created by BorisDev on 04.09.2017.
 */

public class EmailLoginFragment extends Fragment {
    protected View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_email_login, container, false);

        rootView.findViewById(R.id.buttonBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).showLoginTypes();
            }
        });

        rootView.findViewById(R.id.buttonResetPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText)rootView.findViewById(R.id.emailInput)).getText().toString();
                resetPassword(email);
            }
        });

        rootView.findViewById(R.id.buttonForward).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText)rootView.findViewById(R.id.emailInput)).getText().toString();
                String password = ((EditText)rootView.findViewById(R.id.passwordInput)).getText().toString();
                authorizeEmail(email, password);
            }
        });

        rootView.findViewById(R.id.imageViewFbRec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).authorizeFB();
            }
        });

        rootView.findViewById(R.id.imageViewVkRec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((LoginActivity)getActivity()).authorizeVK();
            }
        });

        return rootView;
    }

    private void resetPassword(String email) {
        String authReq[] = new String[2];
        authReq[0] = MyPoshApplication.DOMAIN + "reset-password";
        authReq[1] = JsonHelper.convertEmail(email);
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Content-Type", "application/json");
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        postRequest.setRequestProperties(reqProps);
        try {
            String postResult = postRequest.execute(authReq).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            onPasswordResetResult(postResult);
        } catch (InterruptedException | ExecutionException e) {
            showUnknownError();
            e.printStackTrace();
        }
    }

    private void onPasswordResetResult(String postResult) {
        String message = JsonHelper.getMessage(postResult);
        if (message.contains("We sent a new password to your email")) {
            showPasswordResetSuccess();
        } else if (message.contains("User doesn't exists")) {
            showNoUserExist();
        } else if (message.contains("Validation error")) {
            showEmailFormatIncorrect();
        } else {
            showMessage(message);
        }
    }

    private void showEmailFormatIncorrect() {
        showMessage(getString(R.string.email_format_incorrect));
    }

    private void showNoUserExist() {
        showMessage(getString(R.string.no_user_exist));
    }

    private void showPasswordResetSuccess() {
        showMessage(getString(R.string.password_reset_success));
    }

    private void authorizeEmail(String email, String password) {
        String authReq[] = new String[2];
        authReq[0] = MyPoshApplication.DOMAIN + "auth";
        authReq[1] = JsonHelper.convertEmailPassword(email, password);
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Content-Type", "application/json");
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        postRequest.setRequestProperties(reqProps);
        try {
            String postResult = postRequest.execute(authReq).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            onAuthResult(postResult);
        } catch (InterruptedException | ExecutionException e) {
            showUnknownError();
            e.printStackTrace();
        }
    }

    private void showUnknownError() {
        showMessage(getString(R.string.smth_went_wrong));
    }

    private void onAuthResult(String postResult) {
        String message = JsonHelper.getMessage(postResult);
        if (message.contains("Login successful")) {
            MyPoshApplication.onNewTokenObtained(JsonHelper.getToken(postResult));
            ((LoginActivity)getActivity()).startMainActivity();
        } else if (message.contains("Your email was entered incorrectly, or the user is not registered")) {
            startRegistration();
        } else if (message.contains("The password is incorrect")) {
            showPasswordIncorrect();
        } else {
            showMessage(message);
        }
    }

    private void startRegistration() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.registration));
        alertDialog.setMessage(getString(R.string.email_not_found));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        registerNewAccount();
                    }
                });
        alertDialog.show();
    }

    private void registerNewAccount() {
        String email = ((EditText)rootView.findViewById(R.id.emailInput)).getText().toString();
        String password = ((EditText)rootView.findViewById(R.id.passwordInput)).getText().toString();
        String authReq[] = new String[2];
        authReq[0] = MyPoshApplication.DOMAIN + "registration";
        authReq[1] = JsonHelper.convertEmailPassword(email, password);
        HashMap<String, String> reqProps = new HashMap<>();
        reqProps.put("Content-Type", "application/json");
        HttpPostAsyncTask postRequest = new HttpPostAsyncTask();
        postRequest.setRequestProperties(reqProps);
        try {
            String postResult = postRequest.execute(authReq).get();
            if (null == postResult) {
                throw new InterruptedException();
            }
            onRegistrationResult(postResult);
        } catch (InterruptedException | ExecutionException e) {
            showUnknownError();
            e.printStackTrace();
        }
    }

    private void onRegistrationResult(String postResult) {
        String message = JsonHelper.getMessage(postResult);
        if (message.contains("Thank you for registering! Please check your mail")) {
            MyPoshApplication.onNewTokenObtained(JsonHelper.getToken(postResult));
            ((LoginActivity)getActivity()).startMainActivity();
        } else {
            showMessage(message);
        }
    }

    private void showPasswordIncorrect() {
        showMessage(getString(R.string.incorrect_password));
    }

    private void showMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.error));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}

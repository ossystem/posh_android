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
import ru.jufy.myposh.activities.MainActivity;
import ru.jufy.myposh.utils.HttpPostAsyncTask;
import ru.jufy.myposh.utils.JsonHelper;

import static ru.jufy.myposh.MyPoshApplication.onNewTokenObtained;

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

    private void authorizeEmail(String email, String password) {
        String authReq[] = new String[2];
        authReq[0] = "http://kulon.jwma.ru/api/v1/auth";
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
        showMessage("Что-то пошло не так...");
    }

    private void onAuthResult(String postResult) {
        String message = JsonHelper.getMessage(postResult);
        if (message.contains("Login successful")) {
            MyPoshApplication.onNewTokenObtained(JsonHelper.getToken(postResult));
            ((LoginActivity)getActivity()).startMainActivity();
        } else if (message.contains("Your email was entered incorrectly, or the user is not registered")) {
            showNotRegistered();
        } else if (message.contains("The password is incorrect")) {
            showPasswordIncorrect();
        } else {
            showMessage(message);
        }
    }

    private void showNotRegistered() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Регистрация");
        alertDialog.setMessage("Аккаунта с такой почтой не существует. Создать новый?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
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
        authReq[0] = "http://kulon.jwma.ru/api/v1/registration";
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
        showMessage("Введён неправильный пароль");
    }

    private void showMessage(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Ошибка!");
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}

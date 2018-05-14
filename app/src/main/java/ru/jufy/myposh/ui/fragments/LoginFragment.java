package ru.jufy.myposh.ui.fragments;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jufy.mgtshr.ui.base.BaseFragment;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.ui.activities.LoginActivity;
import ru.jufy.myposh.ui.utils.HttpPostAsyncTask;
import ru.jufy.myposh.ui.utils.JsonHelper;

/**
 * Created by BorisDev on 04.09.2017.
 */

public class LoginFragment extends BaseFragment {

    public static LoginFragment newInstance() {
        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        applyBlurOnBackground(rootView);
/*        final EditText editText = (EditText) rootView.findViewById(R.id.phoneInput);
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());*/
     /*   rootView.findViewById(R.id.buttonForward).setOnClickListener(new View.OnClickListener() {
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
                ((LoginActivity)getActivity()).authorizeInstagram();
            }
        });*/

        return rootView;
    }

    private void applyBlurOnBackground(View rootView) {
        BlurView blurView = rootView.findViewById(R.id.blurView);

        float radius = 5;

        View decorView = getActivity().getWindow().getDecorView();
        //Activity's root View. Can also be root View of your layout (preferably)
        ViewGroup layout = rootView.findViewById(R.id.blurContainer);
        //set background, if your root layout doesn't have one
        Drawable windowBackground = decorView.getBackground();

        blurView.setupWith(layout)
                .windowBackground(windowBackground)
                .blurAlgorithm(new RenderScriptBlur(getContext()))
                .blurRadius(radius)
                .setHasFixedTransformationMatrix(true);
    }

    private void resetPassword(String email) {
        String authReq[] = new String[2];
        authReq[0] = MyPoshApplication.Companion.getDOMAIN() + "reset-password";
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
        authReq[0] = MyPoshApplication.Companion.getDOMAIN() + "auth";
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
            MyPoshApplication.Companion.onNewTokenObtained(JsonHelper.getToken(postResult));
            ((LoginActivity) getActivity()).startMainActivity();
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
   /*     String email = ((EditText)rootView.findViewById(R.id.emailInput)).getText().toString();
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
        }*/
    }

    private void onRegistrationResult(String postResult) {
        String message = JsonHelper.getMessage(postResult);
        if (message.contains("Thank you for registering! Please check your mail")) {
            MyPoshApplication.Companion.onNewTokenObtained(JsonHelper.getToken(postResult));
            ((LoginActivity) getActivity()).startMainActivity();
        } else {
            showMessage(message);
        }
    }

    private void showPasswordIncorrect() {
        showMessage(getString(R.string.incorrect_password));
    }


    /*private void showMessage(String message) {
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
    }*/

    @Override
    protected void setUp(@Nullable View view) {

    }
}

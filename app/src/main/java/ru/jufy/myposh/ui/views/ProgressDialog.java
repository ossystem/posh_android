package ru.jufy.myposh.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import ru.jufy.myposh.R;

/**
 * Created by rolea on 10.10.2017.
 */

public class ProgressDialog extends Dialog {

    String progressMessage;
    String errorMessage;
    private String successMessage;
    private ProgressBar progressBar;
    private TextView messageView;


    public ProgressDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public ProgressDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }

    protected ProgressDialog(@NonNull Context context, boolean cancelable,
                             @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public ProgressDialog(@NonNull Context context, @StyleRes int themeResId, String successMesage, String errorMessage) {
        super(context, themeResId);
        this.successMessage = successMesage;
        this.errorMessage = errorMessage;
        init();
    }

    public String getProgressMessage() {
        return progressMessage;
    }

    public void setProgressMessage(String progressMessage) {
        this.progressMessage = progressMessage;
    }

    private void init() {
        setContentView(R.layout.dialog_progress);
        this.progressMessage = getContext().getString(R.string.progress_message);
        progressBar = findViewById(R.id.progressBar);
        messageView = findViewById(R.id.message_view);

        messageView.setText(progressMessage);
    }

    public void errorLoading(String errorMessage){
        progressBar.setVisibility(View.GONE);
        messageView.setText(errorMessage);
    }

    public void errorLoading() {
        errorLoading(errorMessage);
    }

    public void successLoaded() {
        progressBar.setVisibility(View.GONE);
        messageView.setText(successMessage);
    }

}

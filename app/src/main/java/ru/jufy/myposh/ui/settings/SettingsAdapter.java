package ru.jufy.myposh.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.jufy.myposh.BuildConfig;
import ru.jufy.myposh.MyPoshApplication;
import ru.jufy.myposh.R;
import ru.jufy.myposh.ui.activities.SettingsResultActivity;
import ru.jufy.myposh.ui.global.WebViewActivity;

/**
 * Created by Anna on 4/11/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    Context mainActivity;
    private SettingsListener listener;
    List<SettingsItem> settingsItems;

    public SettingsAdapter(Context activity, SettingsListener listener) {
        mainActivity = activity;
        this.listener = listener;
        settingsItems = new ArrayList<>();
        settingsItems.add(new SettingsItem(R.drawable.settings_contacts,
                R.string.settings_contacts_label,
                R.string.settings_contacts_comment,
                v -> openLinkInBrowser(MyPoshApplication.Companion.getDOMAIN() + "contacts")));
        settingsItems.add(new SettingsItem(R.drawable.settings_qa,
                R.string.settings_qa_label,
                R.string.settings_qa_comment));
        settingsItems.add(new SettingsItem(R.drawable.settings_address,
                R.string.settings_address_label,
                R.string.settings_address_comment,
                v -> openLinkInBrowser(MyPoshApplication.Companion.getDOMAIN() + "address")));

        if (BuildConfig.DEBUG) {
            settingsItems.add(new SettingsItem(R.drawable.settings_address,
                    R.string.debug_info_label,
                    R.string.debug_info_comment,
                    v -> {
                        Intent i = new Intent(v.getContext(), SettingsResultActivity.class);
                        v.getContext().startActivity(i);
                    }));
        }

        settingsItems.add(new SettingsItem(R.drawable.ic_share,
                R.string.share,
                R.string.share_comment, v-> { listener.shareClicked();}
                ));

        settingsItems.add(new SettingsItem(R.drawable.ic_logout,
                R.string.logout,
                v -> listener.logoutClicked()));
    }

    private void openLinkInBrowser(String url) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + MyPoshApplication.Companion.getCurrentToken().getToken());
        Intent i = new Intent(mainActivity, WebViewActivity.class);
        i.putExtra(WebViewActivity.Companion.getURL(), url);
        i.putExtra(WebViewActivity.Companion.getACTION(), WebViewActivity.Companion.getACTION_SHOW_WITH_HEADERS());
        i.putExtra(WebViewActivity.Companion.getHEADERS(), headers);
        mainActivity.startActivity(i);
    }

    @Override
    public SettingsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_item, parent, false);
        return  new SettingsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SettingsViewHolder holder, int position) {
        SettingsItem settingsItem = settingsItems.get(position);
        holder.icon.setImageResource(settingsItem.iconId);
        holder.label.setText(settingsItem.labelId);

        if (settingsItem.commentId  != -1) holder.comment.setText(settingsItem.commentId);

        holder.itemView.setOnClickListener(settingsItem.clickListener);
        if (position == settingsItems.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return settingsItems.size();
    }


    public class SettingsItem {
        int iconId;
        int labelId;
        int commentId;
        View.OnClickListener clickListener = null;


        public SettingsItem(int iconId, int labelId, View.OnClickListener listener) {
            this.iconId = iconId;
            this.labelId = labelId;
            commentId = -1;
            clickListener = listener;
        }

        public SettingsItem(int iconId, int labelId, int commentId) {
            this.iconId = iconId;
            this.labelId = labelId;
            this.commentId = commentId;
        }

        public SettingsItem(int iconId, int labelId, int commentId, View.OnClickListener listener) {
            this(iconId, labelId, commentId);
            clickListener = listener;
        }
    }


    public class SettingsViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView label;
        public TextView comment;
        public View divider;
        public View itemView;
        public SettingsViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.icon);
            label = (TextView)itemView.findViewById(R.id.label);
            comment = (TextView)itemView.findViewById(R.id.comment);
            divider = itemView.findViewById(R.id.divider);
            this.itemView = itemView;
        }
    }
}

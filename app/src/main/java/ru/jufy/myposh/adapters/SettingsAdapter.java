package ru.jufy.myposh.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.jufy.myposh.R;

/**
 * Created by Anna on 4/11/2017.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    List<SettingsItem> settingsItems;

    public SettingsAdapter() {
        settingsItems = new ArrayList<>();
        settingsItems.add(new SettingsItem(R.drawable.settings_unbind,
                R.string.settings_unbind_label,
                R.string.settings_unbind_comment));
        settingsItems.add(new SettingsItem(R.drawable.settings_contacts,
                R.string.settings_contacts_label,
                R.string.settings_contacts_comment));
        settingsItems.add(new SettingsItem(R.drawable.settings_qa,
                R.string.settings_qa_label,
                R.string.settings_qa_comment));
        settingsItems.add(new SettingsItem(R.drawable.settings_address,
                R.string.settings_address_label,
                R.string.settings_address_comment));
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
        holder.comment.setText(settingsItem.commentId);
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

        public SettingsItem(int iconId, int labelId, int commentId) {
            this.iconId = iconId;
            this.labelId = labelId;
            this.commentId = commentId;
        }
    }


    public class SettingsViewHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView label;
        public TextView comment;
        public View divider;
        public SettingsViewHolder(View itemView) {
            super(itemView);
            icon = (ImageView)itemView.findViewById(R.id.icon);
            label = (TextView)itemView.findViewById(R.id.label);
            comment = (TextView)itemView.findViewById(R.id.comment);
            divider = itemView.findViewById(R.id.divider);
        }
    }
}

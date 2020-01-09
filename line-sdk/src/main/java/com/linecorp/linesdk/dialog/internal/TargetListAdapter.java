package com.linecorp.linesdk.dialog.internal;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.linecorp.linesdk.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class TargetListAdapter extends RecyclerView.Adapter<TargetListAdapter.TargetViewHolder> {
    private List<TargetUser> originalTargetList;
    private List<TargetUser> targetList;
    private OnSelectedChangeListener externalListener;
    private String queryString = "";

    public class TargetViewHolder extends RecyclerView.ViewHolder {
        private ViewGroup viewContainer;
        private TextView textView;
        private CheckBox checkBox;
        private ImageView imageView;
        private int highlightTextColor;

        public TargetViewHolder(ViewGroup vg) {
            super(vg);
            viewContainer = vg;
            textView = vg.findViewById(R.id.textView);
            imageView = vg.findViewById(R.id.imageView);
            checkBox = vg.findViewById(R.id.checkBox);
            highlightTextColor = vg.getResources().getColor(R.color.text_highlight);
        }

        public void bind(TargetUser targetUser, OnSelectedChangeListener listener) {
            viewContainer.setSelected(targetUser.getSelected());
            checkBox.setChecked(targetUser.getSelected());
            textView.setText(createHighlightTextSpan(targetUser.getDisplayName(), queryString));

            viewContainer.setOnClickListener(view -> {
                boolean isChecked = !targetUser.getSelected();
                viewContainer.setSelected(isChecked);
                targetUser.setSelected(isChecked);
                checkBox.setChecked(isChecked);
                listener.onSelected(targetUser, isChecked);
            });

            int placeholderResId =
                    (targetUser.getType() == TargetUser.Type.FRIEND)? R.drawable.friend_thumbnail : R.drawable.group_thumbnail;

            Picasso.get().load(targetUser.getPictureUri())
                    .placeholder(placeholderResId)
                    .into(imageView);
        }

        private SpannableString createHighlightTextSpan(String text, String toBeHighLighted) {
            SpannableString span = new SpannableString(text);
            if (toBeHighLighted.isEmpty()) return span;

            int foundIndex = text.toLowerCase().indexOf(toBeHighLighted.toLowerCase());
            if (foundIndex != -1) {
                span.setSpan(new ForegroundColorSpan(highlightTextColor),
                        foundIndex, foundIndex + toBeHighLighted.length(), 0);
            }
            return span;
        }
    }

    public TargetListAdapter(List<TargetUser> targetList, OnSelectedChangeListener externalListener) {
        this.originalTargetList = targetList;
        this.targetList = new ArrayList<TargetUser>(){{ addAll(originalTargetList); }};
        this.externalListener = externalListener;
    }

    public int filter(String text) {
        queryString = text;
        targetList.clear();
        if(text.isEmpty()){
            targetList.addAll(originalTargetList);
        } else{
            text = text.toLowerCase();
            for(TargetUser targetUser: originalTargetList){
                if(targetUser.getDisplayName().toLowerCase().contains(text)) {
                    targetList.add(targetUser);
                }
            }
        }

        notifyDataSetChanged();
        return targetList.size();
    }

    @Override
    public TargetListAdapter.TargetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup viewGroup = (ViewGroup) LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.layout_target_item, parent, false);
        return new TargetViewHolder(viewGroup);
    }

    @Override
    public void onBindViewHolder(TargetViewHolder holder, int position) {
        holder.bind(targetList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return targetList.size();
    }

    public void unSelect(TargetUser user) {
        for (int index = 0; index < targetList.size(); index++) {
            TargetUser targetUser = targetList.get(index);
            if (targetUser.getId().equals(user.getId())) {
                targetUser.setSelected(false);
                notifyItemChanged(index);
                break;
            }
        }
    }

    public void addAll(List<TargetUser> newItems) {
        int position = targetList.size() - 1;
        originalTargetList.addAll(newItems);
        targetList.addAll(newItems);
        notifyItemRangeInserted(position, newItems.size());
    }

    public interface OnSelectedChangeListener {
        void onSelected(TargetUser targetUser, boolean isSelected);
    }

    private OnSelectedChangeListener listener = new OnSelectedChangeListener() {
        @Override
        public void onSelected(TargetUser targetUser, boolean isSelected) {
            externalListener.onSelected(targetUser, isSelected);
        }
    };
}

package com.linecorp.linesdk.dialog.internal;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.linecorp.linesdk.R;

import java.util.HashMap;

public class SendMessageTargetPagerAdapter extends PagerAdapter {
    private Context context;
    private SendMessagePresenter presenter;
    private TargetListAdapter.OnSelectedChangeListener listener;
    private HashMap<TargetUser.Type, TargetListWithSearchView> viewHashMap = new HashMap<>();

    public SendMessageTargetPagerAdapter(Context context,
                                         SendMessagePresenter presenter,
                                         TargetListAdapter.OnSelectedChangeListener listener) {
        this.context = context;
        this.presenter= presenter;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return TargetUser.getTargetTypeCount();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public View instantiateItem(@NonNull ViewGroup container, int position) {
        TargetUser.Type targetUserType = TargetUser.Type.values()[position];
        TargetListWithSearchView view;
        switch (targetUserType) {
            case FRIEND: {
                view = new TargetListWithSearchView(context, R.string.search_no_fiend, listener);
                presenter.getFriends(view::addTargetUsers);
                break;
            }
            case GROUP: {
                view = new TargetListWithSearchView(context, R.string.search_no_group, listener);
                presenter.getGroups(view::addTargetUsers);
                break;
            }
            default:
                return null;
        }
        viewHashMap.put(targetUserType, view);

        container.addView(view);
        return view;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(TargetUser.Type.values()[position]) {
            case FRIEND:
                return context.getString(R.string.select_tab_friends);
            case GROUP:
                return context.getString(R.string.select_tab_groups);
            default:
                return "";
        }
    }

    public void unSelect(TargetUser targetUser) {
        TargetUser.Type type = targetUser.getType();
        viewHashMap.get(type).unSelect(targetUser);
    }
}

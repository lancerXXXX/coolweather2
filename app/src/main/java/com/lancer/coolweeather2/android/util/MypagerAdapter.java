package com.lancer.coolweeather2.android.util;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MypagerAdapter extends PagerAdapter {

    private List<View> mListView;
    private View mCurrentView;
    int position;

    public MypagerAdapter(List<View> listView){
        mListView=listView;
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void notifyDataSetChanged() {
        Log.e("flag","notifyDataSetChanged");
        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mListView.size();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        Log.e("flag","destroy "+position);
        container.removeView((View)object);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View)object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View v=mListView.get(position);
        v.setTag(position);
        ViewGroup parent = (ViewGroup) v.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }
        Log.e("flag","instantiateItem "+position);
        container.addView(mListView.get(position),0);
        return mListView.get(position);
    }

    public View getPrimaryItem() {
        return mCurrentView;
    }
    @Override
    public int getItemPosition(@NonNull Object object) {
        View view=(View)object;
        if((int)view.getTag()==0)
            return POSITION_UNCHANGED;
        else
            return POSITION_NONE;
    }
}

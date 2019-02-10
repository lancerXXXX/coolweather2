package com.lancer.coolweeather2.android.util;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MypagerAdapter extends PagerAdapter {
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return false;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}

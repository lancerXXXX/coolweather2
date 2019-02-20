package com.lancer.coolweeather2.android.util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public interface RecyclerViewItemListener {
    public void onClick(View view,int position);
        public void onLongClick(View view,int position);
}
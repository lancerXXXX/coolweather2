package com.lancer.coolweeather2.android.util;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lancer.coolweeather2.android.R;

public class MyArea extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my_area,container,false);
        MyListView myListView=(MyListView)view.findViewById(R.id.my_list);
        return view;
    }
}

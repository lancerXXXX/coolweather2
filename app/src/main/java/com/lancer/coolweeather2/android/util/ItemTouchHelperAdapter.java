package com.lancer.coolweeather2.android.util;

public interface ItemTouchHelperAdapter {
    public void onItemMove(int fromPostion,int toPosition);
    public void onItemDelete(int position);
}

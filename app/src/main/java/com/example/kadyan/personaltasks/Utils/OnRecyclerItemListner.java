package com.example.kadyan.personaltasks.Utils;

import com.example.kadyan.personaltasks.Data.Todo;

public interface OnRecyclerItemListner {
    void onItemClick(Todo todo,int position);
    void onItemLongClick(Todo todo,int position);
    void onItemPriorityChanged(Todo todo,int position);
}

package com.example.kadyan.personaltasks.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kadyan.personaltasks.Data.Todo;
import com.example.kadyan.personaltasks.R;
import com.example.kadyan.personaltasks.Utils.OnRecyclerItemListner;

import java.util.ArrayList;


public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private Context context;
    private ArrayList<Todo> arrayList;
    private OnRecyclerItemListner recyclerItemListner;


    public TodoAdapter(Context context, ArrayList<Todo> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        /***Multi Select Functionality***/
//        this.selectedPendingTasks=selectedPendingTasks;
        recyclerItemListner= (OnRecyclerItemListner) context;
    }


    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.single_todo,parent,false);
        return new TodoViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final TodoViewHolder holder, final int position) {
        final Todo currentTodo=arrayList.get(position);
        holder.title.setText(currentTodo.getTitle());
        holder.description.setText(currentTodo.getDescription());
        holder.date.setText(currentTodo.getDueDate());
        setStarForPriority(holder.importantOrNot,currentTodo.getPriority());
        holder.importantOrNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"ON STAR CLICK",Toast.LENGTH_SHORT).show();
                currentTodo.setPriority(Math.abs(1-currentTodo.getPriority()));
                setStarForPriority(holder.importantOrNot,currentTodo.getPriority());
                recyclerItemListner.onItemPriorityChanged(currentTodo,holder.getAdapterPosition());
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerItemListner.onItemClick(currentTodo,holder.getAdapterPosition());//for updating the task send position later to access that item
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                recyclerItemListner.onItemLongClick(currentTodo,holder.getAdapterPosition());
                return true;
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    private void setStarForPriority(ImageButton imageButton, int priority){
        if (priority == 0){
            imageButton.setImageResource(R.drawable.ic_star_border_black_24dp);
        }else if (priority == 1){
            imageButton.setImageResource(R.drawable.ic_star_black_24dp);
        }else{
            Toast.makeText(context,"PRIORITY IS DIFFERENT",Toast.LENGTH_SHORT).show();
        }
    }


    /***Multi Select Functionality***/
//    public void refreshAdapter(ArrayList<Todo> arrayList, ArrayList<Todo> selectedPendingTasks) {
//        this.arrayList = arrayList;
//        this.selectedPendingTasks = selectedPendingTasks;
//        this.notifyDataSetChanged();
//    }


    class TodoViewHolder extends RecyclerView.ViewHolder{
        TextView title,description,date;
        ImageButton importantOrNot;
        public TodoViewHolder(View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.todo_title);
            description=itemView.findViewById(R.id.todo_description);
            date=itemView.findViewById(R.id.todo_date);
            importantOrNot=itemView.findViewById(R.id.important_or_not);
        }
    }

}

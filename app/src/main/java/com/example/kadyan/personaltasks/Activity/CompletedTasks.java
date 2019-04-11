package com.example.kadyan.personaltasks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.kadyan.personaltasks.Adapters.TodoAdapter;
import com.example.kadyan.personaltasks.Constants.DatabaseConstants;
import com.example.kadyan.personaltasks.Data.Todo;
import com.example.kadyan.personaltasks.Data.TodoDatabase;
import com.example.kadyan.personaltasks.R;
import com.example.kadyan.personaltasks.Utils.OnRecyclerItemListner;

import java.util.ArrayList;

public class CompletedTasks extends AppCompatActivity implements OnRecyclerItemListner{

    private final String TAG = this.getClass().getName();

    Toolbar toolbar;
    RecyclerView recyclerView;
    TodoAdapter todoAdapter;

    //should be synced with one other at all times as positions must be same
    ArrayList<Todo> completedTasks;
    TodoDatabase todoDatabase;

    private ArrayList<Todo> selectedPendingTasks = new ArrayList<>();
    boolean isMultiSelect=false;
    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        setSupportActionBar(toolbar);

        todoDatabase = TodoDatabase.getTodoDatabase(this);
        completedTasks = todoDatabase.getAllCompletedTasks();
        addItemToRecyclerView();
    }

    private void init() {
        setContentView(R.layout.activity_completed_tasks);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.completed_tasks_recyclerView);
    }


    private void addItemToRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        todoAdapter = new TodoAdapter(this, completedTasks);
        recyclerView.setAdapter(todoAdapter);
    }

    private void deleteTodo(Todo currentTodo, int position) {
        completedTasks.remove(position);
        todoAdapter.notifyItemRemoved(position);
        todoDatabase.deleteDbItem(currentTodo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_completed).setIcon(R.drawable.ic_description_black_24dp);
        menu.findItem(R.id.action_completed).setTitle("Pending Tasks");

        //search option(need to be worked later)
        MenuItem searchItem=menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getBaseContext(),"Searching",Toast.LENGTH_SHORT).show();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getBaseContext(),"Typing",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_completed){
            Toast.makeText(this,"PENDING TASKS",Toast.LENGTH_SHORT).show();
            finish();
        }else if (id == R.id.action_search) {
            Toast.makeText(this,"SEARCH",Toast.LENGTH_SHORT).show();
            return true;
        }else if (id == R.id.action_more_apps){
            Toast.makeText(this,"MORE APPS",Toast.LENGTH_SHORT).show();
            return true;
        }else if (id == R.id.action_settings){
            Toast.makeText(this,"SETTINGS",Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(Todo todo, int position) {
        Toast.makeText(this,"ON CLICK WORKING AT " + position + "POSITION",Toast.LENGTH_SHORT).show();
        if (isMultiSelect)
            multiSelect(position);
    }

    private void multiSelect(int position) {
        if (mActionMode != null) {
            if (selectedPendingTasks.contains(completedTasks.get(position))) {
                selectedPendingTasks.remove(completedTasks.get(position));
            } else {
                selectedPendingTasks.add(completedTasks.get(position));
            }
            if (!selectedPendingTasks.isEmpty())
                mActionMode.setTitle("" + selectedPendingTasks.size());
            else {
                mActionMode.finish();
            }
            todoAdapter.refreshAdapter(completedTasks, selectedPendingTasks);
        }
    }

    @Override
    public void onItemLongClick(final Todo todo, final int position) {
        if (!isMultiSelect) {
            selectedPendingTasks = new ArrayList<>();
            isMultiSelect = true;
            if (mActionMode == null)
                mActionMode = startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.menu_recycler_item_options,menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        int id=item.getItemId();
                        if (id == R.id.action_complete){
                            Toast.makeText(getBaseContext(),"Pending",Toast.LENGTH_SHORT).show();
                            setTaskPending(todo, position);
                        }else if (id == R.id.action_share){
                            Toast.makeText(getBaseContext(),"Share",Toast.LENGTH_SHORT).show();
                            shareTodo(todo);
                        }else if (id == R.id.action_delete){
                            Toast.makeText(getBaseContext(),"Delete",Toast.LENGTH_SHORT).show();
                            deleteTodo(todo,position);
                        }else if (id == android.R.id.home){
                            isMultiSelect = false;
                            selectedPendingTasks.clear();
                            todoAdapter.refreshAdapter(completedTasks, selectedPendingTasks);
                        }
                        mode.finish();
                        return true;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        isMultiSelect = false;
                        selectedPendingTasks.clear();
                        todoAdapter.refreshAdapter(completedTasks, selectedPendingTasks);
                    }
                });
        }
        multiSelect(position);
        Toast.makeText(this,"ON LONG CLICK WORKING AT " + position + "POSITION",Toast.LENGTH_SHORT).show();
    }

    private void setTaskPending(Todo todo, int position) {
        completedTasks.remove(todo);
        todoAdapter.notifyItemChanged(position);
        todo.setCompleted(true);
        todoDatabase.updateDbItem(todo);
    }

    @Override
    public void onItemPriorityChanged(Todo todo, int position) {
    }

    public void shareTodo(Todo todo) {
        String text=String.format(getString(R.string.share_task_format),
                todo.getTitle(),todo.getDescription(),todo.getDueDate().toString(),
                String.valueOf(todo.isImportant()),String.valueOf(todo.isCompleted()),
                String.valueOf(todo.getTimeOfAddition()));
        Log.e(TAG, "shareTodo: "+text );
        String mimeType = "text/plain";
        String title = "Example title";
        Intent shareIntent =   ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(text)
                .getIntent();
        if (shareIntent.resolveActivity(getPackageManager()) != null){
            startActivity(shareIntent);
        }
    }

}

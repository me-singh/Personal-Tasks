package com.example.kadyan.personaltasks.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.kadyan.personaltasks.Adapters.TodoAdapter;
import com.example.kadyan.personaltasks.Constants.AppConstants;
import com.example.kadyan.personaltasks.Constants.DatabaseConstants;
import com.example.kadyan.personaltasks.Data.Todo;
import com.example.kadyan.personaltasks.Data.TodoDatabase;
import com.example.kadyan.personaltasks.R;
import com.example.kadyan.personaltasks.Utils.OnRecyclerItemListner;

import java.util.ArrayList;

/*TODO->
* Add multiSelect feature to delete and share multiple tasks at once
* search option hides after once  (error)
* implement functionality of the search option
* difference b/w navigateUpFromSameTask vs onBackPressed()  (doubt)
* form table for completed tasks(alternate alter status for diff type of tasks in db) (enhance)
* menu click response for both toolbars of main_activity  (enhance)
* construct settings activity for the app  (enhance)
* make functionality better for slide to remove in recyclerView(like in gmail app) (enhance)
* make UI more better
* create splash screen
* create start tutorial with viewpager or signs for steps to follow(recommended)
* add animations for better User Experience
* create notifications for the reminder and alarm(if required)
*/

public class MainActivity extends AppCompatActivity implements OnRecyclerItemListner {

    private static final int REQUEST_ADD_NEW_TODO = 123;
    private static final int REQUEST_EDIT_TODO = 456;
    private final String TAG = this.getClass().getName();

    RecyclerView recyclerView;
    TodoAdapter todoAdapter;
    Toolbar toolbar;
    FloatingActionButton fab;

    //should be synced with one other at all times as positions must be same
    ArrayList<Todo> pendingTasks;
    TodoDatabase todoDatabase;

//    private ArrayList<Todo> selectedPendingTasks = new ArrayList<>();
//    boolean isMultiSelect=false;
//    private ActionMode mActionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        setSupportActionBar(toolbar);

        todoDatabase = TodoDatabase.getTodoDatabase(this);
        pendingTasks = todoDatabase.getAllPendingTasks();
        addItemToRecyclerView();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTodo.class);
                startActivityForResult(intent, REQUEST_ADD_NEW_TODO);
            }
        });

        //swipe to delete functionality(if required)
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(getBaseContext(), "WORKING", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getAdapterPosition();
                try {
                    Todo currentTodo = pendingTasks.get(position);
                    deleteTodo(currentTodo, position);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    Log.e(TAG, "onSwiped: " + "ERRRROOOR");
                }
            }
        }).attachToRecyclerView(recyclerView);

    }


    private void init() {
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        recyclerView=findViewById(R.id.pending_tasks_recyclerView);
        fab = findViewById(R.id.fab);
    }


    private void addItemToRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemDecoration itemDecoration=new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        todoAdapter=new TodoAdapter(this,pendingTasks);
        /***Multi Select Functionality***/
//        todoAdapter = new TodoAdapter(this, pendingTasks,selectedPendingTasks);
        recyclerView.setAdapter(todoAdapter);
    }


    private void deleteTodo(Todo currentTodo, int position) {
        pendingTasks.remove(position);
        todoAdapter.notifyItemRemoved(position);
        todoDatabase.deleteDbItem(currentTodo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_NEW_TODO && resultCode == RESULT_OK){
            Todo currentTodo = (Todo) data.getSerializableExtra(AppConstants.TODO_OBJECT);
            todoDatabase.addTodoToDb(currentTodo);
            pendingTasks.add(currentTodo);
            todoAdapter.notifyDataSetChanged();
            Log.e(TAG, "onActivityResult: "+currentTodo.getTitle()+" "+currentTodo.getDescription()+" "+
                    currentTodo.getDueDate()+" "+currentTodo.getPriority()+" "+currentTodo.getTimeOfAddition());
        }else if (requestCode == REQUEST_EDIT_TODO && resultCode == RESULT_OK){
            Todo currentTodo = (Todo) data.getSerializableExtra(AppConstants.TODO_OBJECT);
            int position = data.getIntExtra(AppConstants.POSITION_IN_RECYCLER_VIEW,-1);
            if (position > -1){
                pendingTasks.set(position,currentTodo);
                todoAdapter.notifyItemChanged(position);
                todoDatabase.updateDbItem(currentTodo);
                Log.e(TAG, "onActivityResult: "+currentTodo.getTitle()+" "+currentTodo.getDescription()+" "+
                        currentTodo.getDueDate()+" "+currentTodo.getPriority()+" "+currentTodo.getTimeOfAddition());
            }else {
                Toast.makeText(this,"ERROR IN ON EDIT RESULT",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_completed).setIcon(R.drawable.ic_done_all_black_24dp);
        menu.findItem(R.id.action_completed).setTitle("Completed Tasks");

        //search option(need to be worked later)
        MenuItem searchItem=menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView= (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
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
            Toast.makeText(this,"COMPLETED TASKS",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CompletedTasks.class);
            startActivity(intent);
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
    public void onItemClick(Todo todo,int position) {
        Toast.makeText(this,"ON CLICK WORKING AT " + position + "POSITION",Toast.LENGTH_SHORT).show();
        /***Multi Select Functionality***/
//        if (isMultiSelect)
//            multiSelect(position);
//        else {
            Intent intent=new Intent(MainActivity.this,AddTodo.class);
            intent.putExtra(AppConstants.TODO_OBJECT, todo);
            intent.putExtra(AppConstants.POSITION_IN_RECYCLER_VIEW, position);
            startActivityForResult(intent, REQUEST_EDIT_TODO);
//        }
    }


    @Override
    public void onItemPriorityChanged(Todo todo, int position) {
        if (position > -1){
            pendingTasks.set(position,todo);
            todoAdapter.notifyItemChanged(position);
            todoDatabase.updateDbItem(todo);
            Log.e(TAG, "onActivityResult: "+todo.getTitle()+" "+todo.getDescription()+" "+
                    todo.getDueDate()+" "+todo.getPriority()+" "+todo.getTimeOfAddition());
        }else {
            Toast.makeText(this,"ERROR IN STAR EDITING",Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this,"ON STAR EDIT WORKING",Toast.LENGTH_SHORT).show();
    }


    /***Multi Select Functionality***/
//    private void multiSelect(int position) {
//        if (mActionMode != null) {
//            if (selectedPendingTasks.contains(pendingTasks.get(position))) {
//                selectedPendingTasks.remove(pendingTasks.get(position));
//            } else {
//                selectedPendingTasks.add(pendingTasks.get(position));
//            }
//            if (selectedPendingTasks.size() > 0)
//                mActionMode.setTitle("" + selectedPendingTasks.size());
//            else {
//                if (mActionMode != null)
//                    mActionMode.finish();
//            }
//            todoAdapter.refreshAdapter(pendingTasks, selectedPendingTasks);
//        }
//    }

    //second toolbar for more options
    @Override
    public void onItemLongClick(final Todo todo, final int position) {
        /***Multi Select Functionality***/
//        if (!isMultiSelect) {
//            selectedPendingTasks = new ArrayList<>();
//            isMultiSelect = true;
//            if (mActionMode == null)
//                mActionMode = startSupportActionMode(new ActionMode.Callback() {
//                    @Override
//                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onDestroyActionMode(ActionMode mode) {
//
//                    }
//                });
//        }
//        multiSelect(position);
        Toast.makeText(this,"ON LONG CLICK WORKING AT " + position + "POSITION",Toast.LENGTH_SHORT).show();
        startSupportActionMode(new ActionMode.Callback() {
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
                    Toast.makeText(getBaseContext(),"Complete",Toast.LENGTH_SHORT).show();
                    setTaskCompleted(todo, position);
                }else if (id == R.id.action_share){
                    Toast.makeText(getBaseContext(),"Share",Toast.LENGTH_SHORT).show();
                    shareTodo(todo);
                }else if (id == R.id.action_delete){
                    Toast.makeText(getBaseContext(),"Delete",Toast.LENGTH_SHORT).show();
                    deleteTodo(todo, position);
                }
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    private void setTaskCompleted(Todo todo, int position) {
        pendingTasks.remove(todo);
        todoAdapter.notifyItemChanged(position);
        todo.setType(DatabaseConstants.COMPLETED_TASKS);
        todoDatabase.updateDbItem(todo);
    }


    public void shareTodo(Todo todo) {
        String text=String.format(getString(R.string.share_task_format),
                todo.getTitle(),todo.getDescription(),todo.getDueDate(),
                String.valueOf(todo.getPriority()),todo.getType(),
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

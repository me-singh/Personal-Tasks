//package com.example.kadyan.personaltasks.Utils;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.NavigationView;
//import android.support.v4.view.GravityCompat;
//import android.support.v4.widget.DrawerLayout;
//import android.support.v7.app.ActionBarDrawerToggle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.support.v7.widget.helper.ItemTouchHelper;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//
//import com.example.kadyan.personalmanager.Adapter.TodoAdapter;
//import com.example.kadyan.personalmanager.Data.TodoContract;
//import com.example.kadyan.personalmanager.Data.TodoDbHelper;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//public class MainActivity extends AppCompatActivity
//        implements NavigationView.OnNavigationItemSelectedListener {
//
//    public static final int REQUEST_ID=123;
//    private static final String ARRAYLIST_CONTENT ="arrayListContent" ;
//    private final String TAG = MainActivity.this.getClass().getName() ;
//    private RecyclerView todoRecylerView;
//    private TodoAdapter todoAdapter;
//    private ArrayList<Todo> todoArrayList=new ArrayList<>();
//
////    private static final String SHARED_PREF_FILE ="fileName" ;
////    Gson gson=new Gson();//to convert json(string) into todoObject and vice-versa
//
//    String title,description, dueDate;
//
////    SQLiteDatabase sqLiteDatabase;
//    TodoDbHelper todoDbHelper;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        todoRecylerView=findViewById(R.id.todoRecyclerView);
//        FloatingActionButton fab =findViewById(R.id.fab_to_add_todo);
//
//        todoDbHelper = new TodoDbHelper(this);
////        sqLiteDatabase=todoDbHelper.getWritableDatabase();
//
//        Cursor cursor=todoDbHelper.getAllTodos();
//
//        while (cursor.moveToNext()){
//            todoArrayList.add(new Todo(cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_TITLE)),
//                    cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DESCRIPTION)),
//                    cursor.getString(cursor.getColumnIndex(TodoContract.TodoEntry.COLUMN_DUEDATE)),0,0));
//        }
//
//
////        //getting storred content into arraylist for displying in recylerview(using SharedPreferences)
////        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREF_FILE,MODE_PRIVATE);
////        Map<String,?> content= sharedPreferences.getAll();
////        for (int i=0;i<content.size();i++){
////            String json= (String) content.get(""+i);
////            Todo todo=gson.fromJson(json,Todo.class);
////            todoArrayList.add(todo);
////        }
//
//        //retrieving data when configuration changes(but not stored permanently)
//        if (savedInstanceState!=null){
//            todoArrayList.clear();
//            todoArrayList= (ArrayList<Todo>) savedInstanceState.getSerializable(ARRAYLIST_CONTENT);
//        }
//
//        todoRecylerView.setLayoutManager(new LinearLayoutManager(this));
////        todoRecylerView.setHasFixedSize(true);
//        todoAdapter = new TodoAdapter(this,todoArrayList);
//        todoRecylerView.setAdapter(todoAdapter);
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("RestrictedApi")
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(MainActivity.this,AddTodo.class);
//                startActivityForResult(intent,REQUEST_ID,null);
//            }
//        });
//
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            //TOBE CHECKED
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int id = (int) viewHolder.itemView.getTag(); // java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.Long
////                        Long.parseLong((String) viewHolder.itemView.getTag());
//
//                boolean flag=todoDbHelper.deleteWithId(id);
//                todoArrayList.remove(id);
//                todoAdapter.notifyDataSetChanged();
//            }
//        }).attachToRecyclerView(todoRecylerView);
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode==REQUEST_ID){
//            if (resultCode==RESULT_OK){
//
//                //data for the todo retrieved
//                title=data.getStringExtra(AddTodo.TODO_TITLE);
//                description=data.getStringExtra(AddTodo.TODO_DESCRIPTION);
//                dueDate =data.getStringExtra(AddTodo.TODO_DUE_DATE);
//
//                Todo newTodo=new Todo(title,description, dueDate,0,0);
//
////                //storing data to the sharedPreferences for later retrieval
////                SharedPreferences.Editor editor=getSharedPreferences(SHARED_PREF_FILE,MODE_PRIVATE).edit();
////                String json=gson.toJson(newTodo);
////                editor.putString(""+todoArrayList.size(),json);
////                editor.apply();//does storing asyncronously
//
//                long pos=todoDbHelper.addTodoToDB(newTodo);
//
//                todoArrayList.add(newTodo);
//
//                Collections.sort(todoArrayList, new Comparator<Todo>() {
//                    @Override
//                    public int compare(Todo o1, Todo o2) {
//                        return o1.getDueDate().compareTo(o2.getDueDate());
//                    }
//                });
//
//                todoAdapter.notifyDataSetChanged();
//
//            }
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putSerializable(ARRAYLIST_CONTENT,todoArrayList);
//    }
//
//}

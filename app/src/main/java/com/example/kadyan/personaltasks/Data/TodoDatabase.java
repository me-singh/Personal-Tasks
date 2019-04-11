package com.example.kadyan.personaltasks.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.kadyan.personaltasks.Constants.DatabaseConstants;

import java.util.ArrayList;

/**
 * Created by Kadyan on 30/04/2018.
 */

public class TodoDatabase extends SQLiteOpenHelper {


    private String TAG=this.getClass().getName();
    private static TodoDatabase todoDatabase;
    private static final String DATABASE_NAME = "appDb.db";
    private static final int DATA_VERSION = 1;


    private TodoDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATA_VERSION);
    }

    //to get instance(only possible single instance) of database
    //synchronized method makes this function run for one instance one at a time(i.e. syncronously for one instance)
    public static synchronized TodoDatabase getTodoDatabase(Context context){
        if ( todoDatabase == null){
            todoDatabase=new TodoDatabase(context);
        }
        return todoDatabase;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + DatabaseConstants.TODO_TABLE +
                "(" +
                DatabaseConstants.COLUMN_ID + " INTEGER PRIMARY KEY, " + // Define a primary key
                DatabaseConstants.COLUMN_TITLE + " TEXT, " +
                DatabaseConstants.COLUMN_DESCRIPTION+ " TEXT," + // Define a foreign key
                DatabaseConstants.COLUMN_TIME_OF_ADDITION+ " INTEGER," +
                DatabaseConstants.COLUMN_DUEDATE + " INTEGER, " +
                DatabaseConstants.COLUMN_DUETIME + " INTEGER, " +
                DatabaseConstants.COLUMN_ISIMPORTANT+ " INTEGER, " +
                DatabaseConstants.COLUMN_ISCOMPLETED + " INTEGER)";
        Log.d("Todo", CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DatabaseConstants.TODO_TABLE);
            onCreate(db);
        }
    }


    public void addTodoToDb(Todo todo){
        SQLiteDatabase sqLiteDatabase = todoDatabase.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(DatabaseConstants.COLUMN_TITLE,todo.getTitle());
            cv.put(DatabaseConstants.COLUMN_DESCRIPTION,todo.getDescription());//1525610153924
            cv.put(DatabaseConstants.COLUMN_TIME_OF_ADDITION,todo.getTimeOfAddition());
            cv.put(DatabaseConstants.COLUMN_DUEDATE,todo.getDueDate());
            cv.put(DatabaseConstants.COLUMN_DUETIME,todo.getDueTime());
            cv.put(DatabaseConstants.COLUMN_ISIMPORTANT,todo.isImportant()? 1:0);
            cv.put(DatabaseConstants.COLUMN_ISCOMPLETED, todo.isCompleted()? 1:0);
            Log.e(TAG, "addTodoToDb: "+todo.getTitle()+" "+todo.getDescription()
                    +" "+todo.getTimeOfAddition()+" "+todo.getDueDate()+" "+todo.getDueTime()
                    +" "+todo.isImportant()+" "+todo.isCompleted());
            long id = sqLiteDatabase.insertOrThrow(DatabaseConstants.TODO_TABLE,null,cv);
            Log.e(TAG, "addTodoToDb: "+ id + "added" );
            if (id == -1)
                Log.e(TAG, "addTodoToDb: "+"WRONG" );
            else
                Log.e(TAG, "addTodoToDb: "+"RIGHT" );
            sqLiteDatabase.setTransactionSuccessful();
        }finally {
            sqLiteDatabase.endTransaction();
        }
    }


    public ArrayList<Todo> getAllTodos(){
        ArrayList<Todo> todoArrayList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = todoDatabase.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query(DatabaseConstants.TODO_TABLE,
                null,
                null,
                null,
                null,
                null,
                null);
        try {
            if (cursor.moveToFirst()){
                do {
                    todoArrayList.add(new Todo(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_DESCRIPTION)),
                            cursor.getLong(cursor.getColumnIndex(DatabaseConstants.COLUMN_TIME_OF_ADDITION)),
                            cursor.getLong(cursor.getColumnIndex(DatabaseConstants.COLUMN_DUEDATE)),
                            cursor.getLong(cursor.getColumnIndex(DatabaseConstants.COLUMN_DUETIME)),
                            cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_ISIMPORTANT)) == 1,
                            cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_ISCOMPLETED)) == 1
                    ));
                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        Log.e(TAG, "getAllTodos: "+ todoArrayList.size() );
        return todoArrayList;
    }


    public ArrayList<Todo> getAllPendingTasks(){
        ArrayList<Todo> pendingTodos = new ArrayList<>();
        ArrayList<Todo> allTodos = getAllTodos();
        for(int i=0;i<allTodos.size();i++){
            if (!allTodos.get(i).isCompleted()){
                pendingTodos.add(allTodos.get(i));
            }
        }
        return pendingTodos;
    }


    public ArrayList<Todo> getAllCompletedTasks(){
        ArrayList<Todo> completedTodos = new ArrayList<>();
        ArrayList<Todo> allTodos = getAllTodos();
        for(int i=0;i<allTodos.size();i++){
            if (allTodos.get(i).isCompleted()){
                completedTodos.add(allTodos.get(i));
            }
        }
        return completedTodos;
    }


    public void updateDbItem(Todo todo){
        SQLiteDatabase sqLiteDatabase=todoDatabase.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues cv=new ContentValues();
            cv.put(DatabaseConstants.COLUMN_TITLE, todo.getTitle());
            cv.put(DatabaseConstants.COLUMN_DESCRIPTION, todo.getDescription());
            cv.put(DatabaseConstants.COLUMN_TIME_OF_ADDITION, todo.getTimeOfAddition());
            cv.put(DatabaseConstants.COLUMN_DUEDATE, todo.getDueDate());
            cv.put(DatabaseConstants.COLUMN_DUETIME, todo.getDueTime());
            cv.put(DatabaseConstants.COLUMN_ISIMPORTANT, todo.isImportant()? 1:0);
            cv.put(DatabaseConstants.COLUMN_ISCOMPLETED, todo.isCompleted()? 1:0);
            int items = sqLiteDatabase.update(DatabaseConstants.TODO_TABLE, cv,
                    DatabaseConstants.COLUMN_TIME_OF_ADDITION + "=?",
                    new String[]{String.valueOf(todo.getTimeOfAddition())});
            if (items == 1)
                Log.e(TAG, "updateDbItem: "+"RIGHT");
            else
                Log.e(TAG, "updateDbItem: "+"WRONG");
            sqLiteDatabase.setTransactionSuccessful();
        }finally {
            sqLiteDatabase.endTransaction();
        }
    }


    public void deleteDbItem(Todo todo) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            int items = sqLiteDatabase.delete(DatabaseConstants.TODO_TABLE,
                    DatabaseConstants.COLUMN_TIME_OF_ADDITION + "=?",
                    new String[]{String.valueOf(todo.getTimeOfAddition())});
            Log.d("Rows", String.valueOf(items));
            if (items == 1)
                Log.e(TAG, "deleteDbItem: " + "ROGHT");
            else
                Log.e(TAG, "deleteDbItem: " + "WRONG");
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

}

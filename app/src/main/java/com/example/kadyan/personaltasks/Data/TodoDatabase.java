package com.example.kadyan.personaltasks.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
        String CREATE_TABLE = "CREATE TABLE " + TodoContract.TODO_TABLE +
                "(" +
                TodoContract.COLUMN_ID+ " INTEGER PRIMARY KEY," + // Define a primary key
                TodoContract.COLUMN_TITLE + " TEXT, " + TodoContract.COLUMN_DESCRIPTION+ " TEXT," + // Define a foreign key
                TodoContract.COLUMN_DUEDATE + " TEXT, " + TodoContract.COLUMN_PRIORITY+ " INTEGER, " +
                TodoContract.COLUMN_TIME_OF_ADDITION+ " INTEGER)";
        Log.d("Todo", CREATE_TABLE);
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TodoContract.TODO_TABLE);
            onCreate(db);
        }
    }

    public ArrayList<Todo> getAllTodos(){
        ArrayList<Todo> todoArrayList=new ArrayList<>();
        SQLiteDatabase sqLiteDatabase=todoDatabase.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query(TodoContract.TODO_TABLE,
                null,
                null,
                null,
                null,
                null,
                null);
        try {
            if (cursor.moveToFirst()){
                do {
                    todoArrayList.add(new Todo(cursor.getString(cursor.getColumnIndex(TodoContract.COLUMN_TITLE)),
                            cursor.getString(cursor.getColumnIndex(TodoContract.COLUMN_DESCRIPTION)),
                            cursor.getString(cursor.getColumnIndex(TodoContract.COLUMN_DUEDATE)),
                            cursor.getInt(cursor.getColumnIndex(TodoContract.COLUMN_PRIORITY)),
                            cursor.getLong(cursor.getColumnIndex(TodoContract.COLUMN_TIME_OF_ADDITION))
                    ));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }
        Log.e(TAG, "getAllTodos: "+ todoArrayList.size() );
        return todoArrayList;
    }

    public void addTodoToDb(Todo todo){
        SQLiteDatabase sqLiteDatabase=todoDatabase.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues cv=new ContentValues();
            cv.put(TodoContract.COLUMN_TITLE,todo.getTitle());
            cv.put(TodoContract.COLUMN_DESCRIPTION,todo.getDescription());//1525610153924
            cv.put(TodoContract.COLUMN_DUEDATE,todo.getDueDate());
            cv.put(TodoContract.COLUMN_PRIORITY,todo.getPriority());
            cv.put(TodoContract.COLUMN_TIME_OF_ADDITION,todo.getTimeOfAddition());
            Log.e(TAG, "addTodoToDb: "+todo.getTitle()+" "+todo.getDueDate()+" "+todo.getPriority()+" "+todo.getTimeOfAddition() );
            long id=sqLiteDatabase.insertOrThrow(TodoContract.TODO_TABLE,null,cv);
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

    public void updateDbItem(Todo todo){
        SQLiteDatabase sqLiteDatabase=todoDatabase.getWritableDatabase();
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues cv=new ContentValues();
            cv.put(TodoContract.COLUMN_TITLE,todo.getTitle());
            cv.put(TodoContract.COLUMN_DESCRIPTION,todo.getDescription());
            cv.put(TodoContract.COLUMN_DUEDATE,todo.getDueDate());
            cv.put(TodoContract.COLUMN_PRIORITY,todo.getPriority());
            cv.put(TodoContract.COLUMN_TIME_OF_ADDITION,todo.getTimeOfAddition());
            int items=sqLiteDatabase.update(TodoContract.TODO_TABLE,cv,TodoContract.COLUMN_TIME_OF_ADDITION+"=?",
                                                                        new String[]{String.valueOf(todo.getTimeOfAddition())});
            if (items==1)
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
            int items = sqLiteDatabase.delete(TodoContract.TODO_TABLE, TodoContract.COLUMN_TIME_OF_ADDITION + "=?",
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

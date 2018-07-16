package com.example.kadyan.personaltasks.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.kadyan.personaltasks.Constants.DatabaseConstants;
import com.example.kadyan.personaltasks.Data.Todo;
import com.example.kadyan.personaltasks.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.example.kadyan.personaltasks.Constants.AppConstants.POSITION_IN_RECYCLER_VIEW;
import static com.example.kadyan.personaltasks.Constants.AppConstants.TODO_OBJECT;

public class AddTodo extends AppCompatActivity{

    public final String TAG = this.getClass().getName();

    Todo currentTodo;
    int currentPosition;

    EditText addTitle;
    EditText addDescription;
    EditText addDueDate;
    RadioGroup radioGroup;
    Toolbar toolbar;
    CheckBox todoNotification;//to make an alarm or notification

    Calendar myCalendar = Calendar.getInstance();
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();
        setSupportActionBar(toolbar);

        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(TODO_OBJECT)){
            setUpTheActivity(getIntent());
        }

        addDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDatePicker();//set datePicker and gets date into editText formated
            }
        });

        todoNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setTimePicker();
                }
            }
        });

    }


    private void init() {
        setContentView(R.layout.activity_add_todo);
        toolbar = findViewById(R.id.toolbar);
        addTitle=findViewById(R.id.addTitleEditText);
        addDescription=findViewById(R.id.addDescriptionEditText);
        addDueDate=findViewById(R.id.addDateEditText);
        radioGroup = findViewById(R.id.radioGroup);
        todoNotification=findViewById(R.id.todoNotification);
    }


    private void setUpTheActivity(Intent intent) {
        currentTodo = (Todo) intent.getSerializableExtra(TODO_OBJECT);
        addTitle.setText(currentTodo.getTitle());
        addDescription.setText(currentTodo.getDescription());
        addDueDate.setText(currentTodo.getDueDate());
        if (currentTodo.getPriority() == 1){
            ((RadioButton)findViewById(R.id.radioImportant)).toggle();
        }
        currentPosition = intent.getIntExtra(POSITION_IN_RECYCLER_VIEW,-1);
        Log.e(TAG, "onCreate: "+ currentPosition+" "+currentTodo.getTitle()+" "+currentTodo.getDescription()+" "+
                currentTodo.getDueDate()+" "+currentTodo.getPriority()+" "+currentTodo.getTimeOfAddition());
    }


    @Override
    public void onBackPressed() {
        if (!TextUtils.isEmpty(addTitle.getText())){
            showAlertDialog();
        }else {
            super.onBackPressed();
        }
    }


    private void setDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR,year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                updateDueDate();
            }
        };
        new DatePickerDialog(AddTodo.this,dateSetListener,myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void updateDueDate() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
        addDueDate.setText(simpleDateFormat.format(myCalendar.getTime()));
    }


    private void setTimePicker() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                myCalendar.set(Calendar.MINUTE,minute);
                updateNotificationTime();
            }
        };
        new TimePickerDialog(AddTodo.this,timeSetListener,myCalendar.get(Calendar.HOUR_OF_DAY),
                myCalendar.get(Calendar.MINUTE),true).show();
    }


    //update to the ui to show to the user
    private void updateNotificationTime() {
        String myFormat = "hh:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat,Locale.getDefault());
        Toast.makeText(this,simpleDateFormat.format(myCalendar.getTime()),Toast.LENGTH_SHORT).show();
        //set time to the required view
    }


    //show alert dialog to save details before pressing back button in addTodo activity
    private void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTodo.this)
                .setTitle("Are You Sure?")
                .setMessage("Quit Without Saving?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(),"STAY ON PAGE",Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getBaseContext(),"LEAVE THE PAGE",Toast.LENGTH_SHORT).show();
                        AddTodo.super.onBackPressed();
                    }
                })
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.show();
    }


    private int getPriority() {
        int priority;
        int priorityId = radioGroup.getCheckedRadioButtonId();
        if (priorityId == R.id.radioNotImportant) {
            priority = 0;
        }
        else if (priorityId == R.id.radioImportant){
            priority = 1;
        }
        else {
            priority = 222;
            Toast.makeText(this,"MUST BE ERROR",Toast.LENGTH_SHORT).show();
        }
        return priority;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_todo,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home : {
                onBackPressed();
                //NavUtils.navigateUpFromSameTask(AddTodo.this);//check for its working and why it is not working
                return true;
            }
            case R.id.addTodoDetails : {
                if (TextUtils.isEmpty(addTitle.getText())){
                    Snackbar.make(findViewById(R.id.constraint_add_todo),"Title must be provided",Snackbar.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent();
                    Todo todo;
                    if (currentTodo != null){
                        todo = new Todo(addTitle.getText().toString(),
                                addDescription.getText().toString(),
                                addDueDate.getText().toString(),
                                getPriority(),
                                DatabaseConstants.PENDING_TASKS,
                                currentTodo.getTimeOfAddition());
                        intent.putExtra(POSITION_IN_RECYCLER_VIEW,currentPosition);
                    }else {
                        todo = new Todo(addTitle.getText().toString(),
                                addDescription.getText().toString(),
                                addDueDate.getText().toString(),
                                getPriority(),
                                DatabaseConstants.PENDING_TASKS,
                                Calendar.getInstance().getTimeInMillis());
                    }
                    intent.putExtra(TODO_OBJECT,todo);
                    setResult(RESULT_OK,intent);
//                  Snackbar.make(findViewById(R.id.constraint_add_todo), "ADD TO DATABASE :)", Snackbar.LENGTH_SHORT)
//                          .setAction("Undo", new View.OnClickListener() {
//                              @Override
//                              public void onClick(View v) {
//                                 Toast.makeText(getBaseContext(),"Removed from DB.",Toast.LENGTH_SHORT).show();
//                              }
//                          }).show();
                      finish();
                }
                return true;
            }
            default : return super.onOptionsItemSelected(item);
        }
    }


}

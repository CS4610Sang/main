package com.example.creatingahabit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;

public class EditHabitActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    DatabaseHelper myDB;
    EditText habitName, habitDescription, habitFrequency;
    MenuItem createHabit;
    Spinner frequencySpinner;
    String spinnerSelected;
//    String formerHabitName;
    private int habitID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_habit);

        myDB = new DatabaseHelper(this);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        habitName = (EditText) findViewById(R.id.habitName);
        habitDescription = (EditText) findViewById(R.id.description);
        habitFrequency = (EditText) findViewById(R.id.frequency);
        createHabit = (MenuItem) findViewById(R.id.save);
        frequencySpinner = (Spinner)findViewById(R.id.frequency_spinner);
        spinnerCreation();

        //Extract data from Intent
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //Habit name is passed through the Intent
        String message = extras.getString("habit_name");
        setTitle(message);
//        formerHabitName = habitName.getText().toString();
        habitID = myDB.returnID(message);
        populateEditText(message);


    }

    public void spinnerCreation(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.frequency_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        frequencySpinner.setAdapter(adapter);
        frequencySpinner.setOnItemSelectedListener(this);
    }

    public void populateEditText(String name){
        Cursor cursor = null;
        try{
            cursor = myDB.getItem(String.valueOf(habitID));
            if(cursor.getCount() == 0) {
                Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
                return;
            }else {
                cursor.moveToFirst();
                //Populate fields
                String hName = cursor.getString(cursor.getColumnIndex("NAME"));
                String descrip = cursor.getString(cursor.getColumnIndex("DESCRIPTION"));
                int freq = cursor.getInt(cursor.getColumnIndex("FREQUENCY"));
                String timePeriod = cursor.getString(cursor.getColumnIndex("TIME_PERIOD"));
//                Log.d("E", habitName + descrip + freq + timePeriod);
                habitName.setText(hName);
                habitDescription.setText(descrip);
                habitFrequency.setText(String.valueOf(freq));
                frequencySpinner.setSelection(returnSpinnerColumn(timePeriod));
            }
        }finally {
            cursor.close();
        }
    }


    public static int returnSpinnerColumn(String selection){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Day", 0);
        map.put("Week", 1);
        map.put("Month",2);
        map.put("Year",3);
        return map.get(selection);
    }

    public void clickSave(View view){
        myDB.updateData(habitID, habitName.getText().toString(),
                habitDescription.getText().toString(),
                habitFrequency.getText().toString(), spinnerSelected);
        Intent create = new Intent(this, MainActivity.class);
        startActivity(create);
    }

    //
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.homeAsUp:
//                Intent sendToDescription = new Intent(this, DescriptionActivity.class);
//                Bundle extra = new Bundle();
//                //Passes the habit name into the Intent extra
//                Log.d("E", "former habit:" + formerHabitName);
//                extra.putString("name",formerHabitName);
//                sendToDescription.putExtras(extra);
//                startActivity(sendToDescription);
//        }
//        return super.onOptionsItemSelected(item);
//    }
        @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        spinnerSelected = adapterView.getItemAtPosition(position).toString();
        Toast.makeText(adapterView.getContext(), "Selected spinner frequency: "+ spinnerSelected, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}

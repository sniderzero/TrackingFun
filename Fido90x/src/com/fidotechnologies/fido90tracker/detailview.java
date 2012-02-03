package com.fidotechnologies.fido90tracker;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class detailview extends Activity {
	
	//declare stuff here :)
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected Cursor cursor_user;
	protected ListAdapter adapter;
	protected int curPos;
	protected String dayID;
	TextView txtName;
	TextView txtRepsValue;
	TextView txtWeightValue;
	TextView txtTimeValue;
	Spinner spnWeight;
	Spinner spnReps;
	Spinner spnTime;
	ArrayAdapter<CharSequence> adapter_time;
	ArrayAdapter<CharSequence> adapter_rep;
	ArrayAdapter<CharSequence> adapter_weight;
	ImageButton btnNext;
	ImageButton btnPrev;

	
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.detailview);
        //declaring widgets
        txtName = (TextView) findViewById(R.id.txtName);
        txtRepsValue = (TextView) findViewById(R.id.txtRepsValue);
        txtWeightValue = (TextView) findViewById(R.id.txtWeightValue);
        txtTimeValue = (TextView) findViewById(R.id.txtTimeValue);
    	btnNext = (ImageButton) findViewById(R.id.imgNext);
    	btnPrev = (ImageButton) findViewById(R.id.imgPrev);
        //open db
        db = (new DBHelper(this)).getWritableDatabase();
    	//grab exercise name variable passed from previous activity
        curPos = getIntent().getIntExtra("int",-1);
        //grab day name variable passed from pervious activity
        dayID = getIntent().getStringExtra("PROGRAM_DAY");
    	// query db based on that variable
    	cursor = db.rawQuery("SELECT _id, day, name, type, exernum, bonus FROM nintyexer WHERE day = " + "'" + dayID +"'", null);
    	cursor.moveToPosition(curPos);
    	//db.close();
    	// txtName Value based on previous selection criteria    	
    	txtName.setText(cursor.getString(2));
    	//creating some spinner adapters
    	adapter_rep = ArrayAdapter.createFromResource(
    			  this, R.array.rep_values, android.R.layout.simple_spinner_item );
    			adapter_rep.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
    	adapter_weight = ArrayAdapter.createFromResource(
    	    	this, R.array.weight_values, android.R.layout.simple_spinner_item );
    	    	adapter_weight.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
    	adapter_time = ArrayAdapter.createFromResource(
    	    	this, R.array.time_values, android.R.layout.simple_spinner_item );
    	    	adapter_time.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );    
    	//assigning adapters to spinners
    	spnReps = (Spinner) findViewById( R.id.spnReps );
    	spnReps.setAdapter(adapter_rep);
    	spnWeight = (Spinner) findViewById( R.id.spnWeight );
    	spnWeight.setAdapter(adapter_weight);
    	spnTime = (Spinner) findViewById( R.id.spnTime );
    	spnTime.setAdapter(adapter_time);
    	fillUserStats();
    }
    public void nextExercise(View v)
    {
    	//TextView txtName = (TextView) findViewById(R.id.txtName);
    	
    	if (cursor.isLast() == false) {
        	//if the cursor is not at the end, go to the next one and change the value of txtName
    		saveRecord(v);
    		cursor.moveToNext();
    		fillUserStats();
    		//viewRefresh();
        	txtName.setText(cursor.getString(2));
        	btnPrev.setVisibility(View.VISIBLE);
        	
    	}
    	else if (cursor.isLast() == true){
    	btnNext.setVisibility(View.INVISIBLE);
    	}
    }
  public void prevExercise(View v)
        {
        	TextView txtName = (TextView) findViewById(R.id.txtName);
        	if (cursor.isFirst() == false) {
            	//if the cursor is not at the end, go to the next one and change the value of txtName
        		cursor.moveToPrevious();
        		fillUserStats();
            	txtName.setText(cursor.getString(2));
            	btnNext.setVisibility(View.VISIBLE);
        	}
        	else{
        		btnPrev.setVisibility(View.INVISIBLE);
        	}
    }		
  public void saveRecord(View v){
		String spnWeightInt = spnWeight.getItemAtPosition((int) spnWeight.getSelectedItemId()).toString();
		String spnRepsInt = spnReps.getItemAtPosition((int) spnReps.getSelectedItemId()).toString();
		String spnTimeInt = spnTime.getItemAtPosition((int) spnTime.getSelectedItemId()).toString();
		String strDate = getDate();
	  
	  db.execSQL("INSERT INTO userstats (exername,weight,reps,time,date) VALUES("+ "'" + cursor.getString(2) +"'" + "," 
			+ "'" + spnWeightInt +"'"+","+"'" + spnRepsInt +"'"+","+"'" + spnTimeInt +"'"+","+ "'" + strDate +"'"+ ")");
	
	
  }
  public void fillUserStats(){
  	//changing the textviews to show the users last results
  	cursor_user = db.rawQuery("SELECT _id, weight, exername, reps, time FROM userstats WHERE exername = " + "'" + cursor.getString(2) +"'", null);
  	
  	if (cursor_user.getCount()==0){
  		txtRepsValue.setText("0");
  		txtWeightValue.setText("0");
  		txtTimeValue.setText("0");
  	}
  	else{
  		cursor_user.moveToLast();
  		txtRepsValue.setText(cursor_user.getString(3));
  		txtWeightValue.setText(cursor_user.getString(1));
  		txtTimeValue.setText(cursor_user.getString(4));
  	}
  }
  	public void viewRefresh(){
  		adapter_time.notifyDataSetInvalidated();
  		adapter_weight.notifyDataSetInvalidated();
  		adapter_rep.notifyDataSetInvalidated();
  		
  	}
  	public String getDate(){
        // get the current date
  		int intYear;
  		int intMonth;
  		int intDay;
  		String fullDate;
        Calendar c = Calendar.getInstance();
        intYear = c.get(Calendar.YEAR);
        intMonth = c.get(Calendar.MONTH) + 1;
        intDay = c.get(Calendar.DAY_OF_MONTH); 
        
        Integer.toString(intYear);
        Integer.toString(intMonth);
        Integer.toString(intDay);
        fullDate = intMonth + "/" + intDay + "/" + intYear;
        
        return fullDate;       
        
  	}
  }

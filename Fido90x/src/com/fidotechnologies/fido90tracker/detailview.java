package com.fidotechnologies.fido90tracker;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
protected Cursor cursor, cursor_user;
protected ListAdapter adapter;
protected int curPos;
int dayID, type, hasRIP;
TextView txtName, txtRepsValue, txtWeightValue, txtTimeValue, txtAssistValue, txtBandValue, txtDateValue;
Spinner spnWeight, spnReps, spnBand, spnAssist;
ArrayAdapter<CharSequence> adapter_band, adapter_rep, adapter_weight, adapter_assist;
Button btnNext, btnPrev;
SharedPreferences preferences;
String equipPref;


@Override
// on open
    public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
        setContentView(R.layout.detailview);
        //building widgets
        txtName = (TextView) findViewById(R.id.txtName);
        txtRepsValue = (TextView) findViewById(R.id.txtRepsValue);
        txtWeightValue = (TextView) findViewById(R.id.txtWeightValue);
        txtTimeValue = (TextView) findViewById(R.id.txtTimeValue);
        txtAssistValue = (TextView) findViewById(R.id.txtAssistValue);
        txtBandValue = (TextView) findViewById(R.id.txtBandValue);
        txtDateValue = (TextView) findViewById(R.id.txtDateValue);
     btnNext = (Button) findViewById(R.id.imgNext);
     btnPrev = (Button) findViewById(R.id.imgPrev);

     //creating some spinner adapters
     adapter_rep = ArrayAdapter.createFromResource(
     this, R.array.rep_values, android.R.layout.simple_spinner_item );
     adapter_rep.setDropDownViewResource( android.R.layout.simple_spinner_item );
     adapter_weight = ArrayAdapter.createFromResource(
     this, R.array.weight_values, android.R.layout.simple_spinner_item );
     adapter_weight.setDropDownViewResource( android.R.layout.simple_spinner_item);
     adapter_band = ArrayAdapter.createFromResource(
     this, R.array.band_values, android.R.layout.simple_spinner_item );
     adapter_band.setDropDownViewResource( android.R.layout.simple_spinner_item );
     adapter_assist= ArrayAdapter.createFromResource(
     this, R.array.assist_values, android.R.layout.simple_spinner_item );
     adapter_assist.setDropDownViewResource( android.R.layout.simple_spinner_item );

     //creating spinners and assigning adapters
     spnReps = (Spinner) findViewById( R.id.spnReps );
     spnReps.setAdapter(adapter_rep);
     spnWeight = (Spinner) findViewById( R.id.spnWeight );
     spnWeight.setAdapter(adapter_weight);
     spnBand = (Spinner) findViewById( R.id.spnBand );
     spnBand.setAdapter(adapter_band);
     spnAssist = (Spinner) findViewById( R.id.spnAssist );
     spnAssist.setAdapter(adapter_assist);

        //open db
        db = (new DBHelper(this)).getWritableDatabase();
     //grab variables from previous activity
        curPos = getIntent().getIntExtra("int",-1); //current postion
        dayID = getIntent().getIntExtra("PROGRAM_DAY", 0); //day name
        hasRIP = getIntent().getIntExtra("HAS_RIPPER", 1); //has ripper or not
     // query db based on that variable and move to correct position
        if (hasRIP == 0){
         nohasRipper();
        }
        else{
         hasRipper();
        }
     //move to position selected in list
     cursor.moveToPosition(curPos);
     //db.close();
     
     // txtName Value based on previous selection criteria
     txtName.setText(cursor.getString(2));
     
     // filling out user stats     
     fillUserStats();
     
     //grabbing the band/weight preference
     preferences = PreferenceManager.getDefaultSharedPreferences(this);
     equipPref = preferences.getString("equipmentType", "");
     
     //calling function to set what is visible
     showWhat();
     
     //checking to see if we are at the beginning or end, and showing the appropriate buttons for each
     atEnd();
     atBeginning();
     

     
    
    }
    public void nextExercise(View v)
    {
     //saving user stats
     saveRecord();
     //moving to next record
     cursor.moveToNext();
     //reseting our view
     fillUserStats();
     //reset type
     showWhat();
     txtName.setText(cursor.getString(2));
     //checking to see where we are and setting next button to invisible if we are at the end
     atEnd();
     atBeginning();
    }
    
  public void prevExercise(View v)
        {
	  //moving to prev record
	  cursor.moveToPrevious();
	  //reseting our view
	  fillUserStats();
	  //reset type
	  showWhat();
	  txtName.setText(cursor.getString(2));
	  //checking to see where we are and setting next button to invisible if we are at the end
	  atEnd();
	  atBeginning();
    }
  
  
public void saveRecord(){
	String spnWeightInt = spnWeight.getItemAtPosition((int) spnWeight.getSelectedItemId()).toString();
	String spnRepsInt = spnReps.getItemAtPosition((int) spnReps.getSelectedItemId()).toString();
	String spnBandInt = spnBand.getItemAtPosition((int) spnBand.getSelectedItemId()).toString();
	String spnAssistInt = spnAssist.getItemAtPosition((int) spnAssist.getSelectedItemId()).toString();
	String strDate = getDate();

	db.execSQL("INSERT INTO results (ExerciseName,weight,reps,band,date,pullup) VALUES("+ "'" + cursor.getString(2) +"'" + ","
			+ "'" + spnWeightInt +"'"+","+"'" + spnRepsInt +"'"+","+"'" + spnBandInt +"'"+","+ "'" + strDate +"'"+","+ + 0 + ")");


  }


  public void fillUserStats(){
   //changing the textviews to show the users last results
   cursor_user = db.rawQuery("SELECT _id, weight, ExerciseName, reps, time, date, pullup, band FROM results WHERE ExerciseName = " + "'" + cursor.getString(2) +"'", null);
  
   if (cursor_user.getCount()==0){
   txtRepsValue.setText("null");
   txtWeightValue.setText("null");
   txtTimeValue.setText("null");
   txtAssistValue.setText("null");
   txtBandValue.setText("null");
   txtDateValue.setText("null");
   }
   else{
   cursor_user.moveToLast();
   txtRepsValue.setText(cursor_user.getString(3));
   txtWeightValue.setText(cursor_user.getString(1));
   txtTimeValue.setText(cursor_user.getString(4));
   txtAssistValue.setText(cursor_user.getString(6));
   txtBandValue.setText(cursor_user.getString(7));
   txtDateValue.setText(cursor_user.getString(5));
   }
  }
   public void viewRefresh(){
   adapter_band.notifyDataSetInvalidated();
   adapter_weight.notifyDataSetInvalidated();
   adapter_rep.notifyDataSetInvalidated();
  
   }
   public static String getDate(){
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
   public void showWhat(){
   type = cursor.getInt(4);
   switch(type){
   case 1:
   showReps();
   break;
   case 2:
   weightsOrBands();
   break;
   case 3:
   showTime();
   break;
   case 4:
   showAssist();
   break;
   }

   }
public void atEnd(){
if (cursor.isLast() == false) {
     btnNext.setVisibility(View.VISIBLE);
     }
else if (cursor.isLast() == true){
btnNext.setVisibility(View.INVISIBLE);
}
}
public void atBeginning(){
if (cursor.isFirst() == false) {
     btnPrev.setVisibility(View.VISIBLE);
     }
else if (cursor.isFirst() == true){
btnPrev.setVisibility(View.INVISIBLE);
}
}

public void nohasRipper(){

    cursor = db.rawQuery("SELECT _id, dayID, name, exernum, type FROM p90Exercises WHERE dayID =" + dayID, null);

}
public void hasRipper(){
cursor = db.rawQuery("SELECT _id, dayID, name, exernum, type FROM p90Exercises WHERE dayID =" + dayID + " or dayID = "+ 12, null);
}

public void clickHistory(View v){
Intent intent = new Intent(detailview.this, HistoryView.class);
intent.putExtra("EXERCISE_NAME", cursor.getString(2));
intent.putExtra("EXERCISE_TYPE", cursor.getInt(4));
startActivity(intent);
}
//different functions for changing item visibility dynamically
public void showReps(){
   ((TextView)findViewById(R.id.txtWeight)).setVisibility(View.INVISIBLE);
   ((TextView)findViewById(R.id.txtBand)).setVisibility(View.INVISIBLE);
   ((TextView)findViewById(R.id.txtAssist)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtReps)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtTime)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtWeightValue)).setVisibility(View.INVISIBLE);
   ((TextView)findViewById(R.id.txtBandValue)).setVisibility(View.INVISIBLE);
   ((TextView)findViewById(R.id.txtAssistValue)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtRepsValue)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtTimeValue)).setVisibility(View.GONE); 
	
   ((LinearLayout)findViewById(R.id.llReps)).setVisibility(View.VISIBLE);
   ((LinearLayout)findViewById(R.id.llWeight)).setVisibility(View.GONE);
   ((LinearLayout)findViewById(R.id.llAssist)).setVisibility(View.GONE);
   ((LinearLayout)findViewById(R.id.llBand)).setVisibility(View.GONE);
   
}

public void showWeights() {

   ((TextView)findViewById(R.id.txtWeight)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtBand)).setVisibility(View.INVISIBLE);
   ((TextView)findViewById(R.id.txtAssist)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtReps)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtTime)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtWeightValue)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtBandValue)).setVisibility(View.INVISIBLE);
   ((TextView)findViewById(R.id.txtAssistValue)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtRepsValue)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtTimeValue)).setVisibility(View.GONE);
   
   ((LinearLayout)findViewById(R.id.llReps)).setVisibility(View.VISIBLE);
   ((LinearLayout)findViewById(R.id.llWeight)).setVisibility(View.VISIBLE);
   ((LinearLayout)findViewById(R.id.llAssist)).setVisibility(View.GONE);
   ((LinearLayout)findViewById(R.id.llBand)).setVisibility(View.GONE);
}

public void showTime() {
//do time stuff in here
}

public void showAssist() {
   ((TextView)findViewById(R.id.txtWeight)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtBand)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtAssist)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtReps)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtTime)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtWeightValue)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtBandValue)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtAssistValue)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtRepsValue)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtTimeValue)).setVisibility(View.GONE);
   
   ((LinearLayout)findViewById(R.id.llReps)).setVisibility(View.VISIBLE);
   ((LinearLayout)findViewById(R.id.llWeight)).setVisibility(View.GONE);
   ((LinearLayout)findViewById(R.id.llAssist)).setVisibility(View.VISIBLE);
   ((LinearLayout)findViewById(R.id.llBand)).setVisibility(View.VISIBLE);
}

public void showBand(){

   ((TextView)findViewById(R.id.txtWeight)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtBand)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtAssist)).setVisibility(View.INVISIBLE);
   ((TextView)findViewById(R.id.txtReps)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtTime)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtWeightValue)).setVisibility(View.GONE);
   ((TextView)findViewById(R.id.txtBandValue)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtAssistValue)).setVisibility(View.INVISIBLE);
   ((TextView)findViewById(R.id.txtRepsValue)).setVisibility(View.VISIBLE);
   ((TextView)findViewById(R.id.txtTimeValue)).setVisibility(View.GONE);
   
   ((LinearLayout)findViewById(R.id.llReps)).setVisibility(View.VISIBLE);
   ((LinearLayout)findViewById(R.id.llWeight)).setVisibility(View.GONE);
   ((LinearLayout)findViewById(R.id.llAssist)).setVisibility(View.GONE);
   ((LinearLayout)findViewById(R.id.llBand)).setVisibility(View.VISIBLE);
}

public void weightsOrBands(){
	if(equipPref.equals("Bands")){
		showBand();
	}
	else {
		showWeights();
	}
}

}
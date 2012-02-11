package com.fidotechnologies.fido90tracker;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
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

//declare stuff here for non-stopwatch
protected SQLiteDatabase db;
protected Cursor cursor, cursor_user;
protected ListAdapter adapter;
protected int curPos;
int dayID, type, hasRIP;
String spnBandStr, spnAssistStr, TimerStr, strDate, spnWeightStr, spnRepsStr;
TextView txtName, txtRepsValue, txtWeightValue, txtTimeValue, txtAssistValue, txtBandValue, txtDateValue;
Spinner spnWeight, spnReps, spnBand, spnAssist;
ArrayAdapter<CharSequence> adapter_band, adapter_rep, adapter_weight, adapter_assist;
Button btnNext, btnPrev, actionBtn;
SharedPreferences preferences;
String equipPref;
//declare stuff her for stopwatch
private TextView timerTextView;
private Handler mHandler = new Handler();
private long startTime;
private long elapsedTime;
private final int REFRESH_RATE = 100;
private String hours,minutes,seconds;
private long secs,mins,hrs;
private boolean stopped = false;
Intent intent;


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

        timerTextView = (TextView) findViewById(R.id.timer); 
        actionBtn = (Button)findViewById(R.id.startButton);
        actionBtn = (Button)findViewById(R.id.resetButton);
        actionBtn = (Button)findViewById(R.id.stopButton);

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
     
     //setting the date string
     strDate = getDate();

     
    
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
     viewRefresh();
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
	  viewRefresh();
    }
  
  
public void saveRecord(){
	//grabing values
	spnWeightStr = spnWeight.getItemAtPosition((int) spnWeight.getSelectedItemId()).toString();
	spnRepsStr = spnReps.getItemAtPosition((int) spnReps.getSelectedItemId()).toString();
	spnBandStr = spnBand.getItemAtPosition((int) spnBand.getSelectedItemId()).toString();
	spnAssistStr = spnAssist.getItemAtPosition((int) spnAssist.getSelectedItemId()).toString();
	TimerStr = timerTextView.getText().toString();
	
	if(spnRepsStr.equals("0") && TimerStr.equals("00:00:00")){
		Toast.makeText(this, "Exercise Not Saved, proper values not selected", 1500).show();
	}
	else {
		
		//storing them in DB if they aren't all 0
		db.execSQL("INSERT INTO results (ExerciseName,weight,reps,band,date,pullup,time) VALUES("+ "'" + cursor.getString(2) +"'" + ","
				+  "'" +spnWeightStr+"'" +","+ "'" +spnRepsStr+"'" +","+"'" + spnBandStr +"'"+","+ "'" + strDate +"'"+ ","+"'" + 
				spnAssistStr +"'"+ ","+"'" + TimerStr +"'"+ ")");
		Toast.makeText(this, "Exercise Saved", 1500).show();
		

	}


  }


  public void fillUserStats(){
   //changing the textviews to show the users last results
   cursor_user = db.rawQuery("SELECT _id, weight, ExerciseName, reps, time, date, pullup, band FROM results WHERE ExerciseName = " + "'" + cursor.getString(2) +"'", null);
  
   if (cursor_user.getCount()==0){
   txtRepsValue.setText("");
   txtWeightValue.setText("");
   txtTimeValue.setText("");
   txtAssistValue.setText("");
   txtBandValue.setText("");
   txtDateValue.setText("");
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
  
  //function resets spinners and timer
   public void viewRefresh(){
		((TextView)findViewById(R.id.timer)).setText("00:00:00");
		spnWeight.setSelection(0);
		spnReps.setSelection(0);
		spnAssist.setSelection(0);
		spnBand.setSelection(0);
  
   }
   public static String getDate(){
   // get the current date and turn it into a String
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
   //Switch that decides what widgets are displayed on the screen
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
   
   //checking to see if we are at the end of the cursor
public void atEnd(){
if (cursor.isLast() == false) {
     btnNext.setVisibility(View.VISIBLE);
     }
else if (cursor.isLast() == true){
btnNext.setVisibility(View.INVISIBLE);
}
}

//check to see if we are at the beginning of the cursor
public void atBeginning(){
if (cursor.isFirst() == false) {
     btnPrev.setVisibility(View.VISIBLE);
     }
else if (cursor.isFirst() == true){
btnPrev.setVisibility(View.INVISIBLE);
}
}

//cursor created when the day does not have ab ripper x
public void nohasRipper(){

    cursor = db.rawQuery("SELECT _id, dayID, name, exernum, type FROM p90Exercises WHERE dayID =" + dayID, null);

}

//cursor created when the day does have ab ripper x
public void hasRipper(){
cursor = db.rawQuery("SELECT _id, dayID, name, exernum, type FROM p90Exercises WHERE dayID =" + dayID + " or dayID = "+ 12, null);
}


//opens the full history view
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
   ((LinearLayout)findViewById(R.id.llStopwatch)).setVisibility(View.GONE);
   
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
   ((LinearLayout)findViewById(R.id.llStopwatch)).setVisibility(View.GONE);
}

public void showTime() {
	   ((TextView)findViewById(R.id.txtWeight)).setVisibility(View.GONE);
	   ((TextView)findViewById(R.id.txtBand)).setVisibility(View.VISIBLE);
	   ((TextView)findViewById(R.id.txtAssist)).setVisibility(View.VISIBLE);
	   ((TextView)findViewById(R.id.txtReps)).setVisibility(View.INVISIBLE);
	   ((TextView)findViewById(R.id.txtTime)).setVisibility(View.VISIBLE);
	   ((TextView)findViewById(R.id.txtWeightValue)).setVisibility(View.GONE);
	   ((TextView)findViewById(R.id.txtBandValue)).setVisibility(View.VISIBLE);
	   ((TextView)findViewById(R.id.txtAssistValue)).setVisibility(View.VISIBLE);
	   ((TextView)findViewById(R.id.txtRepsValue)).setVisibility(View.INVISIBLE);
	   ((TextView)findViewById(R.id.txtTimeValue)).setVisibility(View.VISIBLE);
	   ((LinearLayout)findViewById(R.id.llSpinners)).setVisibility(View.GONE);
	((LinearLayout)findViewById(R.id.llStopwatch)).setVisibility(View.VISIBLE);
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
   ((LinearLayout)findViewById(R.id.llStopwatch)).setVisibility(View.GONE);
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
   ((LinearLayout)findViewById(R.id.llStopwatch)).setVisibility(View.GONE);
}

public void hideStopButton(){
	((Button)findViewById(R.id.stopButton)).setVisibility(View.GONE);
	((Button)findViewById(R.id.startButton)).setVisibility(View.VISIBLE);
	((Button)findViewById(R.id.resetButton)).setVisibility(View.VISIBLE);
}

public void showStopButton(){
	((Button)findViewById(R.id.stopButton)).setVisibility(View.VISIBLE);
	((Button)findViewById(R.id.startButton)).setVisibility(View.GONE);
	((Button)findViewById(R.id.resetButton)).setVisibility(View.GONE);
}


//checking the user's equip preferences to show weights or bands
public void weightsOrBands(){
	if(equipPref.equals("Bands")){
		showBand();
	}
	else {
		showWeights();
	}
}
/*The section of code is in support of the stopwatch timer*/
//actions when clicking the start button
public void startClick (View view){
	if(stopped){
		startTime = System.currentTimeMillis() - elapsedTime; 
	}
	else{
		startTime = System.currentTimeMillis();
	}
	showStopButton();
	mHandler.removeCallbacks(startTimer);
    mHandler.postDelayed(startTimer, 0);
}
//actions when clicking the stop button
public void stopClick (View view){
	mHandler.removeCallbacks(startTimer);
	stopped = true;
	hideStopButton();
}
//actions when clicking the reset button
public void resetClick (View view){
	stopped = false;
	((TextView)findViewById(R.id.timer)).setText("00:00:00"); 	
}


private Runnable startTimer = new Runnable() {
 	   public void run() {
 		   elapsedTime = System.currentTimeMillis() - startTime;
 		   updateTimer(elapsedTime);
 	       mHandler.postDelayed(this,REFRESH_RATE);
 	   }
 	};
 	//logic for building the timer
private void updateTimer (float time){
	secs = (long)(time/1000);
	mins = (long)((time/1000)/60);
	hrs = (long)(((time/1000)/60)/60);
	
	/* Convert the seconds to String 
	 * and format to ensure it has
	 * a leading zero when required
	 */
	secs = secs % 60;
	seconds=String.valueOf(secs);
	if(secs == 0){
		seconds = "00";
	}
	if(secs <10 && secs > 0){
		seconds = "0"+seconds;
	}
	
	/* Convert the minutes to String and format the String */
	
	mins = mins % 60;
	minutes=String.valueOf(mins);
	if(mins == 0){
		minutes = "00";
	}
	if(mins <10 && mins > 0){
		minutes = "0"+minutes;
	}
	
	/* Convert the hours to String and format the String */
	
	hours=String.valueOf(hrs);
	if(hrs == 0){
		hours = "00";
	}
	if(hrs <10 && hrs > 0){
		hours = "0"+hours;
	}
	    	
	/* Setting the timer text to the elapsed time */
	((TextView)findViewById(R.id.timer)).setText(hours + ":" + minutes + ":" + seconds);
}


  /* This guy makes a dialog asking if the user wants to share their stats with their friends*/
    public void dialogShare()
    {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this); 
    	builder
    	.setMessage("Congratulations! you just finished the " + dayID + " workout!!, do you want to tell your friends?")
    	.setTitle("Share with your Friends?")
        .setCancelable(false)
        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
        	//since they said yes, we are going to show them the share dialog
			public void onClick(DialogInterface dialog, int which) {
				Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				sharingIntent.setType("text/plain");
				String URL = "https://market.android.com/details?id=com.fidotechnologies.jit";
				String shareBody = "just completed " + dayID + ", and I tracked it using UltiTrack! get it at " + URL;
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I am Awesome!!!");
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
				startActivity(Intent.createChooser(sharingIntent, "Share via"));
				finish();
				
			}
            })

         .setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
			//since they said no, we take them to the launch activity
            	
			startActivity(intent);
			}
            });
    	
    	AlertDialog alert = builder.create();
    	
    	alert.show();
    }

}
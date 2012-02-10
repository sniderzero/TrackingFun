package com.fidotechnologies.fido90tracker;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import com.fidotechnologies.fido90tracker.detailview;



public class StopwatchActivity extends Activity{
    /** Called when the activity is first created. */
	
	
	private TextView timerTextView, textName, txtTime, txtDate; 
	private Button actionBtn; 
	private Handler mHandler = new Handler();
	private long startTime;
	private long elapsedTime;
	private final int REFRESH_RATE = 100;
	private String hours,minutes,seconds,dayID;
	private long secs,mins,hrs;
	private boolean stopped = false;
	protected SQLiteDatabase db;
	Cursor cursor_user;
	Intent intent;
	ListAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.stopwatch);
                
    	//building UI widgets   	  
        textName = (TextView) findViewById(R.id.textName);
        txtTime = (TextView) findViewById(R.id.txtTime);
        txtDate = (TextView) findViewById(R.id.txtDate);
    	timerTextView = (TextView) findViewById(R.id.timer); 
    	actionBtn = (Button)findViewById(R.id.startButton);
    	actionBtn = (Button)findViewById(R.id.resetButton);
    	actionBtn = (Button)findViewById(R.id.stopButton);
    	actionBtn = (Button)findViewById(R.id.btnHist);
    	//creating an intent to call when finished
    	intent = new Intent(StopwatchActivity.this, launch.class);
    	//grabbing passed variable
    	dayID = getIntent().getStringExtra("PROGRAM_DAY");
    	//setting header value
    	textName.setText(dayID);
    	//opening db
    	db = (new DBHelper(this)).getWritableDatabase();
    	cursor_user = db.rawQuery("SELECT _id, ExerciseName, date, time FROM results WHERE ExerciseName = " + "'" + textName.getText().toString() +"'", null);
    	// setting values for user stats
    	fillUserStats();
    	
    	
    }
        
    @Override
    public void onDestroy() {
      super.onDestroy();
    }
    
    //actions when clicking the start button
    public void startClick (View view){
    	if(stopped){
    		startTime = System.currentTimeMillis() - elapsedTime; 
    	}
    	else{
    		startTime = System.currentTimeMillis();
    	}
    	mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
    }
    //actions when clicking the stop button
    public void stopClick (View view){
    	mHandler.removeCallbacks(startTimer);
    	stopped = true;
    	((Button)findViewById(R.id.saveButton)).setVisibility(View.VISIBLE);
    }
    //actions when clicking the reset button
    public void resetClick (View view){
    	stopped = false;
    	((TextView)findViewById(R.id.timer)).setText("00:00:00"); 	
    }
    //actions when clicking the save button
    public void saveClick (View view){
    	String content = timerTextView.getText().toString();
    	String strDate = detailview.getDate();
    	db.execSQL("INSERT INTO results (ExerciseName,time,date) VALUES("+ "'" + dayID +"'" + "," 
    			+ "'" + content + "'" + ","+ "'" + strDate +"'"+ ")");
    	db.close();
    	dialogShare();
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

	  public void fillUserStats(){
		  	//changing the textviews to show the users last results
		  			  	
		  	if (cursor_user.getCount()==0){
		  		txtTime.setText("0");
		  		txtDate.setText("0");

		  	}
		  	else{
		  		cursor_user.moveToLast();
		  		txtTime.setText(cursor_user.getString(3));
		  		txtDate.setText(cursor_user.getString(2));
		  	}
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
	    
public void clickHistory(View v){
	Intent intent = new Intent(StopwatchActivity.this, HistoryView.class);
	intent.putExtra("EXERCISE_NAME", dayID);
	intent.putExtra("EXERCISE_TYPE", 3);
	startActivity(intent);
}

}
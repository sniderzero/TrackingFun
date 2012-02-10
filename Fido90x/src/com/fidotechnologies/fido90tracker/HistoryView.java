package com.fidotechnologies.fido90tracker;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;





public class HistoryView extends Activity{
	
	ListView lstHistory;
	SQLiteDatabase db;
	Cursor cursor;
	String exerName;
	ListAdapter adapter;
	TextView lblHistory;
	Integer exerType;
	
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.historyview);
        //declare widgets
        lstHistory = (ListView)findViewById(R.id.lstHistory);
        lblHistory = (TextView)findViewById(R.id.lblHistory);
        
        
        //open db connection
        db = (new DBHelper(this)).getWritableDatabase();
        //bring in variables from previous activity
        exerName = getIntent().getStringExtra("EXERCISE_NAME");
        exerType = getIntent().getIntExtra("EXERCISE_TYPE", 0);
        Toast toast = Toast.makeText(this, exerType.toString(), 5000);
        toast.show();


        //set label to exercisename
        lblHistory.setText(exerName);
        //search db based on variable
        cursor = db.rawQuery("SELECT _id, ExerciseName, date, time, weight, reps, band, pullup FROM results WHERE ExerciseName = " + "'" + exerName +"'", null);
        //set cursor an an adapter
    	/*adapter = new SimpleCursorAdapter(
				this, 
				R.layout.historyrow, 
				cursor, 
				new String[] {"time", "date", "weight", "reps", "band", "pullup"}, 
				new int[] {R.id.txtTime, R.id.txtDate, R.id.txtWeight, R.id.txtReps, R.id.txtBand, R.id.txtAssist}); */
		lstHistory.setAdapter(new adapter(this,cursor));
		//determine what to show
        whatDisplay();
		
		
}
	public void clickClose(View v){
		finish();
	}
	
	public void whatDisplay(){
		switch(exerType){
		case 1:
			showReps();
			break;
		case 2:
			showWeight();
			break;
		case 3:
			showTime();
			break;
		case 4:
			showAssist();
			break;
		}
	}
	
	public class adapter extends CursorAdapter{  
	    private Cursor mCursor;  
	    private Context mContext;  
	    LayoutInflater mInflater;  
	  
	    public adapter(Context context, Cursor cursor) {  
	      super(context, cursor, true);  
	      mInflater = LayoutInflater.from(context);  
	      mContext = context;  
	    }  
	  
	    @Override  
	    public void bindView(View view, Context context, Cursor cursor) {  
	      TextView t = (TextView) view.findViewById(R.id.txtTime);  
	      t.setText(cursor.getString(cursor.getColumnIndex("time")));  
	  
	      t = (TextView) view.findViewById(R.id.txtDate);  
	      t.setText(cursor.getString(cursor.getColumnIndex("date")));
	      	  
	      t = (TextView) view.findViewById(R.id.txtWeight);  
	      t.setText(cursor.getString(cursor.getColumnIndex("weight")));  
	      
	      t = (TextView) view.findViewById(R.id.txtAssist);  
	      t.setText(cursor.getString(cursor.getColumnIndex("pullup")));
	      
	      t = (TextView) view.findViewById(R.id.txtReps);  
	      t.setText(cursor.getString(cursor.getColumnIndex("reps")));
	      
	      t = (TextView) view.findViewById(R.id.txtBand);  
	      t.setText(cursor.getString(cursor.getColumnIndex("band")));
	      
			
	      switch(exerType){
	      case 3:
			((TextView)view.findViewById(R.id.txtTime)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.txtReps)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtWeight)).setVisibility(View.GONE); 
			((TextView)view.findViewById(R.id.txtBand)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtAssist)).setVisibility(View.GONE);
			break;
			
	      case 1:
			((TextView)view.findViewById(R.id.txtTime)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtReps)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.txtWeight)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtBand)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtAssist)).setVisibility(View.GONE);
			break;
			
	      case 2:
			((TextView)view.findViewById(R.id.txtTime)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtReps)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.txtWeight)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.txtBand)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtAssist)).setVisibility(View.GONE);
			break;
	      
	      case 5:
			((TextView)view.findViewById(R.id.txtTime)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtReps)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.txtWeight)).setVisibility(View.GONE);
			((TextView)view.findViewById(R.id.txtBand)).setVisibility(View.VISIBLE);
			((TextView)view.findViewById(R.id.txtAssist)).setVisibility(View.GONE);
			break;
			
	      case 4:
			((TextView)findViewById(R.id.txtTime)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.txtReps)).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.txtWeight)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.txtBand)).setVisibility(View.GONE);
			((TextView)findViewById(R.id.txtAssist)).setVisibility(View.VISIBLE);
	      break;
	      }
	      
	      }  
	  
	    @Override  
	    public View newView(Context context, Cursor cursor, ViewGroup parent) {  
	      final View view = mInflater.inflate(R.layout.historyrow, parent, false);  
	      return view;  
	    }  
	  }  
	
	

	
	public void showTime(){
		
		((TextView)findViewById(R.id.lblTime)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblReps)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblWeight)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblBand)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblAssist)).setVisibility(View.GONE);
				
	}
	
	public void showReps(){
		((TextView)findViewById(R.id.lblTime)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblReps)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblWeight)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblBand)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblAssist)).setVisibility(View.GONE);

	}
	
	public void showWeight(){
		((TextView)findViewById(R.id.lblTime)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblReps)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblWeight)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblBand)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblAssist)).setVisibility(View.GONE);

	}
	
	public void showBand(){
		((TextView)findViewById(R.id.lblTime)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblReps)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblWeight)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblBand)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblAssist)).setVisibility(View.GONE);

	}
	
	public void showAssist(){
		((TextView)findViewById(R.id.lblTime)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblReps)).setVisibility(View.VISIBLE);
		((TextView)findViewById(R.id.lblWeight)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblBand)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.lblAssist)).setVisibility(View.VISIBLE);

	}
}
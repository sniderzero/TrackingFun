package com.fidotechnologies.ultitrack90;

import com.fidotechnologies.ultitrack90.R;
import com.fidotechnologies.ultitrack90.HistoryView.adapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class programlist extends Activity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected String programID;
	protected ListView lstDays;
	protected int type;
	SharedPreferences preferences;
	TextView lblHeader;
	Typeface font;
	
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
		
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.programlist);
        lstDays = (ListView)findViewById(R.id.list);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        font = Typeface.createFromAsset(getAssets(), "font.otf");
        lblHeader = (TextView)findViewById(R.id.lblHeader);
        lblHeader.setTypeface(font);
    	//open db
        db = (new DBHelper(this)).getWritableDatabase();
    	//grab user track preference
        programID =  preferences.getString("trackType", "Lean");
    	// query db based on that variable
    	cursor = db.rawQuery("SELECT dayname, _id, daynumber, type, track, dayID, hasRipper, date " +
    			"FROM p90days WHERE track =" + "'" + programID +"'", null);
		lstDays.setAdapter(new adapter(this,cursor));

		
		
		
			
 lstDays.setOnItemClickListener(
	new OnItemClickListener()
			{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			type = cursor.getInt(3);
			if(type == 2)
				{
					Intent intent = new Intent(programlist.this, exerciselist.class);
					//Cursor cursor2 = (Cursor) adapter.getItem(position);
					intent.putExtra("PROGRAM_DAY", cursor.getInt(5));
					intent.putExtra("HAS_RIPPER", cursor.getInt(6));
					intent.putExtra("DAY_ID", cursor.getInt(1));
					intent.putExtra("DAY_NAME", cursor.getString(0));
					db.close();
					startActivity(intent);
				}
				else { 
					Intent intent = new Intent(programlist.this, StopwatchActivity.class);
					intent.putExtra("DAY_NAME", cursor.getString(0));
					intent.putExtra("DAY_ID", cursor.getInt(1));
					db.close();
					startActivity(intent);
				 	}
																								}

			});
	
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
	      TextView t = (TextView)view.findViewById(R.id.name);  
	      t.setText(cursor.getString(cursor.getColumnIndex("dayname")));
	  
	      TextView t1 = (TextView)view.findViewById(R.id.daynum);  
	      t1.setText(cursor.getString(cursor.getColumnIndex("daynumber")));
	      	  
	      TextView t2 = (TextView)view.findViewById(R.id.lblDAY);  
	      
	      
	      t.setTypeface(font);
	      t1.setTypeface(font);
	      t2.setTypeface(font);
	      
	      if(cursor.isNull(7)){
	    	  ((ImageView)view.findViewById(R.id.imgCheck)).setVisibility(View.INVISIBLE);
	      }
	      else{
	    	  ((ImageView)view.findViewById(R.id.imgCheck)).setVisibility(View.VISIBLE);
	      }
	      }  
	  
	    @Override  
	    public View newView(Context context, Cursor cursor, ViewGroup parent) {  
	      final View view = mInflater.inflate(R.layout.row, parent, false);  
	      return view;  
	    }  
	  }  
	
	
}





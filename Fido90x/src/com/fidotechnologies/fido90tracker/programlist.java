package com.fidotechnologies.fido90tracker;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;


public class programlist extends Activity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected String programID;
	protected ListView lstDays;
	protected int type;
	
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
		
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.programlist);
        lstDays = (ListView)findViewById(R.id.list);
    	//open db
        db = (new DBHelper(this)).getWritableDatabase();
    	//grab variable passed from previous activity
        programID = getIntent().getStringExtra("PROGRAM_NAME");
    	// query db based on that variable
    	cursor = db.rawQuery("SELECT _id, track, dayname, weeknumber, daynumber, type FROM p90days WHERE track = " + "'" + programID +"'", null);
    	// set ListView to results
    	adapter = new SimpleCursorAdapter(
				this, 
				R.layout.row, 
				cursor, 
				new String[] {"dayname", "daynumber"}, 
				new int[] {R.id.name, R.id.programname});
		lstDays.setAdapter(adapter);
	

			
 lstDays.setOnItemClickListener(
	new OnItemClickListener()
			{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			type = cursor.getInt(5);
			if(type == 2)
				{
				Toast.makeText(getBaseContext(), cursor.getString(2), 2000).show();	
				Intent intent = new Intent(programlist.this, exerciselist.class);
					//Cursor cursor2 = (Cursor) adapter.getItem(position);
					intent.putExtra("PROGRAM_DAY", cursor.getString(2));
					//db.close();
					startActivity(intent);
				}
				else { 
					Toast.makeText(getBaseContext(), cursor.getString(5), 2000).show();
					Intent intent = new Intent(programlist.this, StopWatch.class);
					startActivity(intent);
				 	}
																								}

			});
	
}
}





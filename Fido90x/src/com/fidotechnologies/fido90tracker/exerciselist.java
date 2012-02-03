package com.fidotechnologies.fido90tracker;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;


public class exerciselist extends Activity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected String dayID;
	protected Integer curPos;
	protected ListView lstExer;
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.programlist);
        lstExer = (ListView)findViewById(R.id.list);
        //open db
        db = (new DBHelper(this)).getWritableDatabase();
    	//grab variable passed from previous activity
        dayID = getIntent().getStringExtra("PROGRAM_DAY");
    	// query db based on that variable
    	cursor = db.rawQuery("SELECT _id, day, name, exernum, type FROM p90exercises WHERE day = " + "'" + dayID +"'", null);
    	db.close();
    	// set ListView to results
    	adapter = new SimpleCursorAdapter(
				this, 
				R.layout.row, 
				cursor, 
				new String[] {"name"}, 
				new int[] {R.id.name});
		lstExer.setAdapter(adapter);
		
    
	//this is what we do when we select an item from our new list
	 lstExer.setOnItemClickListener(
				new OnItemClickListener()
						{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	Intent intent = new Intent(exerciselist.this, detailview.class);
    	Cursor cursor = (Cursor) adapter.getItem(position);
    	curPos = cursor.getPosition();
    	intent.putExtra("int", curPos);
    	intent.putExtra("PROGRAM_DAY", dayID);
    	startActivity(intent);
						}
							
						});
}
}
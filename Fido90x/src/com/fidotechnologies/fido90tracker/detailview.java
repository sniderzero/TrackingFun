package com.fidotechnologies.fido90tracker;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class detailview extends Activity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected String dayID;
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.detailview);
    	//open db
        db = (new DBHelper(this)).getWritableDatabase();
    	//grab variable passed from previous activity
        dayID = getIntent().getStringExtra("PROGRAM_DAY");
    	// query db based on that variable
    	cursor = db.rawQuery("SELECT _id, day, name FROM nintyexer WHERE day = " + "'" + dayID +"'", null);
    	// set ListView to results
    	adapter = new SimpleCursorAdapter(
				this, 
				R.layout.row, 
				cursor, 
				new String[] {"name", "day"}, 
				new int[] {R.id.name, R.id.programname});
		//setListAdapter(adapter);
    }}
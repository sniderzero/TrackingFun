package com.fidotechnologies.fido90tracker;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class programlist extends ListActivity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected String programID;
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.programlist);
    	//open db
        db = (new DBHelper(this)).getWritableDatabase();
    	//grab variable passed from previous activity
        programID = getIntent().getStringExtra("PROGRAM_NAME");
    	// query db based on that variable
    	cursor = db.rawQuery("SELECT _id, day, track, daynumber FROM wodays WHERE track = " + "'" + programID +"'", null);
    	// set ListView to results
    	adapter = new SimpleCursorAdapter(
				this, 
				R.layout.row, 
				cursor, 
				new String[] {"day", "daynumber"}, 
				new int[] {R.id.name, R.id.programname});
		setListAdapter(adapter);
    }
	//this is what we do when we select an item from our new list
    public void onListItemClick(ListView parent, View view, int position, long id) {
    	Intent intent = new Intent(this, exerciselist.class);
    	Cursor cursor = (Cursor) adapter.getItem(position);
    	intent.putExtra("PROGRAM_DAY", cursor.getString(cursor.getColumnIndex("day")));
    	db.close();
    	startActivity(intent);
    }
}

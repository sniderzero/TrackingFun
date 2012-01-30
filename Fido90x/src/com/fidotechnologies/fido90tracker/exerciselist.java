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


public class exerciselist extends ListActivity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected String dayID;
	protected Integer curPos;
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.programlist);
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
				new String[] {"name"}, 
				new int[] {R.id.name});
		setListAdapter(adapter);
		db.close();
    }
	//this is what we do when we select an item from our new list
    public void onListItemClick(ListView parent, View view, int position, long id) {
    	Intent intent = new Intent(this, detailview.class);
    	Cursor cursor = (Cursor) adapter.getItem(position);
    	curPos = cursor.getPosition();
    	intent.putExtra("int", curPos);
    	intent.putExtra("PROGRAM_DAY", dayID);
    	startActivity(intent);
    }
}

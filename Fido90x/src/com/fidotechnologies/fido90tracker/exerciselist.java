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
	protected int dayID,hasRIP;
	protected Integer curPos;
	protected ListView lstExer;
	protected int type;
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.exerciselist);
        lstExer = (ListView)findViewById(R.id.list);
        //open db
        db = (new DBHelper(this)).getWritableDatabase();
    	//grab variable passed from previous activity
        dayID = getIntent().getIntExtra("PROGRAM_DAY", 0);
        hasRIP = getIntent().getIntExtra("HAS_RIPPER", 1);
    	// query db based on that variable
        if (hasRIP == 0){
        	nohasRipper();
        }
        else{
        	hasRipper();
        }
        //db.close();
    	// set ListView to results
    	adapter = new SimpleCursorAdapter(
				this, 
				R.layout.exerrow, 
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
								type = cursor.getInt(3);
								Intent intent = new Intent(exerciselist.this, detailview.class);
								Cursor cursor2 = (Cursor) adapter.getItem(position);
								curPos = cursor2.getPosition();
								intent.putExtra("int", curPos);
								intent.putExtra("PROGRAM_DAY", dayID);
								intent.putExtra("EXER_TYPE", type);
								intent.putExtra("HAS_RIPPER", hasRIP);
								startActivity(intent);
						}
							
						});
}
public void nohasRipper(){
	cursor = db.rawQuery("SELECT _id, dayID, name, type " +
			"FROM p90Exercises WHERE dayID =" + "'" + dayID +"'", null);
}
public void hasRipper(){
	cursor = db.rawQuery("SELECT _id, dayID, name, type " +
			"FROM p90Exercises WHERE dayID =" + 12 + " OR dayID = "  + dayID , null);
}
}
package com.fidotechnologies.fido90tracker;



import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class programlist extends ListActivity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected String programID;
	@Override
	
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.programlist);
    	db = (new DBHelper(this)).getWritableDatabase();
    	programID = getIntent().getStringExtra("PROGRAM_NAME");
    	
    	cursor = db.rawQuery("SELECT _id, day, track FROM wodays WHERE track = " + "'" + programID +"'", null);
    	
    	Toast.makeText(getBaseContext(), programID ,Toast.LENGTH_LONG).show();
		
    	
    	adapter = new SimpleCursorAdapter(
				this, 
				R.layout.row, 
				cursor, 
				new String[] {"day", "track"}, 
				new int[] {R.id.name, R.id.programname});
		setListAdapter(adapter);
    }
}

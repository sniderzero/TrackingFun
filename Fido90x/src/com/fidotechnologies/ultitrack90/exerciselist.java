package com.fidotechnologies.ultitrack90;

import com.fidotechnologies.ultitrack90.R;
import com.fidotechnologies.ultitrack90.programlist.adapter;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;


public class exerciselist extends Activity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	protected int dayID,hasRIP,dayID2;
	String dayName;
	protected Integer curPos;
	protected ListView lstExer;
	protected int type;
	Typeface font;
	TextView lblHeader;
	@Override
	// on open 
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.exerciselist);
        lstExer = (ListView)findViewById(R.id.list);
        //open db
        db = (new DBHelper(this)).getWritableDatabase();
        //set font
        font = Typeface.createFromAsset(getAssets(), "font.otf");
        lblHeader = (TextView)findViewById(R.id.lblHeader);
        lblHeader.setTypeface(font);
    	//grab variable passed from previous activity
        dayID = getIntent().getIntExtra("PROGRAM_DAY", 0);
        hasRIP = getIntent().getIntExtra("HAS_RIPPER", 1);
        dayID2 = getIntent().getIntExtra("DAY_ID", 1);
        dayName = getIntent().getStringExtra("DAY_NAME");
    	// query db based on that variable
        if (hasRIP == 0){
        	nohasRipper();
        }
        else{
        	hasRipper();
        }
        
    	// set ListView to results

		lstExer.setAdapter(new adapter(this,cursor));
		db.close();
    
	//this is what we do when we select an item from our new list
	 lstExer.setOnItemClickListener(
				new OnItemClickListener()
						{
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								type = cursor.getInt(3);
								Intent intent = new Intent(exerciselist.this, detailview.class);
								//Cursor cursor2 = (Cursor) adapter.getItem(position);
								curPos = cursor.getPosition();
								intent.putExtra("int", curPos);
								intent.putExtra("PROGRAM_DAY", dayID);
								intent.putExtra("EXER_TYPE", type);
								intent.putExtra("HAS_RIPPER", hasRIP);
								intent.putExtra("DAY_ID", dayID2);
								intent.putExtra("DAY_NAME", dayName);
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
      t.setText(cursor.getString(cursor.getColumnIndex("name")));
      	  
      t.setTypeface(font);
      
      }  
  
    @Override  
    public View newView(Context context, Cursor cursor, ViewGroup parent) {  
      final View view = mInflater.inflate(R.layout.exerrow, parent, false);  
      return view;  
    }  
  }  

public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options, menu);
    return true;
  }

public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.preferences:
        Intent in = new Intent(this, AppPreferences.class);
        startActivity(in);
          return true;
    default:
          return super.onOptionsItemSelected(item);
    }

}

}
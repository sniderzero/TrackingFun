package com.fidotechnologies.fido90tracker;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;




public class launch extends Activity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
      db = (new DBHelper(this)).getWritableDatabase();

        cursor = db.rawQuery("SELECT _id, name, program FROM programs", null);
        adapter = new SimpleCursorAdapter(
    			this, 
    			R.layout.progrow, 
    			cursor, 
    			new String[] {"name","program"}, 
    			new int[] {R.id.name,R.id.progname });
        ;
    	}
    
    
	
public void programSelect (View view)
{
	
	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Select Program");
	builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
		
                public void onClick(DialogInterface dialog,
                        int position) {
                
                Cursor cursor = (Cursor) adapter.getItem(position);
                Intent in = new Intent(launch.this, programlist.class);
                in.putExtra("PROGRAM_NAME", cursor.getString(cursor.getColumnIndex("name")));
                //start activity
				startActivity(in); 
                dialog.dismiss();
                db.close();
        }
});
	
	AlertDialog alert = builder.create();
	alert.show(); 
}

public void preferenceSelect (View view)
{

                Intent in = new Intent(launch.this, AppPreferences.class);
               
                startActivity(in); 

}

}
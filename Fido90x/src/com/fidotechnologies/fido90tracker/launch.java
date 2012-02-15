package com.fidotechnologies.fido90tracker;




import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;




public class launch extends Activity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	SharedPreferences preferences;
	Spinner spnTrack, spnEquip;
	String strTrack, strEquip, strUserName;
	SpinnerAdapter adapter_track, adapter_equip;
	TextView txtName;
	Button btnBringIt, btnPreferences;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        txtName = (TextView)findViewById(R.id.lblHeaderMain);
        btnBringIt = (Button)findViewById(R.id.btnBringIt);
        btnPreferences = (Button)findViewById(R.id.btnPreferences);
        Typeface font = Typeface.createFromAsset(getAssets(), "font.otf");
        txtName.setTypeface(font);
        btnBringIt.setTypeface(font);
        btnPreferences.setTypeface(font);
        
        db = (new DBHelper(this)).getWritableDatabase();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        cursor = db.rawQuery("SELECT _id, name, program FROM programs", null);
        adapter = new SimpleCursorAdapter(
    			this, 
    			R.layout.progrow, 
    			cursor, 
    			new String[] {"name","program"}, 
    			new int[] {R.id.name,R.id.progname });
        ;
    	}
    
    
public void clickBringIt(View v){
	boolean firstrun = preferences.getBoolean("firstrun", false);
	if(firstrun == true){
    	Intent in = new Intent(launch.this, programlist.class);
    	startActivity(in);
	}
	else {
		preferenceSelect();
	}
}
 
	
public void preferenceSelect ()
{
    Dialog dialog = new Dialog(launch.this);
    dialog.setContentView(R.layout.firstrundialog);
    dialog.setTitle("Set Your Preferences");
    dialog.setCancelable(true);
    //set up spinners
    ArrayAdapter<CharSequence> adapter_track = ArrayAdapter.createFromResource(
    this, R.array.listTrack, android.R.layout.simple_spinner_item);
    adapter_track.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
    spnTrack = (Spinner) dialog.findViewById(R.id.spnTrack);
    spnTrack.setAdapter(adapter_track);
    
    ArrayAdapter<CharSequence> adapter_equip = ArrayAdapter.createFromResource(
    this, R.array.listEquip, android.R.layout.simple_spinner_item );
    adapter_equip.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
    spnEquip = (Spinner) dialog.findViewById(R.id.spnEquip);
    spnEquip.setAdapter(adapter_equip);
    //set up button
    Button button = (Button) dialog.findViewById(R.id.btnStart);
    button.setOnClickListener(new OnClickListener() {
    @Override
        public void onClick(View v) {
    	Editor edit = preferences.edit();
    	strTrack = spnTrack.getItemAtPosition((int) spnTrack.getSelectedItemId()).toString();
    	strEquip = spnEquip.getItemAtPosition((int) spnEquip.getSelectedItemId()).toString();
    	edit.putString("trackType", strTrack);
    	edit.putString("equipmentType", strEquip);
    	edit.putBoolean("firstrun", true);
    	edit.commit();
    	Intent in = new Intent(launch.this, programlist.class);
    	startActivity(in);
    	finish();
        }
    });
    
    
    dialog.show();
	
}

public void preferenceSelect (View view)
{

                Intent in = new Intent(launch.this, AppPreferences.class);
               
                startActivity(in); 

}

}
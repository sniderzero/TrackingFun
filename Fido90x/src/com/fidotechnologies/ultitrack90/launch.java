package com.fidotechnologies.ultitrack90;




import com.fidotechnologies.ultitrack90.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;




public class launch extends Activity {
	
	
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected ListAdapter adapter;
	SharedPreferences preferences;
	Spinner spnTrack, spnEquip;
	String strTrack, strEquip, strUserName, strRuns, url;
	SpinnerAdapter adapter_track, adapter_equip;
	TextView txtName;
	Button btnBringIt;
	Integer intRuns;
	Editor edit;
	Boolean bnFeedback, bnURLTest;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        txtName = (TextView)findViewById(R.id.lblHeaderMain);
        btnBringIt = (Button)findViewById(R.id.btnBringIt);
        Typeface font = Typeface.createFromAsset(getAssets(), "font.otf");
        txtName.setTypeface(font);
        btnBringIt.setTypeface(font);
        
        //open preferences
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //declare preference editor
        edit = preferences.edit();
        //grabbing number or runs preference
        strRuns = preferences.getString("runnumber", "0");
        //grabbing if feedback has been given boolean
        bnFeedback = preferences.getBoolean("feedbackgiven", false);
        //converting it to Integer
        intRuns = Integer.valueOf(strRuns);
        //adding one everytime the user runs the app
        intRuns = intRuns+1;
        //converting back to a string
        strRuns = String.valueOf(intRuns);
        //storing in preferences
        edit.putString("runnumber", strRuns);
        edit.commit();
        
        newDialog();
        
       //check if the user has given feedback
        if(bnFeedback == false){
        
        //check if the number of runs is 5/10/15
       
        if(intRuns ==  10 || intRuns == 15 || intRuns == 25){
        	//ask for feedback if it is
        	
            final Dialog dialog = new Dialog(launch.this);
            dialog.setContentView(R.layout.rateappdialog);
            dialog.setTitle("Will you rate UltiTrack90?");
            dialog.setCancelable(true);
            //declare dialog buttons
            Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
            Button btnLater = (Button) dialog.findViewById(R.id.btnLater);
            Button btnNothanks = (Button) dialog.findViewById(R.id.btnNothanks);
            btnYes.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
            	edit.putBoolean("feedbackgiven", true);
            	edit.commit();
            	rateApp();
            	dialog.dismiss();
                }
            });
            btnLater.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
            	dialog.dismiss();
                }
            });
            btnNothanks.setOnClickListener(new OnClickListener() {
            @Override
                public void onClick(View v) {
            	edit.putBoolean("feedbackgiven", true);
            	edit.commit();
            	dialog.dismiss();
                }
            });
        	dialog.show();

        }
        } 
        
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
    
    
public void clickBringIt(View v){
	boolean firstrun = preferences.getBoolean("firstrun", false);
	if(firstrun == true){
		db.close();
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
    	db.close();
        }
    });
    
    
    dialog.show();
	
}
//function for rating app
public void rateApp(){
	Intent intent = new Intent(Intent.ACTION_VIEW);
	//intent.setData(Uri.parse("market://details?id=com.fidotechnologies.ultitrack90"));  //PLAY STORE
	intent.setData(Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=com.fidotechnologies.ultitrack90"));  //AMAZON APP STORE
	//intent.setData(Uri.parse("http://www.barnesandnoble.com/u/NOOK-Apps/379003212"));  //NOOK STORE	
	startActivity(intent);
}

//function for checking if intent is available
public static boolean isUriAvailable(Context context, String uri) {
    Intent test = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
    return context.getPackageManager().resolveActivity(test, 0) != null;
}


public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options, menu);
    return true;
  }

public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.preferences:
        Intent in = new Intent(launch.this, AppPreferences.class);
        startActivity(in);
          return true;
    default:
          return super.onOptionsItemSelected(item);
    }

}
public void newDialog(){
	final Dialog dialog = new Dialog(launch.this);
    dialog.setContentView(R.layout.upgradedialog);
    dialog.setTitle("Update Time!!");
    dialog.setCancelable(true);
    //declare dialog buttons
    Button btnYes = (Button) dialog.findViewById(R.id.btnYes);
    Button btnNothanks = (Button) dialog.findViewById(R.id.btnNothanks);
    btnYes.setOnClickListener(new OnClickListener() {
    @Override
        public void onClick(View v) {
    	Intent intent = new Intent(Intent.ACTION_VIEW);
    	intent.setData(Uri.parse("market://details?id=com.appsmarttech.ut90")); //play store
    		//intent.setData(Uri.parse("amzn://apps/android?p=com.appsmarttech.ut90")); //amazon app store
    	startActivity(intent);
    	dialog.dismiss();
        }
    });
    btnNothanks.setOnClickListener(new OnClickListener() {
    @Override
        public void onClick(View v) {
    	
    	dialog.dismiss();
        }
    });
	dialog.show();

}

}
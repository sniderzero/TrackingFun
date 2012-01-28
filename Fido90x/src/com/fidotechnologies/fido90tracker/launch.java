package com.fidotechnologies.fido90tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class launch extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        }
    
public void programSelect (View v)
{
	final CharSequence[] items = {"Classic","Cardio", "Doubles"};

	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	builder.setTitle("Select Program");
	builder.setItems(items, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int item) {
	       // Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
	       // Intent in = new Intent(jitapp.this, webview.class);
	      //  in.putExtra(bar, items[item]);
			//startActivity(in);
	    }});
	AlertDialog alert = builder.create();
	alert.show();
}
}
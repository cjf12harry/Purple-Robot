package edu.northwestern.cbits.purple_robot_manager;

import java.util.ArrayList;

import edu.northwestern.cbits.purple_robot_manager.scripting.BaseScriptEngine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class DialogActivity extends Activity
{
	public static String DIALOG_MESSAGE = "dialog_message";
	public static String DIALOG_TITLE = "dialog_title";
	public static String DIALOG_CONFIRM_BUTTON = "dialog_confirm";
	public static String DIALOG_CANCEL_BUTTON= "dialog_cancel";

	public static String DIALOG_CONFIRM_SCRIPT = "dialog_confirm_script";
	public static String DIALOG_CANCEL_SCRIPT = "dialog_cancel_script";
	
	private static ArrayList<DialogActivity> _dialogStack = new ArrayList<DialogActivity>();

	protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.layout_dialog_activity);

        getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        
        while (DialogActivity._dialogStack.size() > 4)
        {
        	DialogActivity activity = DialogActivity._dialogStack.remove(0);
        	
        	activity.finish();
        }
        
        DialogActivity._dialogStack.add(this);
    }
	
	protected void onStop()
	{
		super.onStop();
		
		DialogActivity._dialogStack.remove(this);
	}

	protected void onResume()
	{
		super.onResume();

		final DialogActivity me = this;

        final TextView messageText = (TextView) this.findViewById(R.id.text_dialog_message);

        Intent intent = this.getIntent();

        String title = intent.getStringExtra(DialogActivity.DIALOG_TITLE);
        String message = intent.getStringExtra(DialogActivity.DIALOG_MESSAGE);

        final String confirmScript = intent.getStringExtra(DialogActivity.DIALOG_CONFIRM_SCRIPT);

        this.setTitle(title);

        messageText.setText(message);

        Button confirmButton = (Button) this.findViewById(R.id.button_dialog_confirm);
        confirmButton.setText(intent.getStringExtra(DialogActivity.DIALOG_CONFIRM_BUTTON));

        confirmButton.setOnClickListener(new OnClickListener()
        {
			public void onClick(View v)
			{
				if (confirmScript != null && confirmScript.length() >= 0)
				{
					try
					{
						BaseScriptEngine.runScript(me, confirmScript);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				me.finish();
			}
        });

        Button cancelButton = (Button) this.findViewById(R.id.button_dialog_cancel);

        if (intent.hasExtra(DialogActivity.DIALOG_CANCEL_BUTTON))
        {
            cancelButton.setText(intent.getStringExtra(DialogActivity.DIALOG_CANCEL_BUTTON));

            if (intent.hasExtra(DialogActivity.DIALOG_CANCEL_SCRIPT))
            {
	            final String cancelScript = intent.getStringExtra(DialogActivity.DIALOG_CANCEL_SCRIPT);
	
	            cancelButton.setOnClickListener(new OnClickListener()
	            {
	    			public void onClick(View v)
	    			{
	    				if (cancelScript != null && cancelScript.length() >= 0)
	    				{
	    					try
	    					{
	    						BaseScriptEngine.runScript(me, cancelScript);
	    					}
	    					catch (Exception e)
	    					{
	    						e.printStackTrace();
	    					}
	    				}
	    				
	    				me.finish();
	    			}
	            });
            }

            cancelButton.setVisibility(View.VISIBLE);
        }
        else
            cancelButton.setVisibility(View.GONE);
        	
    }
}

package edu.northwestern.cbits.purple_robot_manager.triggers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jsint.Pair;
import jsint.Symbol;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import edu.northwestern.cbits.purple_robot_manager.R;
import edu.northwestern.cbits.purple_robot_manager.SettingsActivity;

public class TriggerManager 
{
	private static final TriggerManager _instance = new TriggerManager();
    
	private List<Trigger> _triggers = new ArrayList<Trigger>();
	
    private TriggerManager() 
    {
        if (TriggerManager._instance != null)
            throw new IllegalStateException("Already instantiated");
    }

    public static TriggerManager getInstance() 
    {
        return TriggerManager._instance;
    }

	public void nudgeTriggers(Context context)
	{
		Date now = new Date();
		
		synchronized(this._triggers)
		{
			for (Trigger trigger : this._triggers)
			{
				boolean execute = false;
	
				if (trigger instanceof DateTrigger)
				{
					if (trigger.matches(context, now))
						execute = true;
				}
				
				if (execute)
					trigger.execute(context);
			}
		}
	}

	public void updateTriggers(List<Trigger> triggerList)
	{
		ArrayList<Trigger> toAdd = new ArrayList<Trigger>();
		
		synchronized(this._triggers)
		{
			for (Trigger newTrigger : triggerList)
			{
				boolean found = false;
				
				for (Trigger trigger : this._triggers)
				{
					if (trigger.equals(newTrigger))
					{
						trigger.merge(newTrigger);
						
						found = true;
					}
				}
				
				if (!found)
					toAdd.add(newTrigger);
			}
		
			this._triggers.addAll(toAdd);
		}
	}

	public List<Trigger> triggersForId(String triggerId) 
	{
		ArrayList<Trigger> matches = new ArrayList<Trigger>();
		
		synchronized(this._triggers)
		{
			for (Trigger trigger : this._triggers)
			{
				if (trigger.identifier().equals(triggerId))
					matches.add(trigger);
			}
		}		
		return matches;
	}

	public List<Trigger> allTriggers() 
	{
		return this._triggers;
	}

	public void addTrigger(Trigger t) 
	{
		ArrayList<Trigger> ts = new ArrayList<Trigger>();
		
		ts.add(t);
		
		this.updateTriggers(ts);
	}

	public void removeAllTriggers() 
	{
		synchronized(this._triggers)
		{
			this._triggers.clear();
		}
	}
	
	public static PreferenceScreen buildPreferenceScreen(PreferenceActivity settingsActivity)
	{
		PreferenceManager manager = settingsActivity.getPreferenceManager();

		PreferenceScreen screen = manager.createPreferenceScreen(settingsActivity);
		screen.setOrder(0);
		screen.setTitle(R.string.title_preference_triggers_screen);
		screen.setKey(SettingsActivity.TRIGGERS_SCREEN_KEY);

		PreferenceCategory triggersCategory = new PreferenceCategory(settingsActivity);
		triggersCategory.setTitle(R.string.title_preference_triggers_category);
		triggersCategory.setKey("key_available_triggers");

		screen.addPreference(triggersCategory);

		for (Trigger trigger : TriggerManager.getInstance().allTriggers())
		{
			PreferenceScreen triggerScreen = trigger.preferenceScreen(settingsActivity);

			if (triggerScreen != null)
				screen.addPreference(triggerScreen);
		}

		return screen;
	}

	public Pair schemePairs(Context context) 
	{
		Pair pair = null;
		
		for (Trigger t : this._triggers)
		{
			Pair triggerPair = t.schemePair();
			
			if (pair == null)
				pair = new Pair(Symbol.BEGIN, new Pair(triggerPair, Pair.EMPTY));
			else
			{
				Pair rest = (Pair) pair.rest();
				
				rest = new Pair(triggerPair, rest);

				pair.setRest(rest);
			}
		}

		return new Pair(pair, Pair.EMPTY);
	}
}

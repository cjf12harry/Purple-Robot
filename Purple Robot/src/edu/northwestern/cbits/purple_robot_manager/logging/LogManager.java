package edu.northwestern.cbits.purple_robot_manager.logging;

import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import edu.northwestern.cbits.anthracite.Logger;
import edu.northwestern.cbits.purple_robot_manager.EncryptionManager;

public class LogManager 
{
	private static final String ENABLED = "config_enable_log_server";
	private static final boolean ENABLED_DEFAULT = false;
	
	private static final String URI = "config_log_server_uri";
	private static final String URI_DEFAULT = null;

	private static final String INCLUDE_LOCATION = "config_log_location";
	private static final boolean INCLUDE_LOCATION_DEFAULT = false;
	
	private static final String UPLOAD_INTERVAL = "config_log_upload_interval";
	private static final long UPLOAD_INTERVAL_DEFAULT = 300000;
	
	private static final String WIFI_ONLY = "config_restrict_log_wifi";
	private static final boolean WIFI_ONLY_DEFAULT = true;
	
	private static final String LIBERAL_SSL = "config_http_liberal_ssl";
	private static final boolean LIBERAL_SSL_ONLY = false;

	private static LogManager _sharedInstance = null;
	
	private Logger _logger = null;
	
	public LogManager(Context context) 
	{
		String userId = EncryptionManager.getInstance().getUserHash(context);
		
		this._logger = Logger.getInstance(context, userId);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		this._logger.setEnabled(prefs.getBoolean(LogManager.ENABLED, LogManager.ENABLED_DEFAULT));
		this._logger.setUploadUri(Uri.parse(prefs.getString(LogManager.URI, LogManager.URI_DEFAULT)));
		this._logger.setIncludeLocation(prefs.getBoolean(LogManager.INCLUDE_LOCATION, LogManager.INCLUDE_LOCATION_DEFAULT));
		this._logger.setUploadInterval(prefs.getLong(LogManager.UPLOAD_INTERVAL, LogManager.UPLOAD_INTERVAL_DEFAULT));
		this._logger.setWifiOnly(prefs.getBoolean(LogManager.WIFI_ONLY, LogManager.WIFI_ONLY_DEFAULT));
		this._logger.setLiberalSsl(prefs.getBoolean(LogManager.LIBERAL_SSL, LogManager.LIBERAL_SSL_ONLY));
	}

	public static LogManager getInstance(Context context)
	{
		if (LogManager._sharedInstance != null)
			return LogManager._sharedInstance;
		
		if (context != null)
			LogManager._sharedInstance = new LogManager(context.getApplicationContext());
		
		LogManager._sharedInstance.log("pr_log_manager_initialized", null);
		
		return LogManager._sharedInstance;
	}
	
	public boolean log(String event, Map<String, Object> payload)
	{
		return this._logger.log(event, payload);
	}

	public void logException(Throwable e) 
	{
		this._logger.logException(e);
	}
}

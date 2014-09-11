package com.hinnovac.dualscreen;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CordovaInterface;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.webkit.WebView;

public class DualDisplay extends CordovaPlugin{	
	public static final String TAG = "DualDisplay";
	public static final String ACTION_SEND = "send";
	public static final String ACTION_BIND_LISTENER = "ACTION_BIND_LISTENER";
	private static WebView webview;
	private static boolean isWebview = false;
	
	private static CallbackContext listenerCallbackContext;
	
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
	    super.initialize(cordova, webView);
	    // your init code here
	}
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		Log.d(TAG,"in DualDisplay");
		try{
			if (ACTION_SEND.equals(action)) {
				Log.d(TAG,"Sending:"+args.toString());
				sendJS(args.getString(0));
				
				callbackContext.success();
				
				return true;
			}
			else if(ACTION_BIND_LISTENER.equals(action)){	
				return bindListener(args, callbackContext);
			}

			else {
				callbackContext.error("Invalid action in dualdisplay");
				return false;
			}
		} catch(Exception e) {
			System.err.println("[DualDisplay] Exception: " + e.getMessage());
			callbackContext.error(e.getMessage());
			return false;
	    }
	}	

	public static void setWebview(WebView wv){
		Log.d(TAG,"setWebview");
		webview= wv;
		isWebview=true;
	}
	
	public void sendJS(String message){
		final String msg = message;
		if(isWebview){
			webview.post(new Runnable() {
			    @Override
			    public void run() {
			    	webview.loadUrl("javascript:"+msg);
			    }
			});
			
		}
		
	}
	
	private boolean bindListener(JSONArray args, CallbackContext callbackContext) {
    	Log.d(TAG, "bindListener");
    	listenerCallbackContext = callbackContext;
    	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
    	pluginResult.setKeepCallback(true);
    	callbackContext.sendPluginResult(pluginResult);
    	return true;
	}
	
	public static void reportEvent(JSONObject eventData){
		try{
			if(listenerCallbackContext!= null){
		    	Log.d(TAG, "reportEvent: "+eventData.toString());
		    	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, eventData);
		    	pluginResult.setKeepCallback(true);
		    	listenerCallbackContext.sendPluginResult(pluginResult);
			}
		}catch(Exception e){
			System.err.println("[DualDisplay.reportEvent] Exception: " + e.getMessage());
		}
    }

}

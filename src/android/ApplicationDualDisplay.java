
package com.hinnovac.dualscreen;

import android.annotation.TargetApi;
import android.app.Presentation;
import android.content.Context;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.cordova.*;
import org.json.JSONException;
import org.json.JSONObject;


public class ApplicationDualDisplay extends CordovaActivity 
{
	MediaRouter router=null;
	static Presentation preso=null;
	SimpleCallback cb=null;
	public static final String TAG = "ApplicationDualDisplay";
	
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.init();
        // Set by <content src="index.html" /> in config.xml
        super.loadUrl(Config.getStartUrl());
        //super.loadUrl("file:///android_asset/www/index.html");
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onResume() {
      super.onResume();

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        if (cb==null) {
          cb=new RouteCallback();
          router=(MediaRouter)getSystemService(MEDIA_ROUTER_SERVICE);
        }
        
        handleRoute(router.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO));
        router.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, cb);
      }
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onPause() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        clearPreso();

        if (router != null) {
          router.removeCallback(cb);
        }
      }

      super.onPause();
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void handleRoute(RouteInfo route) {
      if (route == null) {
        clearPreso();
      }
      else {
        Display display=route.getPresentationDisplay();

        if (route.isEnabled() && display != null) {
          if (preso == null) {
            showPreso(route);
            Log.d(getClass().getSimpleName(), "enabled route");
          }
          else if (preso.getDisplay().getDisplayId() != display.getDisplayId()) {
            clearPreso();
            showPreso(route);
            Log.d(getClass().getSimpleName(), "switched route");
          }
          else {
            // no-op: should already be set
          }
        }
        else {
          clearPreso();
          Log.d(getClass().getSimpleName(), "disabled route");
        }
      }
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void clearPreso() {
      if (preso != null) {
        preso.dismiss();
        preso=null;
      }
      dispachOnReceive("screenDisconnected","No Dual Screen available");
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showPreso(RouteInfo route) {
      preso=new SimplePresentation(this, route.getPresentationDisplay());
      preso.show();
      dispachOnReceive("screenConnected",route.getPresentationDisplay().getName());
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private class RouteCallback extends SimpleCallback {
      @Override
      public void onRoutePresentationDisplayChanged(MediaRouter router,
                                                    RouteInfo route) {
        handleRoute(route);
      }
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private class SimplePresentation extends Presentation {
    	WebView wv;
    	
      SimplePresentation(Context ctxt, Display display) {
        super(ctxt, display);
      }

      @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wv=new WebView(getContext());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setAllowFileAccessFromFileURLs(true);
        wv.setWebViewClient(new MyWebViewClient());
        wv.loadUrl("file:///android_asset/www2/index.html");


        setContentView(wv);
      }
      
      
      private class MyWebViewClient extends WebViewClient{
    	  
    	  @Override
    	  public boolean shouldOverrideUrlLoading(WebView webview, String url){
    		  webview.loadUrl(url);
    		  return true;
    	  }
    	  
    	  @Override
    	  public void onPageFinished(WebView view, String url) 
    	  {       
    	      // Obvious next step is: document.forms[0].submit()
    		  DualDisplay.setWebview(view);
    		  dispachOnReceive("screenReady", "");
    	  }  
      }
    }
    
    protected static void dispachOnReceive(String type, String msg){
		JSONObject eventData = new JSONObject();
		
		Log.e(TAG, "try to push back.");
		
		try {
			//DualDisplay bridge = (Communicate) appView.pluginManager.getPlugin("DualDisplay");
			eventData.put("eventType", type);
			eventData.put("message", msg);
			DualDisplay.reportEvent(eventData);
		} catch (JSONException e) {
			Log.e(TAG, "Cannot send dualdisplay event.");
			e.printStackTrace();
		}
		return;
	}
}


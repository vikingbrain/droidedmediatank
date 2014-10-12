/*
 * Copyright 2011-2014 Rafael Iñigo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vikingbrain.dmt.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.service.SettingsService;

/**
 * Activity to show the information section of the application. It uses a web view to load
 * a static web page at m.vikingbrain.com so information about the app like FAQ's can be changed
 * dynamically without releasing a new apk version.
 * 
 * @author Rafael Iñigo
 */
public class InfoActivity extends BaseActivity {

	WebView mWebView;
	private SettingsService settingsService;

	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.info_web);

	    settingsService = DMTApplication.getSettingsService();
	    
		mWebView = (WebView) findViewById(R.id.webview);
		
		//Set the properties
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);	    
		webSettings.setSupportZoom(true); //Zoom Control on web (You don't need this if ROM supports Multi-Touch	    
		webSettings.setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
		webSettings.setUseWideViewPort(true); //normal viewport (such as a normal desktop browser)
		
		//Set the user's custom default zoom saved in settings
		webSettings.setDefaultZoom(settingsService.getConfig().getDefaultZoom().getZoomDensity()); 
		
	   //Sets the Chrome Client, and defines the onProgressChanged
	   //This makes the Progress bar be updated.
	   mWebView.setWebChromeClient(new WebChromeClient() {
		    public void onProgressChanged(WebView view, int progress) {
			     //Make the bar disappear after URL is loaded, and changes string to Loading...
		    	getSupportActionBar().setTitle((CharSequence) (progress+"%"));

		         // Return the app name after finish loading
			    if(progress == 100){
			    	getSupportActionBar().setTitle(R.string.title_info);
			    }
		    }
	   });

		   		   
	   mWebView.setWebViewClient(new CustomWebViewClientInfo());
	   
	   String url = getApplicationContext().getResources().getString(R.string.url_info_web);
	   mWebView.loadUrl(url);
	}
	
	private class CustomWebViewClientInfo extends WebViewClient {
		
     	@Override
	    public void onPageFinished(WebView view, String url) {
     		//Limpieza de la cache para que no se acumule
     		view.clearCache(true);
	    }
		 
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {

	    	 if (url.startsWith("mailto:") || url.startsWith("tel:")
	    			 || url.startsWith("http://www.networked")
	    			 || url.startsWith("http://www.vikingbrain")) { 
                 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
                 startActivity(intent); 
             } else if (url.startsWith("http://market.android.com")){
            	 //TODO include play.google.com also            	
            	 String urlMobileMarket = "market://details?id=com.vikingbrain.dmt";
                 Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlMobileMarket)); 
                 startActivity(intent); 
             } else {
            	 //Normal load page in the webview
            	 view.loadUrl(url);
             }
	         return true;
	    }
	}

}
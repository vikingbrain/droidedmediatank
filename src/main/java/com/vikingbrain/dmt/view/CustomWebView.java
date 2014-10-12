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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.actionbarsherlock.view.Menu;
import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.view.util.UtilityView;

/**
 * Activity with a custom web view.
 * 
 * @author Rafael Iñigo
 */
public class CustomWebView extends BaseActivity {
	
	WebView mWebView;		
	private SettingsService settingsService;
	
	public static boolean START_INI = true;
	public static String URL_TO_REMEMBER = "";

	private String urlAsked;
	private String linkUsername = null;
	private String linkPassword = null;
	
	boolean showSignOut = false;

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getSupportMenuInflater().inflate(R.menu.custom_webview_main, menu);                
 		return true;
    }

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) 
	{
	    switch (item.getItemId()) {	    	    
	    case R.id.menu_popout:
			onClickPopout();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	    
  
	    setContentView(R.layout.custom_browser);
	    
		settingsService = DMTApplication.getSettingsService();
		
		mWebView = (WebView) findViewById(R.id.webview);
		
		//Set the properties
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);	    
		webSettings.setSupportZoom(true); //Zoom Control on web (You don't need this if ROM supports Multi-Touch	    
		webSettings.setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
				
		//Fit web page width to the screen
		webSettings.setUseWideViewPort(true); //normal viewport (such as a normal desktop browser)
		
		//Set the default zoom from usser settings
		webSettings.setDefaultZoom(settingsService.getConfig().getDefaultZoom().getZoomDensity()); 
			
	    if (START_INI){
		    //Check if there is an url that was asked
		    Bundle b = getIntent().getExtras();
		    if (b != null) {
		    	urlAsked = b.getString("urlAsked");
				linkUsername = b.getString("username"); //It will get null if it doesn't exists
				linkPassword = b.getString("password"); //It will get null if it doesn't exists
		    }	    	    
	    }
	    
	   //Sets the Chrome Client, and defines the onProgressChanged
	   //This makes the Progress bar be updated.
	   mWebView.setWebChromeClient(new WebChromeClient() {
		    public void onProgressChanged(WebView view, int progress) {
			     //Make the bar disappear after URL is loaded, and changes string to Loading...
		    	getSupportActionBar().setTitle((CharSequence) (progress+"%"));

		         // Return the app name after finish loading
			    if(progress == 100){
			    	getSupportActionBar().setTitle(R.string.title_web_browser);
			    }
		    }
	   });	       	    
	    
		mWebView.setWebViewClient(new CustomWebViewClient(this));
		
		if (START_INI){
		    mWebView.loadUrl(urlAsked);
		    			    	
		    //Recordamos la url por si no navega (se queda en esta principal) sepamos
		    //que es la ultima visitada
		    URL_TO_REMEMBER = urlAsked;		
		} else {
			mWebView.loadUrl(URL_TO_REMEMBER);
		}
		
		START_INI = false;		
	    
	}		
	
	public void setHttpAuthUsernamePassword(String host, String realm, String username, String password) {
		mWebView.setHttpAuthUsernamePassword(host, realm, username, password);
	}

	private class CustomWebViewClient extends WebViewClient {
		
		private CustomWebView mCustomWebViewActivity;
		
		public CustomWebViewClient(CustomWebView customWebViewActivity){
			super();
			mCustomWebViewActivity = customWebViewActivity;			
		}		
		
     	@Override
	    public void onPageFinished(WebView view, String url) {
     		//Clean the cache
     		view.clearCache(true);
	    }
		 
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	        	
        	//Importat to remember the url in case screen is rotated or
	    	//is an url to play a file
        	URL_TO_REMEMBER = url;
        	
        	//Load the asked url
        	view.loadUrl(url);

        	return true;
	    }
	    
        @TargetApi(8)
        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                
                StringBuilder sb = new StringBuilder();
                
                sb.append(view.getResources().getString(R.string.sslWarningsHeader));
                sb.append("\n\n");
                
                if (error.hasError(SslError.SSL_UNTRUSTED)) {
                        sb.append(" - ");
                        sb.append(view.getResources().getString(R.string.sslUntrusted));
                        sb.append("\n");
                }
                
                if (error.hasError(SslError.SSL_IDMISMATCH)) {
                        sb.append(" - ");
                        sb.append(view.getResources().getString(R.string.sslIDMismatch));
                        sb.append("\n");
                }
                
                if (error.hasError(SslError.SSL_EXPIRED)) {
                        sb.append(" - ");
                        sb.append(view.getResources().getString(R.string.sslExpired));
                        sb.append("\n");
                }
                
                if (error.hasError(SslError.SSL_NOTYETVALID)) {
                        sb.append(" - ");
                        sb.append(view.getResources().getString(R.string.sslNotYetValid));
                        sb.append("\n");
                }
                
                UtilityView.showContinueCancelDialog(view.getContext(),
                                android.R.drawable.ic_dialog_info,
                                view.getResources().getString(R.string.sslWarning),
                                sb.toString(),
                                new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                handler.proceed();
                                        }

                                },
                                new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                handler.cancel();
                                        }
                });
        }        
		
		@Override
		public void onReceivedHttpAuthRequest(WebView view, final HttpAuthHandler handler, final String host, final String realm) {
			String username = null;
	        String password = null;	        
						
	        boolean reuseHttpAuthUsernamePassword = handler.useHttpAuthUsernamePassword();
	        
	        if (reuseHttpAuthUsernamePassword && view != null) {
	            String[] credentials = view.getHttpAuthUsernamePassword(
	                    host, realm);
	            if (credentials != null && credentials.length == 2) {
	                username = credentials[0];
	                password = credentials[1];
	            }
	        }

	        if (username != null && password != null) {
	            handler.proceed(username, password);
	        } else {
				showHttpAuthDialog(handler, host, realm);
	        }
		}
		
		private void showHttpAuthDialog(final HttpAuthHandler handler, final String host, final String realm) {
        	LayoutInflater factory = LayoutInflater.from(mCustomWebViewActivity);
            final View v = factory.inflate(R.layout.authentication_dialog, null);
            
            if (linkUsername != null) {
                ((EditText) v.findViewById(R.id.username)).setText(linkUsername);
            }
            if (linkPassword != null) {
                ((EditText) v.findViewById(R.id.password)).setText(linkPassword);
            }
            
            AlertDialog dialog = new AlertDialog.Builder(mCustomWebViewActivity)
            .setTitle(String.format(mCustomWebViewActivity.getString(R.string.title_authenticationDialog, host, realm)))	            
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setView(v)
            .setPositiveButton(R.string.proceed,
                    new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog,
                                 int whichButton) {
                            String nm = ((EditText) v.findViewById(R.id.username)).getText().toString();
                            String pw = ((EditText) v.findViewById(R.id.password)).getText().toString();
	                            mCustomWebViewActivity.setHttpAuthUsernamePassword(host, realm, nm, pw);
                            handler.proceed(nm, pw);
                        }})
            .setNegativeButton(R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            handler.cancel();
                        }})
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    public void onCancel(DialogInterface dialog) {
                        handler.cancel();
                    }})
            .create();
            
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            dialog.show();
                        
            v.findViewById(R.id.username).requestFocus();			
		}
		
	}	    

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    // Check if the key event was the BACK key and if there's history
	    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
	    	mWebView.goBack();
	        return true;
	    }
	    // If it wasn't the BACK key or there's no web page history, bubble up to the default
	    // system behavior (probably exit the activity)
	    return super.onKeyDown(keyCode, event);
	}

	/*
	 * Open the url in the default Android browser.
	 */	
	public void onClickPopout() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_TO_REMEMBER)); 
        startActivity(intent); 		
	}	
	
}
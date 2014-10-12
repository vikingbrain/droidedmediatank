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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.Menu;
import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.DavidBoxService;
import com.vikingbrain.dmt.service.util.Keyboard2RemoteKey;
import com.vikingbrain.dmt.service.util.KeyboardTaskQueue;
import com.vikingbrain.dmt.view.asynctaks.TaskNmtExecuteKeyIR;

/**
 * Activity for the remote control.
 * 
 * @author Rafael Iñigo
 */
public class RemoteControlActivity extends BaseActivity implements 
		DisplayFragment.OnCurrentlyAudioPlayingDetectedListener,
		ActionBar.TabListener {
	
	private DavidBoxService davidBoxService;
	
	WebView mWebView;		
	WebView mWebViewTab1;		
	WebView mWebViewTab2;		
	WebView mWebViewTab3;		
	
	boolean isKeyboardShowing;	
	KeyboardTaskQueue keyboardTaskQueue;

    private static final int POSITION_TAB_NUMERIC = 0;
    private static final int POSITION_TAB_QUICKNAV = 1;
    private static final int POSITION_TAB_ADVANCED = 2;
    
    /**
     * Check if it is a tablet or not.
     * @return if is a tablet
     */
	private boolean isTablet(){
		boolean isTablet = false;
		if (null != findViewById(R.id.webviewXLarge)){
			isTablet = true;
		}		
		return isTablet;
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu.
        getSupportMenuInflater().inflate(R.menu.remote_control_main, menu);                
 		return true;
    }

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    switch (item.getItemId()) {	    	    
	    case R.id.menu_keyboard:
	    	toggleKeyboard();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Change visibility of keyboard.
	 */
	private void toggleKeyboard(){
		if (isKeyboardShowing) {
			hideKeyboard();
		} else {
			showKeyboard();
		}
	}
	
	/**
	 * Show the keyboard.
	 */
	private void showKeyboard(){	
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm != null){
			imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);			
			isKeyboardShowing = true;
		}		
	}

	/**
	 * Hide the keyboard.
	 */
	private void hideKeyboard(){	
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm != null){
			imm.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
			isKeyboardShowing = false;
		}		
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		//In case user leaves the screen always hide the keyboard
		hideKeyboard();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		
		int keyaction = event.getAction();
		int keyCode = event.getKeyCode();
		
		//Manage key up keys
		if (keyaction == KeyEvent.ACTION_UP){

			if (keyCode == KeyEvent.KEYCODE_BACK){
				//Back button, exit the activity
				onBackPressed();
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DEL){	    	
	            //this is for backspace
			    addCharacterToSendQueue(Keyboard2RemoteKey.BACKSPACE.getKeyboardSymbol());		
			    return true;		    
			} else {
				//Any other keyboard key up			
				DmtLogger.d("key pressed", String.valueOf(event.getKeyCode()));		    	    	
			    char unicodeChar = (char)event.getUnicodeChar();	
			    String keyboardSymbol = String.valueOf(unicodeChar);
			    
			    addCharacterToSendQueue(keyboardSymbol);		    
			    return true;
			}
        }  	    
	    return super.dispatchKeyEvent(event);
	}
		
	private void addCharacterToSendQueue(String keyboardSymbol){
		if (null == keyboardTaskQueue){
		    keyboardTaskQueue = new  KeyboardTaskQueue(davidBoxService);
		}
		keyboardTaskQueue.addKeyboardSymbol(keyboardSymbol);
		//it will start it in case it is stopped
		keyboardTaskQueue.start();
	}
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	    
	    
	    setContentView(R.layout.activity_remote_control);

	    davidBoxService = DMTApplication.getDavidBoxService();
	            
	    isKeyboardShowing = false;
	    	    
        if (isTablet()){ //it's tablet
        	mWebView = (WebView) findViewById(R.id.webviewXLarge);
        } else { //it's phone               	
        	mWebViewTab1 = (WebView) findViewById(R.id.webviewtab1);
        	mWebViewTab2 = (WebView) findViewById(R.id.webviewtab2);
        	mWebViewTab3 = (WebView) findViewById(R.id.webviewtab3);
        }

        //Common configuration for web views
        WebChromeClient webChromeClient =  new WebChromeClient();    	
        CustomWebViewClient customWebViewClient = new CustomWebViewClient();
                
 	    
 	    //phone vs tablet, the have different html pages
 	    if (isTablet()){ //it's tablet
 	    	configureWebView(mWebView, webChromeClient, customWebViewClient);

 	    	mWebView.loadUrl(Constants.PATH_ASSET_HTML_REMOTE_CONTROL_INDEX_TABLET);  		
 	    	
 	    } else { //it's phone 	    
 	    	
 	    	configureWebView(mWebViewTab1, webChromeClient, customWebViewClient);
 	    	configureWebView(mWebViewTab2, webChromeClient, customWebViewClient);
 	    	configureWebView(mWebViewTab3, webChromeClient, customWebViewClient);

 	    	//Quicknav is the default shown so it is priority
 	    	mWebViewTab2.loadUrl(Constants.PATH_ASSET_HTML_REMOTE_CONTROL_QUICKNAV);  		
    	   	mWebViewTab1.loadUrl(Constants.PATH_ASSET_HTML_REMOTE_CONTROL_NUMERIC);  		
    	   	mWebViewTab3.loadUrl(Constants.PATH_ASSET_HTML_REMOTE_CONTROL_ADVANCED);  		
  		
    	   	//Tabs
	    	getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	    	ActionBar.Tab tab = getSupportActionBar().newTab();
	    	tab.setText(R.string.tab_numeric);
	    	tab.setTabListener(this);
	    	getSupportActionBar().addTab(tab, POSITION_TAB_NUMERIC);
      
	    	ActionBar.Tab tab2 = getSupportActionBar().newTab();
	    	tab2.setText(R.string.tab_quicknav);
	    	tab2.setTabListener(this);
	    	getSupportActionBar().addTab(tab2, POSITION_TAB_QUICKNAV);

	    	ActionBar.Tab tab3 = getSupportActionBar().newTab();
	    	tab3.setText(R.string.tab_advanced);
	    	tab3.setTabListener(this);
	    	getSupportActionBar().addTab(tab3, POSITION_TAB_ADVANCED);

	    	//Tab selected by default is number 2 (tab 1 is numbber 0)
	    	getSupportActionBar().setSelectedNavigationItem(POSITION_TAB_QUICKNAV);
 	    } 	    
          
	}
    
	private void configureWebView(WebView webView, WebChromeClient webChromeClient, CustomWebViewClient customWebViewClient){
	    //Set the properties		
		WebSettings webSettings = webView.getSettings();
	  	webSettings.setJavaScriptEnabled(true);	    
	  	webSettings.setSupportZoom(true); //Zoom Control on web (You don't need this if ROM supports Multi-Touch	    
	  	webSettings.setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
	  	webSettings.setDomStorageEnabled(true); 
	 	webSettings.setPluginsEnabled(true);	  		
	  	webSettings.setUseWideViewPort(true); //normal viewport (such as a normal desktop browser)
	  		
	  	webView.setWebChromeClient(webChromeClient);
	  	webView.setWebViewClient(customWebViewClient);
	}

	
	private class CustomWebViewClient extends WebViewClient {
		
		public CustomWebViewClient(){
			super();
		}		

     	@Override
	    public void onPageFinished(WebView view, String url) {
     		//Limpieza de la cache para que no se acumule
     		view.clearCache(true);
	    }
		 
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	    	//This is the binding between local web page and java code
	    	//All href's in html are links starting with "http://dmt_send_key/"
	    	//so param url is "http://dmt_send_key/play" or "http://dmt_send_key/stop"
	    	if (url.startsWith(Constants.BASE_URL_FOR_COMMAND)){
	    		String keyName = url.replace(Constants.BASE_URL_FOR_COMMAND, "");
	    		new TaskNmtExecuteKeyIR(davidBoxService).execute(keyName);
	    	}

        	return true;
	    }
	}

	@Override
	public void onCurrentlyAudioPlayingDetected(String fullPath) {
		//The activity is receiving information from the fragment
		//No need to do anything in this activity just implement this method
		//to cover the class cast exception because the use of the fragment in the xml file
	}

	
    @Override
    public void onTabReselected(Tab tab, FragmentTransaction transaction) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction transaction) {

    	switch (tab.getPosition()) {
		case POSITION_TAB_NUMERIC:
    	   	mWebViewTab1.setVisibility(View.VISIBLE);    		
    	   	mWebViewTab2.setVisibility(View.GONE);
    	   	mWebViewTab3.setVisibility(View.GONE);			
			break;
		case POSITION_TAB_QUICKNAV:
    	   	mWebViewTab1.setVisibility(View.GONE);    		
    	   	mWebViewTab2.setVisibility(View.VISIBLE);
    	   	mWebViewTab3.setVisibility(View.GONE);			
			break;
		case POSITION_TAB_ADVANCED:
    	   	mWebViewTab1.setVisibility(View.GONE);    		
    	   	mWebViewTab2.setVisibility(View.GONE);
    	   	mWebViewTab3.setVisibility(View.VISIBLE);			
			break;

		default:
			break;
		}
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction transaction) {
    }
}
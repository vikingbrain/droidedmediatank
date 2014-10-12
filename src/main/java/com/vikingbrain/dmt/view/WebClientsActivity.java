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

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Link;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.LinkService;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.util.UtilProfile;
import com.vikingbrain.dmt.view.util.UtilityView;

/**
 * Activity for the grid of web app links.
 * 
 * @author Rafael Iñigo
 */
public class WebClientsActivity extends BaseActivity {

	/** Tag for the logger. */
	private static final String TAG = WebClientsActivity.class.getSimpleName();
	
	private LinkService linkService;
	private SettingsService settingsService;
	
	GridView grid_enlaces;		

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_clients);
        
        linkService = DMTApplication.getLinkService();
        settingsService = DMTApplication.getSettingsService();
        
        refreshContent();        
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshContent();
    }

    /**
     * Refresh the content for the grid with the links.
     */
	public void refreshContent(){		
		grid_enlaces = (GridView)findViewById(R.id.gridEnlaces);
	    //Call the service
	    List<Link> webClientList = linkService.getLinksShowables();
	    grid_enlaces.setAdapter(new ImageButtonTextAdapter(webClientList));     	
    }
    
	/**
	 * Specify what will happen  when a web client is clicked.
	 * @param commonView the view
	 * @param url the url
	 * @param isAuthentication if authentication is required
	 * @param username the user name
	 * @param password the password
	 */
    private void specifyOnClick(View commonView, final String url,
    		final boolean isAuthentication, final String username, final String password){
    	commonView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {            	
            	buildAndOpenWebLink(url, isAuthentication, username, password);
            }
        });            	
    }

    /**
     * It builds the web url with the IP set in settings and opens the custom web browser.
     * @param url
     */
    public void buildAndOpenWebLink(String url, boolean isAuthentication, String username, String password){
    	//Get the ip for the web clients
    	//if not specified ip for web clients, then get the ipOrDomain for ftp
    	String ipOrDomain = UtilProfile.getIpOrDomainForWebClients(settingsService.getActiveProfile());
    	String urlVerdadera = "http://" + ipOrDomain + url;
    	
    	//Opens in Custom Web Broswer	
    	//Open the browser in mode start_ini so it gets the new url instead of last visited url
    	CustomWebView.START_INI = true;
    	Intent i = new Intent(this, CustomWebView.class);     
    	i.putExtra("urlAsked", urlVerdadera);
		//put authentication if the link specifies that
		if (isAuthentication){
			i.putExtra("authentication", isAuthentication);
			i.putExtra("username", username);
			i.putExtra("password", password);
		}
		startActivity(i);
    }
    
    /**
     * Adapter for an image button with text.
     * @author Rafael Iñigo
     */
	public class ImageButtonTextAdapter extends BaseAdapter{                                 
		
		List<Link> webClientList;

		/**
		 * Constructor.
		 * @param webClientList the list with the web clients
		 */
		public ImageButtonTextAdapter(List<Link> webClientList){
			this.webClientList = webClientList;
		}

		public int getCount() {
			return webClientList.size();
		}		

		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if(convertView==null){
				//do nothing
				DmtLogger.d(TAG, "convertView is null, it's ok");
			}
			else {
				v = convertView;
			}

			LayoutInflater li = getLayoutInflater();
			v = li.inflate(R.layout.icon, null);
						
			TextView tv = (TextView)v.findViewById(R.id.icon_text);
			ImageButton iv = (ImageButton)v.findViewById(R.id.icon_imageButton);
			
			Link link = webClientList.get(position);			
			
			String urlEnding = link.getUrlEnd();
			
			//Set text with link's name
			UtilityView.setTextToLink(link, tv);			
			
			//Set onClick to the text
			specifyOnClick(tv, urlEnding, link.isAuthentication(), link.getUserName(), link.getPassword());
						
			//Set the image
			iv.setImageResource(UtilityView.getIdImage(link));
		
			//Set onClick to the image button	        
			specifyOnClick(iv, urlEnding, link.isAuthentication(), link.getUserName(), link.getPassword());
	        				        
			return v;
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}
	}
	
}
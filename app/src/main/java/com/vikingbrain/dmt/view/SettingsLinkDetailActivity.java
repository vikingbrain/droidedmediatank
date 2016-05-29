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
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.Link;
import com.vikingbrain.dmt.pojo.TypeLink;
import com.vikingbrain.dmt.pojo.TypeWebClient;
import com.vikingbrain.dmt.pojo.UtilLink;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.LinkService;
import com.vikingbrain.dmt.view.util.PreferenceCheckBox;
import com.vikingbrain.dmt.view.util.PreferenceCustomLink;
import com.vikingbrain.dmt.view.util.PreferenceEditText;
import com.vikingbrain.dmt.view.util.UtilityView;

/**
 * Activity to show or edit a link.
 * 
 * @author Rafael Iñigo
 *
 * @param <PreferencesAdapter>
 */
public class SettingsLinkDetailActivity<PreferencesAdapter> extends SherlockPreferenceActivity {
	
	private static final String TAG = SettingsLinkDetailActivity.class.getSimpleName();
	
    private static final String URL_START_DISPLAY_TO_USER = "http://[ipOrDomain]";

	private Link link;	
	private LinkService linkService;	

    Button saveButton;
    Button deleteButton;
    Button restoreButton;

    LinearLayout infoLinearLayout;
    
	private PreferenceEditText name;               
    private PreferenceEditText urlEnd;
    private PreferenceCheckBox checkAuthentication;
    private PreferenceEditText userName;
    private PreferenceEditText password;
    
	private String nameValue = null;
	private String urlEndValue = null;
	private boolean checkAuthenticationValue = true; //default init value, so not null
	private String userNameValue = null;
	private String passwordValue = null;

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	//Emulate back button
	    	onBackPressed();
	        return true;	    	    
		}
		return super.onOptionsItemSelected(item);
	}

	private Link getLink(long id){
    	Link link = linkService.getLink(id);
    	return link;
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        

	    //ActionBarSherlock
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);		

        // Create the preferences screen here: this takes care of saving/loading, but also contains the ListView adapter, etc.
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));

        linkService = DMTApplication.getLinkService();
        		
	    Bundle b = getIntent().getExtras();
	    if (b != null) {
	    	//Edit a existing link
	    	long id = b.getLong("id");
	    	DmtLogger.d(TAG, "Editing link id: " + id);
	    	link = getLink(id);
	    	
	    } else {
	    	//Screen for creating a new link
	    	//Build a link object with the default values
	    	link = UtilLink.buildLinkDefaultFields();
	    }		

	    //Values to show on the screen
	    //TODO icon's link
	    nameValue = link.getName();
	    urlEndValue = link.getUrlEnd();
	    checkAuthenticationValue = link.isAuthentication();
	    userNameValue = link.getUserName();
	    passwordValue = link.getPassword();	    
	    
		// Link Name
        name = new PreferenceEditText(this);
        name.setTitle(R.string.label_name);
        name.setText(nameValue);
        name.getEditText().setSingleLine();
        name.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER);
        name.setDialogTitle(R.string.label_name);
        name.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(name);
        

        //Url end        
		urlEnd = new PreferenceEditText(this);
		urlEnd.setTitle(R.string.label_url);
		urlEnd.setText(urlEndValue);
		urlEnd.getEditText().setSingleLine();
		urlEnd.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
		//Hint
		urlEnd.getEditText().setHint(R.string.url_end_hint);		
		urlEnd.setDialogTitle(R.string.label_url_ending);
		urlEnd.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(urlEnd);


		//checkAuthentication
		checkAuthentication = new PreferenceCheckBox(this);
		checkAuthentication.setTitle(R.string.label_authentication);
		checkAuthentication.setKey("checkAuthentication_preference");
		checkAuthentication.setChecked(checkAuthenticationValue);		
		checkAuthentication.setSummary(R.string.label_authentication_summary);      
		checkAuthentication.setOnPreferenceChangeListener(updateHandler);
		getPreferenceScreen().addItemFromInflater(checkAuthentication);								


        //Username
        userName = new PreferenceEditText(this);
        userName.setTitle(R.string.username);
        userName.setText(userNameValue);
        userName.getEditText().setSingleLine();
        userName.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER);
        userName.setDialogTitle(R.string.username);
        userName.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(userName);

        //Password
        password = new PreferenceEditText(this);
        password.setTitle(R.string.password);
        password.setText(passwordValue);
        password.getEditText().setSingleLine();
        password.getEditText().setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        password.getEditText().setTransformationMethod(new PasswordTransformationMethod());
        password.setDialogTitle(R.string.password);
        password.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(password);

        if (TypeLink.ORIGINAL == link.getTypeLink()){
        	String codename = link.getName();
        
	        //Find the enumeration type for this web client,
        	//it contains all the i18n information
        	TypeWebClient typeWebClient = TypeWebClient.findByCodename(codename);
        	
        	if (null != typeWebClient){
        		
				//Title about for category "About [web_client_name]"
				PreferenceCategory infoPart = new PreferenceCategory(this);
				//i18n web client title
				String webClientTitle = getString(typeWebClient.getTitle());
				infoPart.setTitle(getString(R.string.title_about_link, webClientTitle));				
				getPreferenceScreen().addItemFromInflater(infoPart);
							
				//Link Info with the link info
				PreferenceCustomLink info = new PreferenceCustomLink(this);				
				info.setTitle(R.string.title_info);	
				//i18n with the description
				info.setSummary(typeWebClient.getDescription());				
		    	getPreferenceScreen().addPreference(info);

				//Web client Homepage
		    	PreferenceCustomLink homepagePreference = new PreferenceCustomLink(this);				
		    	homepagePreference.setTitle(R.string.homepage);	
				//i18n with the homepage
				String homepage = getString(typeWebClient.getHomepage());				
				homepagePreference.setSummary(homepage);
				//Intent open url web clicked if it is and url (not just information text)				
				if (UtilityView.isUrl(homepage)){
			        Intent intentHomepage = new Intent(Intent.ACTION_VIEW, Uri.parse(homepage)); 
			        homepagePreference.setIntent(intentHomepage);
				}
				getPreferenceScreen().addPreference(homepagePreference);
				
				//Web client Installation
				PreferenceCustomLink installationPreference = new PreferenceCustomLink(this);				
				installationPreference.setTitle(R.string.installation);	
				//i18n with the installation				
				String installation = getString(typeWebClient.getInstallation());
				installationPreference.setSummary(installation);
				//Intent open url web clicked if it is and url (not just information text)				
				if (UtilityView.isUrl(installation)){
			        Intent intentInstallation = new Intent(Intent.ACTION_VIEW, Uri.parse(installation)); 
			        installationPreference.setIntent(intentInstallation);				
				}
				getPreferenceScreen().addPreference(installationPreference);
				
				//CSI Custom Software Installer
				PreferenceCustomLink csiPreference = new PreferenceCustomLink(this);				
				csiPreference.setTitle(R.string.csi_installer_title);	
				//i18n with the url installation				
				String csiUrl = getString(R.string.csi_installer_url);
				csiPreference.setSummary(csiUrl);
		    	//Intent open url web clicked				
		        Intent intentCsi = new Intent(Intent.ACTION_VIEW, Uri.parse(csiUrl)); 
		        csiPreference.setIntent(intentCsi);				
				getPreferenceScreen().addPreference(csiPreference);				
			}				 
				 
        }
		
        //Update option availability and description texts
        updateOptionAvailability();
        updateDescriptionTexts();        
    }

	private OnPreferenceChangeListener updateHandler = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {

			if (preference == name){
				nameValue = (String) newValue;
				link.setName(nameValue);		
			} else if (preference == urlEnd ) {
				urlEndValue = (String) newValue;
				link.setUrlEnd(urlEndValue);
			} else if (preference == checkAuthentication ) {
				checkAuthenticationValue = ((Boolean) newValue).booleanValue();
				link.setAuthentication(checkAuthenticationValue == true ? Constants.NUMBER_ONE : Constants.NUMBER_ZERO);									
			} else if (preference == userName) {
				userNameValue = (String) newValue;
				link.setUserName(userNameValue);
			} else if (preference == password) {
				passwordValue = (String) newValue;
				link.setPassword(passwordValue);				
			}						
				
			//Save or update the link
			if (null == link.getId()){
				//It's a new link
				long id = linkService.saveOrUpdate(link);
				//Get the object from bbdd to have the id inside the object
				link = getLink(id);
			} else {
				//It's an existing link
				linkService.saveOrUpdate(link);				
			}			
			
			updateOptionAvailability();
			updateDescriptionTexts();
			// Set the value as usual
			return true;
		}		
	};

	/**
	 * Update availability for some options.
	 */	
    private void updateOptionAvailability() {

    	//Name not editable if link is original from the bbdd script
		if (TypeLink.ORIGINAL == link.getTypeLink()){
			name.setSelectable(false);
		} else {
			name.setSelectable(true);
		}

    	//Updates for checkbox
    	checkAuthentication.setChecked(checkAuthenticationValue == true ? true : false);  
    	
    	//Authentication username and passowrd only available if authentication is checked
    	if (checkAuthentication.isChecked()){
    		userName.setEnabled(true);
    		password.setEnabled(true);
    	} else {
    		userName.setEnabled(false);
    		password.setEnabled(false);
    	}    	
    }

    /**
     * Update view description texts.
     */
    private void updateDescriptionTexts() {

    	//Name
    	name.setSummary("".equals(nameValue) ? Constants.EMPTY_TEXT : nameValue);
    	
    	String urlLink = "";
    	if ("".equals(urlEndValue)){
    		urlLink = URL_START_DISPLAY_TO_USER + getText(R.string.url_end_hint);
    	} else {
    		urlLink = URL_START_DISPLAY_TO_USER + urlEndValue;
    	}
    	urlEnd.setSummary(urlLink);
    	
    	//Username
    	userName.setSummary("".equals(userNameValue) ? "": userNameValue);  	
    }

}

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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Config;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.pojo.TypeZoom;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.util.UtilProfile;
import com.vikingbrain.dmt.view.util.PreferenceCustomLink;
import com.vikingbrain.dmt.view.util.PreferenceList;
import com.vikingbrain.dmt.view.util.PreferenceProfile;

/**
 * Activity allows to change application settings.
 * 
 * @author Rafael Iñigo
 */
public class SettingsActivity<PreferencesAdapter> extends AbstractSettingsActivity {

	private Config config;
	private SettingsService settingsService;
	
	private PreferenceCategory profilesCategory;
	private PreferenceCustomLink addProfilePreference;
	
	private PreferenceList defaultZoomType;
	private PreferenceCustomLink advancePreferences;	
		
	private TypeZoom defaultZoomValue = null;	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        // app icon in action bar clicked; go home
	        Intent intent = new Intent(this, HomeActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);
	        return true;	    	    
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    //ActionBarSherlock
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);		

		settingsService = DMTApplication.getSettingsService();
	    	   		
		config = settingsService.getConfig();	    	    
	    defaultZoomValue = config.getDefaultZoom();	    
	    
        // Create the preferences screen here: this takes care of saving/loading, but also contains the ListView adapter, etc.
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));

		//PROFILES
		// Existing profiles for edition and button add progile
		profilesCategory = new PreferenceCategory(this);
		profilesCategory.setTitle(R.string.pref_devices);
		getPreferenceScreen().addItemFromInflater(profilesCategory);
						
		//The content for the profilesCategory will be loaded on the onResume method
				
        // webClientsPart Category common to al nmt profiles
		PreferenceCategory webClientsPart = new PreferenceCategory(this);
		webClientsPart.setTitle(R.string.pref_web_clients_part);
		getPreferenceScreen().addItemFromInflater(webClientsPart);
		        
        //Default zoom for web clients
        defaultZoomType = new PreferenceList(this);
        defaultZoomType.setTitle(R.string.pref_default_zoom);
        if (null != defaultZoomValue){
        	defaultZoomType.setValue(String.valueOf(defaultZoomValue.getId()));	
        }        		
        defaultZoomType.setEntries(R.array.zoom_types_entries);
        defaultZoomType.setEntryValues(R.array.zoom_types_entries_values);
        defaultZoomType.setDialogTitle(R.string.pref_default_zoom);
        defaultZoomType.setOnPreferenceChangeListener(updateHandler);
		getPreferenceScreen().addItemFromInflater(defaultZoomType);
        
        //Custom link para el manage web clients
        advancePreferences = new PreferenceCustomLink(this);
        advancePreferences.setTitle(R.string.pref_manage_apps);
        advancePreferences.setSummary(R.string.pref_manage_apps_summary);
        advancePreferences.setOnPreferenceClickListener(new OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference preference) {
		        Intent intentAdvance= new Intent(getApplicationContext(), SettingsLinksActivity.class);        
		        advancePreferences.setIntent(intentAdvance);        
				return false;
			}
		});
        
        getPreferenceScreen().addItemFromInflater(advancePreferences);
					
        //Control availability of fields
		updateOptionAvailability();
		//Update the description of summaries
        updateDescriptionTexts();
	}

	@Override
	protected void onResume() {
		super.onResume();

		//Fill this category with the list of profiles and the create profile button		
		fillProfilesCategory();
	}
	
	private OnPreferenceChangeListener updateHandler = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
											
			//Zoom type
			if (preference == defaultZoomType ) {
				int codigoTipoZoom = Integer.valueOf((String)newValue);
				defaultZoomValue = TypeZoom.findById(codigoTipoZoom);				
				config.setDefaultZoom(defaultZoomValue);
			}

			//Call the service to update the config
			settingsService.update(config);
			
			
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
    	//do nothing    	    	
    }

    /**
     * Update view description texts.
     */
    private void updateDescriptionTexts() {
    	
    	//default zoom
    	if (defaultZoomValue == null){
    		defaultZoomType.setSummary("");
    	} else {
    		defaultZoomType.setSummary(defaultZoomValue.getIdDescripcion());
    	}
    }

	private void fillProfilesCategory(){
		//Empty profiles category just in case a profile is removed the list need to be reloaded 
		profilesCategory.removeAll();
		
		//Get all profiles
	    List<Profile> profiles = settingsService.getProfiles();	    	    
		//A row for each device profile
		for(Profile profile : profiles){
			PreferenceProfile deviceProfile = new PreferenceProfile(this, profile.getId());
			
			//Alias of the profile in the title
			deviceProfile.setTitle(UtilProfile.getAlias(profile, this));
			
			//Summary of the device (MODEL - IP)
			String summary = "";
			if (!"".equals(profile.getTypeNmt())){
				summary = summary.concat(profile.getTypeNmt());
			} else {
				summary = summary.concat(getApplicationContext().getResources().getString(R.string.pref_unspecified_model));
			}
			summary = summary.concat(" - ");			
			if (!"".equals(profile.getIpNmt())){
				summary = summary.concat(profile.getIpNmt());
			} else {
				summary = summary.concat(getApplicationContext().getResources().getString(R.string.pref_unspecified_ip));
			}			
			deviceProfile.setSummary(summary);        
			
			deviceProfile.setOnPreferenceClickListener(new OnPreferenceClickListener() {			
				@Override
				public boolean onPreferenceClick(Preference preference) {

					Long idProfile = ((PreferenceProfile)preference).getId();	
					Profile profileSelected = settingsService.getProfile(idProfile);
					startActionMode(new ActionModeManageProfile(profileSelected));
					return false;
				}
			});

			profilesCategory.addPreference(deviceProfile);						
		}		

		
        //Add new profile button
		addProfilePreference = new PreferenceCustomLink(this);
		addProfilePreference.setTitle(R.string.pref_add_device);
		addProfilePreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				
		    	//Go to profile settings screen to add a new profile
				Intent intentAddProfile= new Intent(getApplicationContext(), SettingsProfileActivity.class);
				addProfilePreference.setIntent(intentAddProfile);
				return false;
			}
		});
        
		profilesCategory.addPreference(addProfilePreference);
        getPreferenceScreen().addItemFromInflater(advancePreferences);
	}
	
    private final class ActionModeManageProfile implements ActionMode.Callback {

    	final Profile profile;
		
		public ActionModeManageProfile(Profile profile) {
			super();
			this.profile = profile;
		}

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {        	
            // Inflate the menu.
            getSupportMenuInflater().inflate(R.menu.settings_manage_profile, menu);                         		
            return true;
        }

		@Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			Context context = getApplicationContext();
			String alias = UtilProfile.getAlias(profile, context);
			mode.setTitle(alias);
    		return true;
		}

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	                      
    		switch (item.getItemId()) {
    	    case R.id.menu_edit:

    	    	//Go to edit profile settings screen;
				Intent intentEditProfile= new Intent(getApplicationContext(), SettingsProfileActivity.class);
				intentEditProfile.putExtra("id", profile.getId());
		        startActivity (intentEditProfile);    	    	
    	    	
    	    	mode.finish();
				break;
				
		    case R.id.menu_delete:
	
		    	//Delete the profile and refresh the list of profiles
		    	settingsService.deleteProfile(profile.getId());
		    	
		    	//Rebuild the profile category again
		    	fillProfilesCategory();
		    	
		    	mode.finish();
				break;
			}
            
            return true;
        }
		
        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
			
    }
}

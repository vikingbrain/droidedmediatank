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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Constants;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.pojo.util.DmtLogger;
import com.vikingbrain.dmt.service.SettingsService;
import com.vikingbrain.dmt.service.util.UtilProfile;
import com.vikingbrain.dmt.view.util.PreferenceCheckBox;
import com.vikingbrain.dmt.view.util.PreferenceEditText;
import com.vikingbrain.dmt.view.util.PreferenceList;

/**
 * Activity for managing settings for profiles.
 * 
 * @author Rafael Iñigo
 */
public class SettingsProfileActivity<PreferencesAdapter> extends AbstractSettingsActivity {

	/** Tag for logging. */
	private static final String TAG = SettingsProfileActivity.class.getSimpleName();

	private static final String NUMERIC_PATTERN = "[-+]?\\d+(\\.\\d+)?";
	private static final int MAX_PORT_NUMBER = 65535;
	
    private static final int TEXT_PORT_DEFAULT = R.string.pref_textPortDefault;
    private static final int TEXT_LLINK_PORT_DEFAULT = R.string.pref_llinkPortDefault;
	
	private Profile profile;

	private SettingsService settingsService;

	private PreferenceEditText name;
	private PreferenceList nmtType;
	private PreferenceEditText ipNmt;
	private PreferenceEditText ftpPort;
	private PreferenceEditText ftpUser;
	private PreferenceEditText ftpPass;
	private PreferenceCheckBox myihomeActive;
	private PreferenceList ftpNmtDriveName;
	private PreferenceCheckBox llinkActive;
	private PreferenceEditText llinkPort;
	private PreferenceEditText ipWebClients;

	private String nameValue = "";
	private String nmtTypeValue = "";
	private String ipNmtValue = "";
	private String ftpPortValue = "";
	private String ftpUserValue = "";
	private String ftpPassValue = "";
	private boolean myihomeActiveValue = true; //default init value, so not null
	private String ftpNmtDriveNameValue = "";
	private boolean llinkActiveValue = true; //default init value, so not null
	private String llinkPortValue = "";	
	private String ipWebClientsValue = "";


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

	/**
	 * Get a profile by its id.
	 * @param id id for the profile
	 * @return the profile
	 */
	private Profile getProfile(long id){
    	Profile profile = settingsService.getProfile(id);
    	return profile;
	}
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	    //ActionBarSherlock
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);		

        // Create the preferences screen here: this takes care of saving/loading, but also contains the ListView adapter, etc.
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(this));

		settingsService = DMTApplication.getSettingsService();
		
	    Bundle b = getIntent().getExtras();
	    if (b != null) {
	    	//Edit a existing profile
	    	long id = b.getLong("id");
	    	DmtLogger.d(TAG, "Editing profile id: " + id);
	    	profile = getProfile(id);
	    } else {
	    	//Screen for creating profile
	    	//Build a profile object with the deault values
	    	profile = UtilProfile.buildProfileDefaultFields();
	    }		

	    nameValue = profile.getName();
		nmtTypeValue = profile.getTypeNmt();
		ipNmtValue = profile.getIpNmt();
		ftpPortValue = profile.getFtpPort();
		ftpUserValue = profile.getFtpUser();
		ftpPassValue = profile.getFtpPassword();
		myihomeActiveValue = profile.isMyihomeActive();
		ftpNmtDriveNameValue = profile.getFtpNmtDriveName();
		llinkActiveValue = profile.isLlinkActive();
		llinkPortValue = profile.getLlinkPort();
		ipWebClientsValue = profile.getIpWebClients();
		
		
		// Create preference objects
		getPreferenceScreen().setTitle(R.string.pref_dmt);

		// Profile optional name
        name = new PreferenceEditText(this);
        name.setTitle(R.string.pref_device_name);
        name.setText(nameValue);
        name.getEditText().setSingleLine();
        name.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER);
        name.setDialogTitle(R.string.pref_device_name);
        name.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(name);

		// NMT model
		nmtType = new PreferenceList(this);		
		nmtType.setTitle(R.string.pref_nmt_type);
		nmtType.setValue(nmtTypeValue);		
		nmtType.setEntries(R.array.nmt_types);
		nmtType.setEntryValues(R.array.nmt_types);
		nmtType.setDialogTitle(R.string.pref_nmt_type);
		nmtType.setOnPreferenceChangeListener(updateHandler);
		getPreferenceScreen().addItemFromInflater(nmtType);

        // nmt IP or address
		ipNmt = new PreferenceEditText(this);
		ipNmt.setTitle(R.string.pref_ftp_address);
		ipNmt.setText(ipNmtValue);
		ipNmt.getEditText().setSingleLine();
		ipNmt.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
		ipNmt.setDialogTitle(R.string.pref_ftp_address);
		ipNmt.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(ipNmt);

        
		//FTP SETTINGS
		// ftpPart title
		PreferenceCategory ftpPart = new PreferenceCategory(this);
		ftpPart.setTitle(R.string.pref_ftp_part);
		getPreferenceScreen().addItemFromInflater(ftpPart);
		
        // Ftp Port
        ftpPort = new PreferenceEditText(this);
        ftpPort.setTitle(R.string.pref_ftp_port);
        ftpPort.setText(ftpPortValue);
        ftpPort.getEditText().setSingleLine();
        ftpPort.getEditText().setInputType(ftpPort.getEditText().getInputType() | EditorInfo.TYPE_CLASS_NUMBER);
        ftpPort.setDialogTitle(R.string.pref_ftp_port);
        ftpPort.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(ftpPort);
		
        //Ftp User
        ftpUser = new PreferenceEditText(this);
        ftpUser.setTitle(R.string.username);
        ftpUser.setText(ftpUserValue);
        ftpUser.getEditText().setSingleLine();
        ftpUser.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_FILTER);
        ftpUser.setDialogTitle(R.string.username);
        ftpUser.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(ftpUser);
        // Ftp Pass
        ftpPass = new PreferenceEditText(this);
        ftpPass.setTitle(R.string.password);
        ftpPass.setText(ftpPassValue);
        ftpPass.getEditText().setSingleLine();
        ftpPass.getEditText().setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        ftpPass.getEditText().setTransformationMethod(new PasswordTransformationMethod());
        ftpPass.setDialogTitle(R.string.password);
        ftpPass.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(ftpPass);

		//FTP Drive Name
		//NMT real ftp root
		ftpNmtDriveName = new PreferenceList(this);
		ftpNmtDriveName.setTitle(R.string.pref_ftp_drive_name);
		ftpNmtDriveName.setValue(ftpNmtDriveNameValue);		
		ftpNmtDriveName.setEntries(R.array.ftp_drive_name_types);
		ftpNmtDriveName.setEntryValues(R.array.ftp_drive_name_values);
		ftpNmtDriveName.setDialogTitle(R.string.pref_ftp_drive_name);
		ftpNmtDriveName.setOnPreferenceChangeListener(updateHandler);
		getPreferenceScreen().addItemFromInflater(ftpNmtDriveName);

		//Stream to Android
		// streamPart title
		PreferenceCategory streamPart = new PreferenceCategory(this);
		streamPart.setTitle(R.string.pref_stream_part);
		getPreferenceScreen().addItemFromInflater(streamPart);
		
		//MyiHome settings
		myihomeActive = new PreferenceCheckBox(this);
		myihomeActive.setTitle(R.string.pref_myihome_name);
		myihomeActive.setKey("myihomeActive_preference");
		myihomeActive.setChecked(myihomeActiveValue);		
		myihomeActive.setSummary(R.string.pref_myihome_name_summary_inicio);      
		myihomeActive.setOnPreferenceChangeListener(updateHandler);
		getPreferenceScreen().addItemFromInflater(myihomeActive);								
		
		//Llink settings
		llinkActive = new PreferenceCheckBox(this);
		llinkActive.setTitle(R.string.pref_llink_name);
		llinkActive.setKey("llinkActive_preference");
		llinkActive.setChecked(llinkActiveValue);		
		llinkActive.setSummary(R.string.pref_llink_name_summary_inicio);      
		llinkActive.setOnPreferenceChangeListener(updateHandler);
		getPreferenceScreen().addItemFromInflater(llinkActive);		
		
        // Llink Port
        llinkPort = new PreferenceEditText(this);
        llinkPort.setTitle(R.string.pref_llink_port);
        llinkPort.setText(llinkPortValue);
        llinkPort.getEditText().setSingleLine();
        llinkPort.getEditText().setInputType(llinkPort.getEditText().getInputType() | EditorInfo.TYPE_CLASS_NUMBER);
        llinkPort.setDialogTitle(R.string.pref_llink_port);
        llinkPort.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(llinkPort);
        //Added the dependency of other preference
        llinkPort.setDependency(llinkActive.getKey());
		        
        // webClientsPart title
		PreferenceCategory webClientsPart = new PreferenceCategory(this);
		webClientsPart.setTitle(R.string.pref_web_clients_part);
		getPreferenceScreen().addItemFromInflater(webClientsPart);
		
        // IP for web clients
		ipWebClients = new PreferenceEditText(this);
		ipWebClients.setTitle(R.string.pref_address_web_clients);
		ipWebClients.setText(ipWebClientsValue);
		ipWebClients.getEditText().setSingleLine();
		ipWebClients.getEditText().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
		ipWebClients.setDialogTitle(R.string.pref_address_web_clients);
		ipWebClients.setOnPreferenceChangeListener(updateHandler);
        getPreferenceScreen().addItemFromInflater(ipWebClients);
        
        
		updateOptionAvailability();
        updateDescriptionTexts();
	}

	
	private OnPreferenceChangeListener updateHandler = new OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
								
			if (preference == name){
				nameValue = (String) newValue;
				profile.setName(nameValue);		
			} else if (preference == nmtType ) {
				nmtTypeValue = (String) newValue;
				profile.setTypeNmt(nmtTypeValue);
			//SECTION FTP
			} else if (preference == ipNmt) {								
				// DO NOT VALIDATE THE IP address because it can be a host name or a domain name
				ipNmtValue = (String) newValue;
				profile.setIpNmt(ipNmtValue);
			} else if (preference == ftpPort) {
				//Empty value is valid port (means the default will be used)
				//If value is not empty it must be a number and be in range, I allow empty so it's shows the default port value		
				if (! isValidPort((String)newValue)){
					Toast.makeText(getApplicationContext(), R.string.error_invalid_port_number, Toast.LENGTH_LONG).show();
				} else {
					ftpPortValue = (String) newValue;
					profile.setFtpPort(ftpPortValue);
				}
			} else if (preference == ftpUser) {
				ftpUserValue = (String) newValue;
				profile.setFtpUser(ftpUserValue);
			} else if (preference == ftpPass) {
				//passValue = (String) newValue;
				ftpPassValue = (String) newValue;
				profile.setFtpPassword(ftpPassValue);				
				
			} else if (preference == myihomeActive ) {
				myihomeActiveValue = ((Boolean) newValue).booleanValue();
				profile.setMyihomeActive(myihomeActiveValue == true ? Constants.NUMBER_ONE : Constants.NUMBER_ZERO);									
				
			} else if (preference == ftpNmtDriveName ) {
				ftpNmtDriveNameValue = (String) newValue;
				profile.setFtpNmtDriveName(ftpNmtDriveNameValue);

				//Actualizo el valor por despu������s se calcula el summary por este valor
				ftpNmtDriveName.setValue(ftpNmtDriveNameValue);
				
			} else if (preference == llinkActive ) {
				llinkActiveValue = ((Boolean) newValue).booleanValue();
				profile.setLlinkActive(llinkActiveValue == true ? Constants.NUMBER_ONE : Constants.NUMBER_ZERO);					


			} else if (preference == llinkPort) {
				//Empty value is valid port (means the default will be used)
				//If value is not empty it must be a number and be in range, I allow empty so it's shows the default port value		
				if (! isValidPort((String)newValue)){
					Toast.makeText(getApplicationContext(), R.string.error_invalid_port_number, Toast.LENGTH_LONG).show();
				} else {
					llinkPortValue = (String) newValue;
					profile.setLlinkPort(llinkPortValue);
				}

				
			//SECTION WEB
			} else if (preference == ipWebClients) {								
				//It's ok if newAddress == "" because it's not mandatory this ip for web clients
				//Ip is valid or is empty 
				ipWebClientsValue = (String) newValue;
				profile.setIpNmt(ipWebClientsValue);
			}

			//Save or update the profile
			if (null == profile.getId()){
				//It's a new profile
				long id = settingsService.saveOrUpdate(profile);
				//Get the object from bbdd to have the id inside the object
				profile = getProfile(id);
			} else {
				//It's an existing profile
				settingsService.saveOrUpdate(profile);				
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
    	
    	//Updates for checkbox
    	myihomeActive.setChecked(myihomeActiveValue == true ? true : false);  
    	llinkActive.setChecked(llinkActiveValue == true ? true : false);
    	
    	//myiHome is not available for some NMT devices
    	if (Constants.NMT_DEVICES_WITHOUT_MYIHOME.contains(nmtTypeValue)){
    		myihomeActive.setEnabled(false);
    	} else {
    		myihomeActive.setEnabled(true);
    	}    	
    }
    
    /**
     * Update view description texts.
     */
    private void updateDescriptionTexts() {

    	//Name
    	name.setSummary("".equals(nameValue) ? getText(R.string.pref_optional_name) : nameValue);
    	
    	if ("".equals(nmtTypeValue)){
    		nmtType.setSummary(R.string.pref_nmt_type_select_suggestion);
    	} else {
    		nmtType.setSummary(nmtTypeValue.toString());
    	}    	
    	    	 
    	//IP NMT
    	ipNmt.setSummary("".equals(ipNmtValue) ? Constants.EMPTY_TEXT : ipNmtValue);
    	ftpPort.setSummary("".equals(ftpPortValue) ? getText(TEXT_PORT_DEFAULT): ftpPortValue);
    	ftpUser.setSummary("".equals(ftpUserValue) ? "": ftpUserValue);
    	
    	if (!myihomeActive.isEnabled()){
    		myihomeActive.setSummary(R.string.myihome_not_available_for_device);
    	} else {
    		myihomeActive.setSummary(R.string.pref_myihome_name_summary_inicio);
    	}
    	    	
    	//ftp nmt drive name. Este hace un getEntry para ver su valor
    	CharSequence textSummaryEnd = "".equals(ftpNmtDriveNameValue) ? "": ftpNmtDriveName.getEntry();
    	String textSummaryStart = getApplicationContext().getResources().getString(R.string.pref_ftp_drive_name_summary_inicio);
    	String textoSumarioNmtReal = textSummaryStart + textSummaryEnd; 
    	ftpNmtDriveName.setSummary((CharSequence)textoSumarioNmtReal);

    	llinkPort.setSummary("".equals(llinkPortValue)? getText(TEXT_LLINK_PORT_DEFAULT): llinkPortValue);

    	CharSequence ipWebText = "".equals(ipWebClientsValue) ? getText(R.string.pref_address_web_clients_summary_ini) : ipWebClientsValue;
    	ipWebClients.setSummary(ipWebText);
    }
	
	/**
	 * If port has value, then it must be number and be in appropriate range.
	 * @param value port to check
	 * @return if it is a valid port
	 */
	private boolean isValidPort(String value){
		boolean isValid = true;
		if (!"".equals(value)){
			if (!isNumeric(value)){
				isValid = false;
			} else if (!isPortValidRange(value)){
				isValid = false;
			}
		}
		return isValid;
	}
	
	/**
	 * Check if provided input is numeric.
	 * @param inputData any text
	 * @return if it is numeric
	 */
	private static boolean isNumeric(String inputData) {
		return inputData.matches(NUMERIC_PATTERN);
	}

	/**
	 * Port number should be between minimum and maximum port values
	 * @param port the port to check
	 * @return if it is in valid port range
	 */
	private static boolean isPortValidRange(String port){
		int MIN_RANGE = Constants.NUMBER_ONE;
		int MAX_RANGE = MAX_PORT_NUMBER;
		boolean isValid = false;
		try {
			int portNumber = Integer.parseInt(port);
			if ( MIN_RANGE <= portNumber
					&& portNumber <= MAX_RANGE){
				isValid = true;
			}
		} catch (NumberFormatException nfe){
			isValid = false;
		}
		return isValid;
	}
	
}

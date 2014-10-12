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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import ca.benow.transmission.model.AddedTorrentInfo;

import com.vikingbrain.dmt.R;
import com.vikingbrain.dmt.core.DMTApplication;
import com.vikingbrain.dmt.pojo.Profile;
import com.vikingbrain.dmt.service.TransmissionService;
import com.vikingbrain.dmt.service.util.TransmissionConnectionData;

/**
 * Activity to control torrents.
 * 
 * @author Rafael Iñigo
 */
public class TorrentActivity extends AbstractProfileChangerActivity {

	private static final String SCHEME_HTTP = "http";
	private static final String SCHEME_MAGNET = "magnet";
	private static final String SCHEME_FILE = "file";
		
	
	private TransmissionService transmissionService;

	private TextView torrentText;
	private static final int MAX_LENGTH_TORRENT_TEXT = 200;
	private TextView hostText;
	private TextView portText;
	private TextView showTransmissionWarning;
	private TextView helpText;
	private ProgressBar mProgressBar;

	//First step upload options
	private LinearLayout actionsFirstStep;
	//Layout with the include for the authentication dialog
	private LinearLayout authenticationLayout;
	//Username and password
	private EditText username;
	private EditText password;
	private CheckBox checkboxStartWhenAdded;
	private Button buttonClose;
	private Button buttonCancel;
	
	//Layout icon and message with status of operation, error, exceptions, etc...
	private LinearLayout statusLayout;
	private TextView statusText;	
	private ImageView statusImage;

	//Second step, operations after upload
	private LinearLayout actionsSecondStep;
	private Button buttonUpload;
	private Button buttonOpenTransmission;
	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		
	    //get the service and init the client
	    transmissionService = DMTApplication.getTransmissionService();

	    //set the view
		setContentView(R.layout.add_torrent);
				
		torrentText = (TextView) findViewById(R.id.torrentText);
		hostText = (TextView) findViewById(R.id.hostText);
		portText = (TextView) findViewById(R.id.portText);
		showTransmissionWarning = (TextView) findViewById(R.id.showTransmissionWarning);		
		helpText = (TextView) findViewById(R.id.helpText);
		mProgressBar = (ProgressBar) findViewById(R.id.scan_progress);
								
		//First step upload options
		actionsFirstStep = (LinearLayout) findViewById(R.id.actionsFirstStep);
		authenticationLayout = (LinearLayout) findViewById(R.id.authenticationLayout);
		//Username and password		
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		
		checkboxStartWhenAdded = (CheckBox)findViewById(R.id.startWhenAdded);
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		buttonUpload = (Button) findViewById(R.id.button_upload);
		
		//Layout icon and message with status of operation, error, exceptions, etc...
		statusLayout = (LinearLayout)findViewById(R.id.status_layout);
		statusImage = (ImageView) findViewById(R.id.statusImage);
		statusText = (TextView) findViewById(R.id.statusText);		
		
		//Second step, operations after upload
		actionsSecondStep = (LinearLayout) findViewById(R.id.actionsSecondStep);		
		buttonOpenTransmission = (Button) findViewById(R.id.button_open_transmission);
		buttonClose = (Button) findViewById(R.id.button_close);
		
		//initialization of checkbox with value from session property of client transmission
		initCheckboxStartWhenAdded();
				
		showTransmissionWarning.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (View.GONE == helpText.getVisibility()){
					helpText.setVisibility(View.VISIBLE);
				} else {
					helpText.setVisibility(View.GONE);
				}
			}
		});
		
		// if button is clicked, close the custom dialog
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Finish the activity
				finish();
			}
		});
		buttonClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Finish the activity
				finish();
			}
		});

		buttonOpenTransmission.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openTransmissionWebClient();
				//Finish the activity
				finish();
			}
		});		
		
		
		//Get list of profiles
		List<Profile> profiles = getSettingsService().getProfiles();

		//Load the navigation list with the available profiles
		loadProfileNavigation(profiles);
		
		//Get and display information for transmission
		//Fill the user screen with all the information and possible empty values or errors
		getAndDisplayTransmissionConnectionData();		

		//handle the torrent specifying onclicks for the buttons
	    handleIntent(this.getIntent());	    
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		super.onNavigationItemSelected(itemPosition, itemId);
		
		//Get and display information for transmission
		//Fill the user screen with all the information and possible empty values or errors
		getAndDisplayTransmissionConnectionData();		
		
        return true;
	}

	/**
	 * Init the checkbox property start when added with the information obtained from the service
	 */
	private void initCheckboxStartWhenAdded(){		
		//It works with new GetStartWhenAddedTask().execute(); but it
		//doesn't save state in the server so it's better to initialize it with false
		checkboxStartWhenAdded.setChecked(false);
		checkboxStartWhenAdded.setVisibility(View.VISIBLE);
	}

	/**
	 * Get and display information for transmission.
	 * Fill the user screen with all the information and possible empty values or errors.
	 */
	private void getAndDisplayTransmissionConnectionData(){
		//Get and display information for transmission		
		TransmissionConnectionData transmissionConnectionData = transmissionService.getTransmissionConnectionData();

		//Fill the user screen with all the information and possible empty values or errors
		String host = transmissionConnectionData.getHost();
		int port = transmissionConnectionData.getPort();

		//show info host and port
		if ("".equals(host)){					
			hostText.setText(getString(R.string.connection_host_notset));
		} else {
			hostText.setText(getString(R.string.connection_host, host));
		}
		portText.setText(getString(R.string.connection_port, port));
		
		boolean isAuthentication = transmissionConnectionData.isAuthentication();
		if (isAuthentication){
			//Show the authentication layout
			authenticationLayout.setVisibility(View.VISIBLE);
			//Fill the username and password
			username.setText(transmissionConnectionData.getUser());
			password.setText(transmissionConnectionData.getPassword());
		} else {
			authenticationLayout.setVisibility(View.GONE);
		}
		
		String textWrongData = transmissionConnectionData.getTextWrongData();
		if (null != textWrongData
				&& !"".equals(textWrongData)){			
			showStatusError(textWrongData);
		}
	}
	
	private void openTransmissionWebClient(){
		String transmissionWebUrl = transmissionService.getTransmissionWebUrl();
    	//Opens in Custom Web Broswer	
    	//Abrimos el navegador en modo start_ini para que coga esta direccion
    	//y no la ultima que visitara
    	CustomWebView.START_INI = true;
    	Intent i = new Intent(this, CustomWebView.class);     
    	i.putExtra("urlAsked", transmissionWebUrl);
		startActivity(i);
	}
	
    public void handleIntent(final Intent startIntent) {    				
    	
    	// Handle new intents that come from either a regular application startup, a startup from a 
    	// new intent or a new intent being send with the application already started
    	if (startIntent != null 
    			&& startIntent.getData() != null){
        		
    			// Intent should have some Uri data pointing to a single torrent
	    		final String data = startIntent.getDataString();
	    		if (data != null && startIntent.getData() != null && startIntent.getData().getScheme() != null) {
		    		if (startIntent.getData().getScheme().equals(SCHEME_HTTP)) {
			        	// From a global intent to add a .torrent file via URL (maybe form the browser)
		    			String title = data.substring(data.lastIndexOf("/"));
		    			
		    			String shorterText = getShorterText(title);
		    			torrentText.setText(shorterText);		    			
		    			buttonUpload.setOnClickListener(new View.OnClickListener() {
		    				public void onClick(View view) {
		    					new UploadTorrentTask().execute(data, SCHEME_HTTP);
		    				}
		    			});
		    			
		    		} else if (startIntent.getData().getScheme().equals(SCHEME_MAGNET)) {
		    			String shorterText = getShorterText(startIntent.getDataString());
		    			torrentText.setText(shorterText);
		    			// From a global intent to add a magnet link via URL (usually from the browser)		    			
		    			buttonUpload.setOnClickListener(new View.OnClickListener() {
		    				public void onClick(View view) {
		    					new UploadTorrentTask().execute(data, SCHEME_MAGNET);
		    				}
		    			});
		    			
		    		} else if (startIntent.getData().getScheme().equals(SCHEME_FILE)) {
		    			String shorterText = getShorterText(startIntent.getDataString());
		    			torrentText.setText(shorterText);
		    			// From a global intent to add via the contents of a local .torrent file (maybe form a file manager)
		    			buttonUpload.setOnClickListener(new View.OnClickListener() {
		    				public void onClick(View view) {
		    					new UploadTorrentTask().execute(data, SCHEME_FILE);
		    				}
		    			});		    			
		    		}
	    		}
	    		
    	}
    }    

    private final String getShorterText(String text){    	
    	if (text.length() > MAX_LENGTH_TORRENT_TEXT){
    		StringBuilder sb = new StringBuilder(text.substring(0, MAX_LENGTH_TORRENT_TEXT));
    		sb.append("...");
    		text = sb.toString();
    	}
    	return text;
    }
    
	private class UploadTorrentTask extends AsyncTask<Object, Void, AddedTorrentOrException>{
		
    	protected String _data;
    	protected String _scheme;
    	
		@Override
		protected void onPreExecute() {
			actionsFirstStep.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
			
			//Load possible changes to username and password
			String newUsername = username.getText().toString();
			String newPassword = password.getText().toString();
			
			//Init the transmission client
			transmissionService.initClient(newUsername, newPassword);			
		}

		@Override
		protected AddedTorrentOrException doInBackground(Object... params) {
			//Bugfix: Trying to solve way the onPostExecute was not called and the progress was showed forever
			AddedTorrentOrException addedTorrentOrException = new AddedTorrentOrException();
			try {
				_data = (String) params[0];
				_scheme = (String) params[1];
			
				//Perform the upload of the torrent				
				AddedTorrentInfo addedTorrent = null;
				if (_scheme.equals(SCHEME_HTTP)
						|| _scheme.equals(SCHEME_MAGNET)) {
					addedTorrent = addTorrentByUrlOrMagnetUrl(_data);
				} else if (_scheme.equals(SCHEME_FILE)) {
					addedTorrent = addTorrentByFile(_data);
				}
				addedTorrentOrException.setAddedTorrent(addedTorrent);
			} catch (Exception e) {
				addedTorrentOrException.setException(e);
			}
			return addedTorrentOrException;
		}

		@Override
		protected void onPostExecute(AddedTorrentOrException addedTorrentOrException){ //result
			mProgressBar.setVisibility(View.GONE);
			//Process the result of operation
			processOperationResult(addedTorrentOrException);			
		}
	};

	
	/**
     * Open http://XXX.torrent or magnet:?xt=urn:btih:
     * @param url
     * @param title
	 * @throws Exception 
     */
	private AddedTorrentInfo addTorrentByUrlOrMagnetUrl(String url) throws Exception {

		AddedTorrentInfo addedTorrent = transmissionService.addTorrent(url, getCheckboxValueStartWhenAdded());
		return addedTorrent;
	}

	/**
	 * It opens files like TorrentExample.torrent
	 * @param fileUri the file uri
	 * @return the added torrent information
	 * @throws Exception exception in the operation
	 */
	private AddedTorrentInfo addTorrentByFile(String fileUri) throws Exception {
		AddedTorrentInfo addedTorrent = null;					

		//Read the torrent file uri data
		InputStream in = new FileInputStream(new File(URI.create(fileUri)));

		//Add the torrent
		addedTorrent = transmissionService.addTorrent(in, getCheckboxValueStartWhenAdded());
		return addedTorrent;
	}    
	
	private void processOperationResult(AddedTorrentOrException addedTorrentOrException){
		actionsSecondStep.setVisibility(View.VISIBLE);		
		
		if (null != addedTorrentOrException.getAddedTorrent()){
			String torrentName = addedTorrentOrException.getAddedTorrent().getName();
			
			if (getCheckboxValueStartWhenAdded()){
				String successfullyUploadedStarted = getString(R.string.successfully_uploaded_started, torrentName);
				showStatusSuccessful(successfullyUploadedStarted);
			} else {
				String successfullyUploaded = getString(R.string.successfully_uploaded, torrentName);
				showStatusSuccessful(successfullyUploaded);
			}
			
		} else {
			//There is not an added torrent
			Exception exception = addedTorrentOrException.getException();
			//Show the message in the layout
			showStatusException(exception);			
		}
	}
	
	private void showStatusSuccessful(String textMessage){
		statusLayout.setVisibility(View.VISIBLE);
		
		statusImage.setImageResource(R.drawable.icon_ok_small);			
		statusText.setTextColor(getResources().getColor(R.color.verdeOscuro));
		statusText.setText(textMessage);
	}

	private void showStatusError(String textError){
		statusLayout.setVisibility(View.VISIBLE);
		
		statusImage.setImageResource(R.drawable.icon_error_small);
		statusText.setTextColor(Color.RED);
		statusText.setText(textError);
	}

	private void showStatusException(Exception exception){
		statusLayout.setVisibility(View.VISIBLE);
		
		statusImage.setImageResource(R.drawable.icon_error_small);
		statusText.setTextColor(Color.RED);
		if (null != exception.getMessage()){				
			statusText.setText(exception.getMessage()); 								
		} else {
			//exception without message
			statusText.setText(R.string.error_uploading);
		}				
	}
	
	private boolean getCheckboxValueStartWhenAdded(){
		return checkboxStartWhenAdded.isChecked() == true ? true : false;  
	}
	
	private class AddedTorrentOrException{
		AddedTorrentInfo addedTorrent = null;
		Exception exception = null;

		public final AddedTorrentInfo getAddedTorrent() {
			return addedTorrent;
		}
		public final void setAddedTorrent(AddedTorrentInfo addedTorrent) {
			this.addedTorrent = addedTorrent;
		}
		public final Exception getException() {
			return exception;
		}
		public final void setException(Exception exception) {
			this.exception = exception;
		}		
	}
}